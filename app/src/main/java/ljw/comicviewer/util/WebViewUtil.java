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
        String newCookie = "[未修改]cookie: ";
        CookieManager cookieManager = CookieManager.getInstance();
        if(cookieManager.getCookie(url)==null || !cookieManager.getCookie(url).contains(cookie)){
            cookieManager.setCookie(url, cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
            newCookie = "[已修改]cookie: ";
        }
        newCookie += cookieManager.getCookie(url);
        return newCookie;
    }
}
