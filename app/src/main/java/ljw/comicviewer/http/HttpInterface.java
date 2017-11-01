package ljw.comicviewer.http;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by ljw on 2017-08-23 023.
 */

public class HttpInterface {
    public interface ComicRequestServices{
        //获得漫画列表接口
        @Headers("Cookie: country=HK")
        @GET("list/index_p{page}.html")
        Call<String> getList(@Path(value="page",encoded=true) int page);

        //获得漫画详细页网页源码
        @Headers("Cookie: country=HK")
        @GET("comic/{id}/")
        Call<String> getDetails(@Path(value = "id",encoded = true) String id);

        @Headers("Cookie: country=HK")
        @GET("s/{keyword}_p{page}.html")
        Call<String> getSearch(@Path(value = "keyword",encoded = true) String keyword,@Path(value = "page",encoded = true) String page);

        @Headers("Cookie: country=HK")
        @GET("{path}")
        Call<String> getHTML(@Path(value="path",encoded=true) String path);

    }
}
