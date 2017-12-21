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
        @Headers("Cookie: country=HK")
        @GET("{path}")
        Call<String> getHTML(@Path(value="path",encoded=true) String path);
    }
}
