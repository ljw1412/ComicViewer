package ljw.comicviewer.others;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ljw on 2017-08-23 023.
 */

public class MyWebView extends WebViewClient{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}

