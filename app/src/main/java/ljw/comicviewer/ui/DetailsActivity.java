package ljw.comicviewer.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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
import ljw.comicviewer.others.MyWebView;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.dialog.BottomDialog;
import ljw.comicviewer.ui.dialog.ThemeDialog;
import ljw.comicviewer.ui.fragment.ChaptersFragment;
import ljw.comicviewer.util.DialogUtil;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.SnackbarUtil;
import ljw.comicviewer.util.StringUtil;
import ljw.comicviewer.util.WebViewUtil;
import retrofit2.Call;

/**
 * 漫画详细页
 */
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
    private RuleStore ruleStore = RuleStore.get();
    private Snackbar netErrorSnackbar;
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
    @BindView(R.id.details_chapters_loading)
    LinearLayout view_loading;
    private HistoryHolder historyHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_details);

        //view绑定代码生成
        ButterKnife.bind(this);
        historyHolder = new HistoryHolder(context);

        initViewAndData();
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
        comic.setComeFrom(ruleStore.getComeFrom());

        //错误提示
        addErrorBar();

        //加载数据
        loadComicInformation();
        updateLikeStatus();
        updateReadStatus();

    }

    private void addErrorBar(){
        netErrorSnackbar = SnackbarUtil.newColorfulSnackbar(coordinatorLayout,
                getString(R.string.error_comic_chapter_load_fail),
                ContextCompat.getColor(context,R.color.holo_red_light),
                ContextCompat.getColor(context,R.color.white))
                .setDuration(Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(context,R.color.blue_A1E0F4))
                .setAction(getString(R.string.refresh), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRefresh();
                    }
                });
    }

    private void initListener(){
        //标题
        txt_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeDialog themeDialog = new ThemeDialog(context);
                themeDialog.setTitle(R.string.dialog_title_comic_name)
                        .setMessage(txt_title.getText())
                        .setNegativeButton(R.string.dialog_btn_copy, new ThemeDialog.OnButtonClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface) {
                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建普通字符型ClipData
                                ClipData mClipData = ClipData.newPlainText("Label", txt_title.getText());
                                // 将ClipData内容放到系统剪贴板里。
                                cm.setPrimaryClip(mClipData);
                                SnackbarUtil.newAddImageColorfulSnackar(
                                        coordinatorLayout, getString(R.string.alert_copy_success),
                                        R.drawable.icon_ok, ContextCompat.getColor(context,R.color.purple)).show();
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.dialog_btn_close, new ThemeDialog.OnButtonClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface) {
                                dialogInterface.dismiss();
                            }
                        });
                themeDialog.show();
            }
        });
        //收藏按钮
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("comicId",comic_id);
                CollectionHolder collectionHolder = new CollectionHolder(context);
                if(!like){
                    collectionHolder.addCollection(comic);
                    updateLikeStatus();
                    context.setResult(Global.STATUS_CollectionToDetails,intent.putExtra("like_change",false));
                    SnackbarUtil.newAddImageColorfulSnackar(
                            coordinatorLayout,
                            getString(R.string.alert_add_collect_success),
                            R.drawable.icon_delicious,
                            ContextCompat.getColor(context,R.color.smmcl_green)).show();
                }else{
                    collectionHolder.deleteComic(comic.getComicId());
                    updateLikeStatus();
                    context.setResult(Global.STATUS_CollectionToDetails,intent.putExtra("like_change",true));
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

    //加载数据
    public void loadComicInformation(){
        try {
            call_loadComicInformation = ComicService.get().getHTML(this,Global.REQUEST_COMICS_INFO,
                    ruleStore.getDetailsRule().get("url"),comic_id);//18X id:"8788");"16058"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //加载封面
    public void getCover(){
        if(!isDestroyed()) {
            //如果封面地址改变了，更新历史记录数据库中的数据
            History history = getHistory();
            if(history!=null && !comic.getImageUrl().equals(history.getImgUrl())){
                history.setImgUrl(comic.getImageUrl());
                historyHolder.updateOrAddHistory(history);
                setResult(Global.STATUS_COVER_UPDATE,getIntent().putExtra("cover_update",true));
            }
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.img_load_before)
                    .error(R.drawable.img_load_failed)
                    .centerCrop().transform(new RoundedCorners(20));
            Glide.with(context).load(comic.getImageUrl()).apply(options).into(img_cover);
        }
    }

    private int tryTime = 0;
    //获得章节信息(webView方式)
    public void getChapters(){
//        details_container.setRefreshing(true);
        int interval = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String js = ruleStore.getDetailsRule().get("wv-js");
                if(js!=null) {
                    webview.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            if (s.equals("null")) {
                                //加载失败
                                if (tryTime >= 5) {
                                    details_container.setRefreshing(false);
                                    netErrorSnackbar.show();
                                    Log.d(TAG, "debug:第" + tryTime + "重连");
                                } else {
                                    tryTime++;
                                    getChapters();
                                }
                            } else {
                                s = StringUtil.unicodeDecode(s).replace("\\\"", "\"");
                                Log.d(TAG, "onReceiveValue: " + s);
                                details_container.setRefreshing(false);
                                new ChaptersDistributeTask(s).execute();
                                return;
                            }
                        }
                    });
                }else{
                    onError("js is null","Details");
                }
            }
        }, interval);
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
        like = isLike();
        btn_like_icon.setSelected(like);
        btn_like_txt.setSelected(like);
        btn_like_txt.setText(like?R.string.details_del_collection:R.string.details_add_collection);
    }

    private void updateReadStatus(){
        History history = getHistory();
        boolean read = (history != null);
        btn_read_icon.setSelected(read);
        btn_read_txt.setSelected(read);
        btn_read_txt.setText(read?R.string.details_continue_read:R.string.details_to_read);
    }
    //添加可点击作者
    private void setAuthor(List<Author> authors){
		//清除authors中所有组件
		view_authors.removeAllViews();
        if(authors != null && authors.size() > 0){
            for(final Author author : authors){
                if(author.getName()!=null){
                    TintTextView addView_author = new  TintTextView(context);
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
                    addView_author.setMaxLines(1);
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
        return historyHolder.getHistory(comic_id);
    }
    //继续阅读（查询所在列表，并模拟点击）
    private void continueReading(){
        History history = getHistory();
        if(history != null){
            for(int i=0 ; i< TYPE_MAX ; i++){
                if(chaptersFragment_map.get(i)!=null) {
                    for (int j = 0; j < chapterTypeMap.get(i).size(); j++) {
                        if (chapterTypeMap.get(i).get(j).getChapterId().equals(history.getChapterId())) {
                            chaptersFragment_map.get(i).continueReadingClick(j);
                        }
                    }
                }
            }
        }else{
            for(int i=0 ; i< TYPE_MAX ; i++){
                if(chapterTypeMap.get(i)!=null && chapterTypeMap.get(i).size()>0){
                    chaptersFragment_map.get(i).continueReadingClick(0);
                    return;
                }
            }
        }
    }

    //网络请求，更新UI
    @Override
    public void onFinish(final Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_INFO:
                try {
                    new GetDetails(data).execute();
                    new ChaptersDistributeTask(data).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    onError("未知异常",what);
                    return;
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
        netErrorSnackbar.show();
    }

    @Override
    public void onRefresh() {
        netErrorSnackbar.dismiss();
        addErrorBar();
        view_loading.setVisibility(View.VISIBLE);
        viewMain.setVisibility(View.GONE);
        for(int i = 0 ; i<TYPE_MAX ;i++){
            findViewById(typeTextId[i]).setVisibility(View.GONE);
            if(chaptersFragment_map.get(i)!=null) chaptersFragment_map.get(i).clearChapters();
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
                history.setComeFrom(RuleStore.get().getComeFrom());
//                Log.d(TAG, "onActivityResult: "+history.toString());
//                HistoryHolder historyHolder = new HistoryHolder(context);
                historyHolder.updateOrAddHistory(history);
                //刷新章节界面
                for(int i = 0 ; i<TYPE_MAX ;i++){
                    if(chaptersFragment_map.get(i)!=null) chaptersFragment_map.get(i).updateChapters();
                }
                updateReadStatus();
                break;
        }

    }

    //根据类型分发章节
    class ChaptersDistributeTask extends AsyncTask<Void,Void,Map<Integer,List<Chapter>>>{
        private Object data;

        public ChaptersDistributeTask(Object data) {
            this.data = data;
        }

        @Override
        protected Map<Integer,List<Chapter>> doInBackground(Void... voids) {
            //获得章节列表
            List<Chapter> list = ComicFetcher.getComicChapterList(data.toString(),comic);
            if(list==null || (list.size()<=0 && !comic.isBan())) return null;
            Map<Integer,List<Chapter>> map = new HashMap<>();
            for(int i=0 ; i< TYPE_MAX ; i++){
                List<Chapter> cList = new ArrayList<>();
                map.put(i,cList);
            }
            for (Chapter chapter:list) {
                int type = chapter.getType();
                map.get(type).add(chapter);
            }
            chapterTypeMap = map;
            return map;
        }

        @Override
        protected void onPostExecute(Map<Integer,List<Chapter>> map) {
            super.onPostExecute(map);
            if (map==null){
                view_loading.setVisibility(View.GONE);
                netErrorSnackbar.show();
            }else{
                //如果回调时activity已经销毁，则不加载。
                if (context.isDestroyed()) return;
                for(int i = 0 ; i<TYPE_MAX ;i++){
                    if(map.get(i).size()>0){
                        //显示章节类型文字
                        findViewById(typeTextId[i]).setVisibility(View.VISIBLE);
                        //替换章节fragment
                        ChaptersFragment chaptersFragment = new ChaptersFragment(comic.getName(),map.get(i));
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(fragmentId[i], chaptersFragment).commit();
                        chaptersFragment_map.put(i,chaptersFragment);
                    }
                }
                view_loading.setVisibility(View.GONE);
            }
        }
    }

    class GetDetails extends AsyncTask<Void,Void,Comic>{
        private Object data;

        public GetDetails(Object data) {
            this.data = data;
        }

        @Override
        protected Comic doInBackground(Void... voids) {
             return ComicFetcher.getComicDetails(data.toString(),comic);
        }

        @Override
        protected void onPostExecute(Comic comic) {
            super.onPostExecute(comic);
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

            //显示详细界面
            viewMain.setVisibility(View.VISIBLE);
            Log.d(TAG, "onResponse: "+comic.toString());
            details_container.setRefreshing(false);
            //如果收藏的漫画，则更新收藏信息
            if(isLike()) {
                CollectionHolder collectionHolder = new CollectionHolder(context);
                collectionHolder.addOrUpdateCollection(comic);
            }
            if (comic.isBan()){
                final BottomDialog bottomDialog = DialogUtil.bulidBottomDialog(context);
                bottomDialog.setClickOK(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view_loading.setVisibility(View.VISIBLE);
                        if (ruleStore.getCookie()!=null) {
                            //设置cookie
                            Log.d(TAG, "onCreate: " + WebViewUtil.syncCookie(context, RuleStore.get().getDomain(), ruleStore.getCookie()));
                        }
                        webview.getSettings().setJavaScriptEnabled(true);
                        webview.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
                        webview.loadUrl(ruleStore.getHost() +
                                ruleStore.getDetailsRule().get("url").replaceAll("\\{comic:.*?\\}",comic_id));
                        webview.setWebViewClient(new MyWebView());
                        getChapters();
                        bottomDialog.dismiss();
                    }
                });
                bottomDialog.show();
            }
        }
    }
}
