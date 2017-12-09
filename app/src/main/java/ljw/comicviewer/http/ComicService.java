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
    private static FilterStore filterStore;
    private static String host ;;

    private ComicService(){}

    public static ComicService get(){
        if(comicService==null){
            comicService = new ComicService();
        }
        host = RuleStore.get().getHost();
        filterStore = FilterStore.get();
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

    //获得网页源码（请求核心代码）
    private Call<String> getHTML(String path){
        return getService(host).getHTML(path);
    }
    public Call getHTML(final RequestCallback requestCallback, final String what, String path){
        Call<String> call = ComicService.get().getHTML(path);
        Log.d(TAG, what+": " + call.request().toString());
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
     * url参数解析 有页数
     * @param requestCallback
     * @param what
     * @param oPath  含有参数的地址 主要有{type:}，{page:}
     * @param page   当前页数
     * @return
     */
    public Call<String> getHTML(final RequestCallback requestCallback,final String what,String oPath,int page){
        String path = oPath.replaceAll("\\{page:.*?\\}", page + "");
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
        return getHTML(requestCallback,what,path);
    }

    /**
     * url参数解析 有页数,有替换文字
     * @param requestCallback 返回数据监听
     * @param what  请求标识
     * @param oPath 含参地址{page:},{keyword:}
     * @param str 搜索的关键词
     * @param page 当前页数
     * @return
     */
    public Call<String> getHTML(final RequestCallback requestCallback, final String what, String oPath, String str, int page){
        String path = oPath.replaceAll("\\{page:.*?\\}", page + "");
        if (StringUtil.isExits("\\{keyword:.*?\\}", oPath)) {
            path = path.replaceAll("\\{keyword:.*?\\}",str);
        }
        if(StringUtil.isExits("\\{author:.*?\\}", oPath)){
            path = path.replaceAll("\\{author:.*?\\}",str);
        }
        return getHTML(requestCallback,what,path);
    }

    /**
     * url参数解析 有替换文字
     * @param requestCallback
     * @param what
     * @param oPath
     * @param str
     * @return
     */
    public Call<String> getHTML(final RequestCallback requestCallback, final String what, String oPath, String str){
        String path = oPath;
        if (StringUtil.isExits("\\{comic:.*?\\}", oPath)) {
            path = path.replaceAll("\\{comic:.*?\\}",str);
        }
        return getHTML(requestCallback,what,path);
    }

    //自定义回调接口
    public interface RequestCallback<T>{
        //data为返回的数据，what是什么请求，用于分辨是谁的请求结果用于有多个请求同时时的处理。
        void onFinish(T data,String what);

        void onError(String msg,String what);
    }

}
