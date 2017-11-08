package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.ManhuaguiComicInfo;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.others.MyWebView;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.WebViewUtil;

public class ReadViewerLoadingActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName()+"----";
    private String comic_id,comic_name,chapter_id,chapter_name;
    private List<String> imgUrls = new ArrayList<>();
    private int currPos;
    private int tryTime = 0;
    private Context context;
    private Object animMode;
    @BindView(R.id.webview_in_loading)
    WebView webView;
    @BindView(R.id.btn_refresh)
    ImageView refresh;
    @BindView(R.id.load_fail)
    LinearLayout load_fail;
    @BindView(R.id.loading)
    LinearLayout loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_viewer_loading);
        context = this;
        ButterKnife.bind(this);
        comic_id = (String) getIntent().getExtras().get("comic_id");
        comic_name = (String) getIntent().getExtras().get("comic_name");
        chapter_id = (String) getIntent().getExtras().get("chapter_id");
        chapter_name = (String) getIntent().getExtras().get("chapter_name");
        currPos = (int) getIntent().getExtras().get("position");
        animMode = getIntent().getExtras().get("anim_mode");
        if(animMode==null){
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }

        //破解屏蔽
        WebViewUtil.syncCookie(context, Global.MANHUAGUI_DOMAIN,"country=US");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
        webView.loadUrl(Global.MANHUAGUI_HOST+"/comic/"+comic_id+"/"+chapter_id+"/");
        webView.setWebViewClient(new MyWebView());

        getInfo();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            load_fail.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            tryTime = 0;
            getInfo();
            }
        });
    }

    //获得信息
    public void getInfo(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            webView.evaluateJavascript("cInfo;", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                Log.d(TAG,"debug!!="+s);
                if (s.equals("null")){
                    //加载失败
                    if(tryTime>=3){
                        load_fail.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }else{
                        tryTime++;
                        getInfo();
                    }
                }else{
                    ManhuaguiComicInfo info = ComicFetcher.parseCurrentChapter(s);
                    for(int i = 0 ; i < info.getFiles().size() ; i++){
                        imgUrls.add(Global.MANHUAGUI_IMAGE_HOST+info.getPath()+info.getFiles().get(i));
                    }
                    gotoReadView();
                    return;
                }
                }
            });
            }
        }, 1500);
    }


    public void gotoReadView(){
        Intent intent = new Intent(context,ReadViewerActivity.class);
        intent.putExtra("comic_id",comic_id);
        intent.putExtra("comic_name",comic_name);
        intent.putExtra("chapter_id",chapter_id);
        intent.putExtra("chapter_name",chapter_name);
        intent.putExtra("position",currPos);
        intent.putExtra("urls", DisplayUtil.strListToArray(imgUrls));
        startActivity(intent);

        if(animMode != null){
            switch ((int) animMode){
                case Global.LEFT:
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    break;
                case Global.RIGHT:
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    break;
            }
        }else {
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();

    }
}
