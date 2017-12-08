package ljw.comicviewer.http;

import android.util.Log;

import ljw.comicviewer.Global;
import ljw.comicviewer.store.FilterStore;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.util.StringUtil;
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
    private static String host ;//= Global.MANHUAGUI_HOST;

    private ComicService(){}

    public static ComicService get(){
        if(comicService==null){
            comicService = new ComicService();
        }
        host = RuleStore.get().getHost();
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

    //获得网页源码
    private Call<String> getHTML(String path){
        return getService(host).getHTML(path);
    }
    public Call getHTML(final RequestCallback requestCallback , String path, final String what){
        Call<String> call = ComicService.get().getHTML(path);
        Log.d(TAG, "getHTML: " + call.request().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    requestCallback.onFinish(response.body().toString(),what);
                }else {
                    requestCallback.onError(what +":获得response.body()为null，可能是代码失效！",what);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                requestCallback.onError(what + ":网络请求失败！",what);
            }
        });
        return call;
    }

    /**
     *
     * @param oPath  含有参数的地址
     * {type:} 匹配规则 主要有type，page
     * @param page   当前页数
     * @return
     */
    public Call<String> getHTML(final RequestCallback requestCallback,final String what,String oPath,int page){
        String path = oPath.replaceAll("\\{page:.*?\\}", page + "");
        FilterStore filterStore = FilterStore.get();
        if (StringUtil.isExits("\\{type:.*?\\}", oPath)) {
            String typeStr;
            if(what.equals(Global.REQUEST_COMIC_NEWADD)){
                typeStr = "";
            }else{
                typeStr = StringUtil.join(filterStore.getFilterStatus(), filterStore.getSeparate());
                if(filterStore.getEndStr()!=null && !typeStr.equals(""))
                    typeStr += filterStore.getEndStr();
                Log.d(TAG, "getHTML: " + typeStr);
            }
            path = path.replaceAll("\\{type:.*?\\}",typeStr);
        }
        return getHTML(requestCallback,path,what);
    }

    //获得指定页数的漫画列表对象
    private Call<String> getList(int page){return getService(host).getList(page);}
    public Call getListItems(final RequestCallback requestCallback,int page){
        Call<String> call = ComicService.get().getList(page);
        final String what = Global.REQUEST_COMIC_NEWADD;
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
        return call;
    }


    //加载漫画数据
    private Call<String> getDetails(String id){return getService(host).getDetails(id);}
    public Call getComicInfo(final RequestCallback requestCallback, String comic_id){
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
        return call;
    }

    private Call<String> getSearch(String keyword,String page){return getService(host).getSearch(keyword,page);}
    //搜索页面
    public Call getComicSearch(final RequestCallback requestCallback , String keyword , int page){
        Call<String> call = ComicService.get().getSearch(keyword,page+"");
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
        return call;
    }

    //更新漫画页面
    public Call getUpdateList(final RequestCallback requestCallback , int days){
        String path = RuleStore.get().getLatestRule().get("url")+"/d"+days+".html";
        Call<String> call = ComicService.get().getHTML(path);
        final String what = Global.REQUEST_COMICS_UPDATE;
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
                requestCallback.onError("getUpdateList()网络请求失败！",what);
            }
        });
        return call;
    }

    //自定义回调接口
    public interface RequestCallback<T>{
        //data为返回的数据，what是什么请求，用于分辨是谁的请求结果用于有多个请求同时时的处理。
        void onFinish(T data,String what);

        void onError(String msg,String what);
    }

}
