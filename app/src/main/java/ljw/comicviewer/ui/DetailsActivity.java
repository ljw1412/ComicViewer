package ljw.comicviewer.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Author;
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.bean.History;
import ljw.comicviewer.db.CollectionHolder;
import ljw.comicviewer.db.HistoryHolder;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.others.BottomDialog;
import ljw.comicviewer.others.MyWebView;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.fragment.ChaptersFragment;
import ljw.comicviewer.util.DialogUtil;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.SnackbarUtil;
import ljw.comicviewer.util.WebViewUtil;
import retrofit2.Call;

public class DetailsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, ComicService.RequestCallback {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Activity context;
    private boolean isLoaded = false;
    private Comic comic = new Comic();
    private String comic_id;
    private Map<Integer,ChaptersFragment> chaptersFragment_map = new HashMap<>();//0单行本,1单话，2其他
    Map<Integer,List<Chapter>> chapterTypeMap = new HashMap<>();
    private int[] fragmentId = {R.id.details_fragment0,R.id.details_fragment1,R.id.details_fragment2};
    private int[] typeTextId = {R.id.detail_type0,R.id.detail_type1,R.id.detail_type2};
    private int TYPE_MAX = 3;
    private boolean like = false;
    Call call_loadComicInformation;
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
    @BindView(R.id.details_instruction)
    LinearLayout instruction;
    @BindView(R.id.detail_error)
    TextView txtError;
    @BindView(R.id.details_main)
    ScrollView viewMain;
    @BindView(R.id.webview_details)
    WebView webview;
    @BindView(R.id.author)
    LinearLayout view_author;
    @BindView(R.id.tag)
    LinearLayout view_tag;
    @BindView(R.id.status)
    LinearLayout view_status;
    @BindView(R.id.score)
    LinearLayout view_score;
    @BindView(R.id.updateStatus)
    LinearLayout view_updateStatus;
    @BindView(R.id.updateDate)
    LinearLayout view_updateDate;
    @BindView(R.id.btn_add_collection)
    LinearLayout btn_like;
    @BindView(R.id.icon_collection)
    ImageView btn_like_icon;
    @BindView(R.id.txt_add_collection)
    TextView btn_like_txt;
    @BindView(R.id.btn_to_reading)
    LinearLayout btn_read;
    @BindView(R.id.icon_to_reading)
    ImageView btn_read_icon;
    @BindView(R.id.txt_to_reading)
    TextView btn_read_txt;
    @BindView(R.id.details_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.details_authors_view)
    LinearLayout view_authors;

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

        initViewAndData();
        initChapterFragment();
        initListener();
    }

    private void initViewAndData(){
        //隐藏加载前界面
        viewMain.setVisibility(View.GONE);
        details_container.setRefreshing(true);
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
        comic.setComicId(comic_id);
        comic.setScore(score);

        //加载数据
        loadComicInformation();
        updateLikeStatus();
        updateReadStatus();

    }

    private void initListener(){
        //标题
        txt_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(txt_title.getText());
                builder.setTitle(R.string.dialog_title);
                builder.setNegativeButton(R.string.dialog_btn_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", txt_title.getText());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        SnackbarUtil.newAddImageColorfulSnackar(
                                coordinatorLayout, getString(R.string.alert_copy_success),
                                R.drawable.icon_ok, ContextCompat.getColor(context,R.color.purple)).show();
                    }
                });
                builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        //收藏按钮
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                CollectionHolder collectionHolder = new CollectionHolder(context);
                if(!like){
                    collectionHolder.addCollection(comic);
                    updateLikeStatus();
                    context.setResult(Global.CollectionToDetails,intent.putExtra("like_change",false));
                    SnackbarUtil.newAddImageColorfulSnackar(
                            coordinatorLayout,
                            getString(R.string.alert_add_collect_success),
                            R.drawable.icon_delicious,
                            ContextCompat.getColor(context,R.color.smmcl_green)).show();
                }else{
                    collectionHolder.deleteComic(comic.getComicId());
                    updateLikeStatus();
                    context.setResult(Global.CollectionToDetails,intent.putExtra("like_change",true));
                    SnackbarUtil.newAddImageColorfulSnackar(
                            coordinatorLayout,
                            getString(R.string.alert_del_collect_success),
                            R.drawable.icon_crying_face,
                            ContextCompat.getColor(context,R.color.holo_red_light)).show();
                }
            }
        });
        //阅读按钮
        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueReading();
            }
        });
        //简介
        instruction.setOnClickListener(new View.OnClickListener() {
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
    }

    private void initChapterFragment(){
        for (int i = 0 ; i < TYPE_MAX ; i++){
            ChaptersFragment chaptersFragment = new ChaptersFragment();
            chaptersFragment_map.put(i,chaptersFragment);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0 ; i<TYPE_MAX ; i++){
            fragmentManager.beginTransaction()
                    .replace(fragmentId[i], chaptersFragment_map.get(i)).commit();
        }
    }

    //加载数据
    public void loadComicInformation(){
        call_loadComicInformation = ComicService.get().getComicInfo(this,comic_id);//18X id:"8788");"16058"
    }

    //加载封面
    public void getCover(){
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.img_load_before)
                .error(R.drawable.img_load_failed).centerCrop();
        Glide.with(context).load(comic.getImageUrl()).apply(options).into(img_cover);
    }

    private int tryTime = 0;
    //获得章节信息
    public void getChapters(){
        details_container.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webview.evaluateJavascript("document.getElementsByTagName('html')[0].outerHTML;", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if (s.equals("null")){
                            //加载失败
                            if(tryTime>=5){
                                details_container.setRefreshing(false);
                                txtError.setVisibility(View.VISIBLE);
                                Log.d(TAG,"debug:第"+tryTime+"重连");
                            }else{
                                tryTime++;
                                getChapters();
                            }
                        }else{
                            s=DisplayUtil.unicodeDecode(s).replace("\\\"","\"");
                            Log.d(TAG, "onReceiveValue: "+s);
                            details_container.setRefreshing(false);
                            ComicFetcher.getComicChapters(s,comic);
                            new ChaptersDistributeTask(comic.getChapters()).execute();
                            return;
                        }
                    }
                });
            }
        }, 1500);
    }

    //设置界面中的漫画信息文字
    private void setInfoText(LinearLayout parent,TextView textView,String str){
        if (str != null && !str.equals("")){
            textView.setText(str);
        }else if(parent != null){
            parent.setVisibility(View.GONE);
        }
    }

    //数据库查询是否已经添加收藏
    private boolean isLike(){
        CollectionHolder collectionHolder = new CollectionHolder(this);
        return collectionHolder.hasComic(comic.getComicId());
    }
    //更新界面收藏状态
    private void updateLikeStatus(){
        if(isLike()){
            like = true;
            btn_like.setBackgroundResource(R.drawable.shape_border_rounded_rectangle_star_color);
            btn_like_icon.setImageResource(R.drawable.icon_collection_on);
            btn_like_txt.setTextColor(ContextCompat.getColor(context,R.color.star_yellow));
            btn_like_txt.setText(R.string.details_del_collection);
        }else{
            like = false;
            btn_like.setBackgroundResource(R.drawable.shape_border_rounded_rectangle);
            btn_like_icon.setImageResource(R.drawable.icon_collection_off);
            btn_like_txt.setTextColor(ContextCompat.getColor(context,R.color.black_60));
            btn_like_txt.setText(R.string.details_add_collection);
        }
    }

    private void updateReadStatus(){
        History history = getHistory();
        if (history != null){
            btn_read.setBackgroundResource(R.drawable.shape_border_rounded_rectangle_red_color);
            btn_read_icon.setImageResource(R.drawable.icon_read_on);
            btn_read_txt.setTextColor(ContextCompat.getColor(context,R.color.holo_red_light));
            btn_read_txt.setText(R.string.details_continue_read);
        }else{
            btn_read.setBackgroundResource(R.drawable.shape_border_rounded_rectangle);
            btn_read_icon.setImageResource(R.drawable.icon_read_off);
            btn_read_txt.setTextColor(ContextCompat.getColor(context,R.color.black_60));
            btn_read_txt.setText(R.string.details_to_read);
        }
    }
    //添加可点击作者
    private void setAuthor(List<Author> authors){
		//清除authors中所有组件
		view_authors.removeAllViews();
        if(authors != null && authors.size() > 0){
            for(final Author author : authors){
                if(author.getName()!=null){
                    TextView addView_author = new  TextView(context);
                    //设置style
                    TextViewCompat.setTextAppearance(addView_author,R.style.details_font);
                    //增加下划线
                    Spanned result;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        result = Html.fromHtml("<u>"+author.getName()+"</u>",Html.FROM_HTML_MODE_LEGACY);
                    }else{
                        result = Html.fromHtml("<u>"+author.getName()+"</u>");
                    }
                    addView_author.setText(result);
                    //添加margin
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, (int) DisplayUtil.dpToPx(context,2), 0);
                    addView_author.setLayoutParams(params);
                    addView_author.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context,AuthorComicsActivity.class);
                            intent.putExtra("aName",author.getName());
                            intent.putExtra("aMark",author.getMark());
                            startActivity(intent);
                        }
                    });
                    view_authors.addView(addView_author);
                }
            }
        }else {
            //当没有作者对象时，一般是有bug，就显示没有链接的文字即可，即comic.getAuthor();
            Log.d(TAG, "setAuthor: bug");
            view_authors.setVisibility(View.GONE);
        }
    }
    //获取当前漫画历史记录
    private History getHistory(){
        HistoryHolder historyHolder = new HistoryHolder(context);
        return historyHolder.getHistory(comic_id);
    }
    //继续阅读（查询所在列表，并模拟点击）
    private void continueReading(){
        History history = getHistory();
        if(history != null){
            for(int i=0 ; i< TYPE_MAX ; i++){
                for(int j = 0;j < chapterTypeMap.get(i).size();j++){
                    if(chapterTypeMap.get(i).get(j).getChapterId().equals(history.getChapterId())){
                        chaptersFragment_map.get(i).continueReadingClick(j);
                    }
                }
            }
        }else{
            for(int i=0 ; i< TYPE_MAX ; i++){
                if(chapterTypeMap.get(i).size()>0){
                    chaptersFragment_map.get(i).continueReadingClick(0);
                }
            }
        }
    }

    //网络请求，更新UI
    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_INFO:
                try {
                    ComicFetcher.getComicDetails(data.toString(),comic);
                    ComicFetcher.getComicChapters(data.toString(),comic);
                } catch (Exception e) {
                    e.printStackTrace();
                    onError("未知异常",what);
                    return;
                }
                getCover();
                setInfoText(null,txt_title,comic.getName());
                setInfoText(view_score,txt_score,comic.getScore());
                setInfoText(view_author,txt_author,comic.getAuthor());
                setInfoText(view_tag,txt_tag,comic.getTag());
                setInfoText(view_updateDate,txt_updateDate,"更新于"+comic.getUpdate());
                setInfoText(view_updateStatus,txt_updateStatus,comic.getUpdateStatus());
                setInfoText(view_status,txt_status,comic.isEnd()?"已完结":"连载中");
                setInfoText(instruction,txt_info,comic.getInfo());
                setAuthor(comic.getAuthors());
                new ChaptersDistributeTask(comic.getChapters()).execute();
                //默认焦点为顶部图片，防止滚轮不置顶
                img_cover.setFocusableInTouchMode(true);
                img_cover.requestFocus();
                //显示详细界面
                viewMain.setVisibility(View.VISIBLE);
                txtError.setVisibility(View.GONE);
                Log.d(TAG, "onResponse: "+comic.toString());
                details_container.setRefreshing(false);
                if (comic.isBan()){
                    final BottomDialog bottomDialog = DialogUtil.showBottomDialog(context);
                    bottomDialog.setClickOK(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onCreate: "+ WebViewUtil.syncCookie(context,RuleStore.get().getDomain(),"country=US"));
                            webview.getSettings().setJavaScriptEnabled(true);
                            webview.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
                            webview.loadUrl(RuleStore.get().getHost()+"/comic/"+comic_id+"/");
                            webview.setWebViewClient(new MyWebView());
                            getChapters();
                            bottomDialog.dismiss();
                        }
                    });
                    bottomDialog.show();
                }
                break;
        }
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }

    @Override
    public void onError(String msg,String what) {
        Log.e(TAG, "Error: " + msg);
        details_container.setRefreshing(false);
        txtError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
        viewMain.setVisibility(View.GONE);
        txtError.setVisibility(View.GONE);
        for(int i = 0 ; i<TYPE_MAX ;i++){
            findViewById(typeTextId[i]).setVisibility(View.GONE);
            chaptersFragment_map.get(i).clearChapters();
        }
        details_container.setRefreshing(true);
        // 获取对象，重新获取当前目录对象
        loadComicInformation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
        if(call_loadComicInformation!=null && !call_loadComicInformation.isCanceled()){
            call_loadComicInformation.cancel();
            Log.d(TAG, "onDestroy: "+"取消网络请求！");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode:"+requestCode + "resultCode:"+resultCode);
        switch (resultCode){
            case Global.REQUEST_COMIC_HISTORY:
                //阅读界面返回后更新阅读情况
                History history = new History();
                history.setChapterId(data.getStringExtra("chapterId"));
                history.setChapterName(data.getStringExtra("chapterName"));
                history.setComicName(comic.getName());
                history.setImgUrl(comic.getImageUrl());
                history.setComicId(comic.getComicId());
                history.setEnd(comic.isEnd());
                history.setPage(data.getIntExtra("page",1));
                history.setReadTime(System.currentTimeMillis());
                Log.d(TAG, "onActivityResult: "+history.toString());
                HistoryHolder historyHolder = new HistoryHolder(context);
                historyHolder.updateOrAddHistory(history);
                //刷新章节界面
                for(int i = 0 ; i<TYPE_MAX ;i++){
                    chaptersFragment_map.get(i).updateChapters();
                }
                updateReadStatus();
                break;
        }

    }

    //根据类型分发章节
    class ChaptersDistributeTask extends AsyncTask<Void,Void,Map<Integer,List<Chapter>>>{
        private List<Chapter> list;

        public ChaptersDistributeTask(List<Chapter> list) {
            this.list = list;
        }

        @Override
        protected Map<Integer,List<Chapter>> doInBackground(Void... voids) {
            Map<Integer,List<Chapter>> map = new HashMap<>();
            for(int i=0 ; i< TYPE_MAX ; i++){
                List<Chapter> cList = new ArrayList<>();
                map.put(i,cList);
            }
            for (Chapter chapter:list) {
                int type = chapter.getType();
                map.get(type).add(chapter);
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map<Integer,List<Chapter>> map) {
            super.onPostExecute(map);
            if (list.size()==0 && !comic.isBan()){
                txtError.setVisibility(View.VISIBLE);
                txtError.setText(R.string.error_comic_chapter_load_fail);
            }else{
                chapterTypeMap = map;
                for(int i = 0 ; i<TYPE_MAX ;i++){
                    chaptersFragment_map.get(i).addChapters(map.get(i));
                    chaptersFragment_map.get(i).setComicName(comic.getName());
                    if(map.get(i).size()>0){
                        //显示章节类型文字
                        findViewById(typeTextId[i]).setVisibility(View.VISIBLE);
                    }
                    chaptersFragment_map.get(i).updateChapters();
                }
            }
        }
    }

}
