package ljw.comicviewer.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import ljw.comicviewer.ui.Global;
import ljw.comicviewer.bean.CallBackData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by ljw on 2017-08-23 023.
 * 当要返回多个数据是用CallbackData
 */

public class ComicService {
    private static final String TAG ="ComicService----";
    private static ComicService comicService;
    private String host = Global.MANHUAGUI_HOST;
    private String coverHost = Global.MANHUAGUI_COVER;
    private CallBackData callBackData;


    public static ComicService get(){
        if(comicService==null){
            comicService = new ComicService();
        }
        return comicService;
    }


    private Retrofit getRetrofit(String host){
        return new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    private HttpInterface.ComicRequestServices getService(String host){
        return getRetrofit(host).create(HttpInterface.ComicRequestServices.class);
    }

    private Call<String> getHTML(String path){
        return getService(host).getHTML(path);
    }

    private Call<String> getList(int page){
        return getService(host).getList(page);
    }

    private Call<String> getDetails(String id){
        return getService(host).getDetails(id);
    }

    private Call<ResponseBody> getCover(String url){
        return getService(coverHost).getCover(url);//.replace(coverHost,""));
    }

    //获得指定页数的漫画列表对象
    public void getListItems(final RequestCallback requestCallback,int page){
        Call<String> call = ComicService.get().getList(page);
        //网络请求回馈
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    requestCallback.onFinish(response.body().toString(),Global.REQUEST_COMICS_LIST);
                }else {
                    requestCallback.onError("获得response.body()为null，可能是代码失效！");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                requestCallback.onError("网络请求失败");
            }
        });
    }

    //获得漫画图片相关
    public void getImage(final RequestCallback requestCallback,
                         String url, final int number) {//url = comicList.get(i).getImageUrl()
        Call<ResponseBody> call = ComicService.get().getCover(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String msg = (number==-1?"封面":"图片"+number)+"加载";
                callBackData = new CallBackData();
                callBackData.setArg1(number);
                Log.d(TAG, (number==-1?"封面":"图片"+number)+"请求的onResponse: " + call.request().toString());
                if (response.body() == null) {
                    callBackData.setObj(null);
                    msg+="失败";
                } else {
                    Bitmap bCover = BitmapFactory.decodeStream(response.body().byteStream());
                    callBackData.setObj(bCover);
                    msg += (bCover==null ?"失败" :"成功");
                }
                callBackData.setMsg(msg);
                requestCallback.onFinish(callBackData,Global.REQUEST_COMICS_IMAGE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                requestCallback.onError(number + "加载失败,网络请求失败！");
            }
        });
    }

    //加载漫画数据
    public void getComicInfo(final RequestCallback requestCallback, String comic_id){
        Call<String> call = ComicService.get().getDetails(comic_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    requestCallback.onFinish(response.body().toString(),Global.REQUEST_COMICS_INFO);
                }else {
                    requestCallback.onError("获得response.body()为null，可能是代码失效！");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                requestCallback.onError("网络请求失败！");
            }
        });
    }



    //自定义回调接口
    public interface RequestCallback<T>{
        //data为返回的数据，what是什么请求，用于分辨是谁的请求结果用于有多个请求同时时的处理。
        void onFinish(T data,String what);

        void onError(String msg);
    }

}
