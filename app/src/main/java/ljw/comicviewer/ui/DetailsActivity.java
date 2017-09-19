package ljw.comicviewer.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.util.DialogUtil;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.ui.fragment.ChaptersFragment;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;

public class DetailsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, ComicService.RequestCallback {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private boolean isLoaded = false;
    private Comic comic = new Comic();
    private String comic_id;
    private Map<Integer,ChaptersFragment> chaptersFragment_map = new HashMap<>();//0单行本,1单话，2其他
    private int[] fragmentId = {R.id.details_fragment0,R.id.details_fragment1,R.id.details_fragment2};
    private int[] typeTextId = {R.id.detail_type0,R.id.detail_type1,R.id.detail_type2};
    private int TYPE_MAX = 3;

    @BindView(R.id.details_scroll_container)
    SwipeRefreshLayout details_container;
    @BindView(R.id.details_cover)
    ImageView img_cover;
    @BindView(R.id.details_info_arrow)
    ImageView img_arrow;
    @BindView(R.id.title)
    TextView txt_title;
    @BindView(R.id.details_author)
    TextView txt_author;
    @BindView(R.id.details_tag)
    TextView txt_tag;
    @BindView(R.id.details_updateStatus)
    TextView txt_updateStatus;
    @BindView(R.id.details_updateDate)
    TextView txt_updateDate;
    @BindView(R.id.details_info)
    TextView txt_info;
    @BindView(R.id.details_status)
    TextView txt_status;
    @BindView(R.id.details_score)
    TextView txt_score;
    @BindView(R.id.details_chapters)
    LinearLayout details_chapters;
//    @BindView(R.id.webview_details)
//    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_details);
        //全屏化,设置头部padding-top 为状态栏高度
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = DisplayUtil.getStatusBarHeight(context);
        findViewById(R.id.activity_details).setPadding(0,statusBarHeight==-1 ? 50:statusBarHeight,0,0);

        //view绑定代码生成
        ButterKnife.bind(this);

        //隐藏加载前界面
        details_container.setVisibility(View.GONE);
        details_container.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        details_container.setOnRefreshListener(this);//增加刷新方法

        //设置标题栏的标题
        String title = (String) getIntent().getExtras().get("title");
        txt_title.setText(title);

        //预先设置comic的id和评分
        comic_id = (String) getIntent().getExtras().get("id");
        String score = (String) getIntent().getExtras().get("score");
        comic.setId(comic_id);
        comic.setScore(score);
        //加载数据
        loadComicInformation();

        findViewById(R.id.details_instruction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (txt_info.getMaxLines()==2){
                txt_info.setMaxLines(999);
                img_arrow.setImageResource(R.drawable.arrowhead_up);
            }else{
                txt_info.setMaxLines(2);
                img_arrow.setImageResource(R.drawable.arrowhead_down);
            }
            }
        });


        for (int i = 0 ; i < TYPE_MAX ; i++){
            ChaptersFragment chaptersFragment = new ChaptersFragment();
            chaptersFragment_map.put(i,chaptersFragment);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0 ; i<TYPE_MAX ; i++){
            fragmentManager.beginTransaction()
                    .replace(fragmentId[i], chaptersFragment_map.get(i)).commit();
        }


//        Log.d(TAG, "onCreate: "+syncCookie(Global.MANHUAGUI_DOMAIN,"country=US"));
//        webview = (WebView)findViewById(R.id.webview_details);
//        webview.getSettings().setJavaScriptEnabled(true);
//        webview.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
//        webview.loadUrl(ComicService.get().getHost()+"/comic/"+8788+"/");
//        webview.setWebViewClient(new MyWebView());
    }

    //加载数据
    public void loadComicInformation(){
        ComicService.get().getComicInfo(this,comic_id);//18X id:"8788");"16058"
    }

    //加载封面
    public void getCover(){
        ComicService.get().getImage(this,comic.getImageUrl(),-1);
    }



    //根据类型分发章节
    private void ChaptersDistribute(List<Chapter> list){
        Map<Integer,List<Chapter>> map = new HashMap<>();
        for(int i=0 ; i< TYPE_MAX ; i++){
            List<Chapter> cList = new ArrayList<>();
            map.put(i,cList);
        }
        for (Chapter chapter:list) {
            int type = chapter.getType();
            map.get(type).add(chapter);
        }
        for(int i = 0 ; i<TYPE_MAX ;i++){
            chaptersFragment_map.get(i).addChapters(map.get(i));
            chaptersFragment_map.get(i).setComicName(comic.getName());
            if(map.get(i).size()>0){
                //显示章节类型文字
                findViewById(typeTextId[i]).setVisibility(View.VISIBLE);
            }
        }

    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }

    @Override
    public void onRefresh() {
        for(int i = 0 ; i<TYPE_MAX ;i++){
            chaptersFragment_map.get(i).clearChapters();
        }
        details_container.setRefreshing(true);
        // 获取对象，重新获取当前目录对象
        loadComicInformation();
        //2秒刷新事件
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {details_container.setRefreshing(false);
            }
        }, 2000);
    }

    //TODO:网络请求，更新UI
    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_INFO:
                ComicFetcher.getComicDetails(data.toString(),comic);
                ComicFetcher.getComicChapters(data.toString(),comic);
                getCover();
                txt_author.setText(comic.getAuthor());
                txt_tag.setText(comic.getTag());
                txt_updateDate.setText("更新于"+comic.getUpdate());
                txt_updateStatus.setText(comic.getUpdateStatus());
                txt_status.setText(comic.isEnd()?"已完结":"连载中");
                txt_info.setText(comic.getInfo());
                txt_score.setText(comic.getScore());
                ChaptersDistribute(comic.getChapters());
                if (comic.isBan()){
                    DialogUtil.showBottomDialog(context);
                }
                //显示详细界面
                details_container.setVisibility(View.VISIBLE);
                details_container.setRefreshing(false);
                Log.d(TAG, "onResponse: "+comic.toString());
                break;
            case Global.REQUEST_COMICS_IMAGE:
                CallBackData callBackData = (CallBackData) data;
                Bitmap cover = (Bitmap) callBackData.getObj();
                if(cover==null){
                    img_cover.setImageResource(R.drawable.img_load_failed);
                }else{
                    img_cover.setImageBitmap(cover);
                }
                Log.d(TAG,callBackData.getMsg());
                break;
        }
    }

    @Override
    public void onError(String msg,String what) {
        Log.e(TAG, "Error: " + msg);
    }
}
