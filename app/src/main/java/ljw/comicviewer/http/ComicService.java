package ljw.comicviewer.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import ljw.comicviewer.Global;
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

    private ComicService(){}

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

    //获得指定页数的漫画列表对象
    private Call<String> getList(int page){return getService(host).getList(page);}
    public void getListItems(final RequestCallback requestCallback,int page){
        Call<String> call = ComicService.get().getList(page);
        final String what = Global.REQUEST_COMICS_LIST;
        //网络请求回馈
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    requestCallback.onFinish(response.body().toString(),what);
                }else {
                    requestCallback.onError("获得response.body()为null，可能是代码失效！",what);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                requestCallback.onError("getList()网络请求失败",what);
            }
        });
    }


    //加载漫画数据
    private Call<String> getDetails(String id){return getService(host).getDetails(id);}
    public void getComicInfo(final RequestCallback requestCallback, String comic_id){
        Call<String> call = ComicService.get().getDetails(comic_id);
        final String what = Global.REQUEST_COMICS_INFO;
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    requestCallback.onFinish(response.body().toString(),what);
                }else {
                    requestCallback.onError("获得response.body()为null，可能是代码失效！",what);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                requestCallback.onError("getDetails()网络请求失败！",what);
            }
        });
    }

    private Call<String> getSearch(String keyword){return getService(host).getSearch(keyword);}
    //搜索页面
    public void getComicSearch(final RequestCallback requestCallback , String keyword){
        Call<String> call = ComicService.get().getSearch(keyword);
        final String what = Global.REQUEST_COMICS_SEARCH;
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    requestCallback.onFinish(response.body().toString(),what);
                }else {
                    requestCallback.onError("获得response.body()为null，可能是代码失效！",what);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                requestCallback.onError("getComicSearch()网络请求失败！",what);
            }
        });
    }


    //自定义回调接口
    public interface RequestCallback<T>{
        //data为返回的数据，what是什么请求，用于分辨是谁的请求结果用于有多个请求同时时的处理。
        void onFinish(T data,String what);

        void onError(String msg,String what);
    }

}
