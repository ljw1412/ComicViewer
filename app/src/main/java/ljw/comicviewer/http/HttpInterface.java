package ljw.comicviewer.http;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by ljw on 2017-08-23 023.
 */

public class HttpInterface {
    public interface ComicRequestServices{
        @Headers("Cookie: country=HK")
        @GET
        Call<String> getHTML(@Url String url);
    }
}
