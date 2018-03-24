package ljw.comicviewer.util;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * WebView工具类
 */

public class WebViewUtil {
    //同步修改webview的cookie
    public static String syncCookie(Context context,String url, String cookie) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        String newCookie = "cookie: ";
        CookieManager cookieManager = CookieManager.getInstance();
        String[] keyValues = cookie.split(";");
        for(String kv : keyValues){
            cookieManager.setCookie(url, kv);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
        }
//        newCookie = "[已修改]cookie: ";
//        cookieManager.removeAllCookies(null);
//        String webCookie;
//        if((webCookie = cookieManager.getCookie(url))==null){
//            !cookieManager.getCookie(url).contains(cookie)
//        }
        newCookie += cookieManager.getCookie(url);
        return newCookie;
    }
}
