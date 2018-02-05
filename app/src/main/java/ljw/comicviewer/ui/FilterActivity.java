package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Category;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.others.MyWebView;
import ljw.comicviewer.store.FilterStore;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.adapter.ComicRecyclerViewAdapter;
import ljw.comicviewer.ui.adapter.FilterAdapter;
import ljw.comicviewer.ui.listeners.OnItemClickListener;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.RefreshLayoutUtil;
import ljw.comicviewer.util.SnackbarUtil;
import ljw.comicviewer.util.StringUtil;
import retrofit2.Call;

public class FilterActivity extends BaseActivity
        implements ComicService.RequestCallback {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private FilterAdapter filterAdapter;
    private ComicRecyclerViewAdapter filterRecyclerViewAdapter;
    private boolean loadingNext = false;
    private boolean loading = false;
    private int curPage = 1;
    private int maxPage = -1;
    private String use = "html";//使用请求方式
    private WebView webView;
    FilterStore filterStore = FilterStore.get();
    RuleStore ruleStore = RuleStore.get();
    List<Comic> comics = new ArrayList<>();
    List<Category> categories = new ArrayList<>();
    List<TextView> textViews = new ArrayList<>();
    Call call_filter;
    @BindView(R.id.nav_child_title)
    TextView title;
    @BindView(R.id.filter_type_box)
    LinearLayout typeBox;
    @BindView(R.id.filter_layout_types)
    RelativeLayout view_types;//下面gridView_filter的父级
    @BindView(R.id.filter_grid_view)
    GridView gridView_filter;
    @BindView(R.id.filter_type_shadow)
    RelativeLayout view_shadow;//gridView_filter下面的阴影
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.btn_toTop)
    FloatingActionButton btn_toTop;
    @BindView(R.id.comic_grid_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        context = this;
        ButterKnife.bind(this);
        initView();
        initLoad();
    }

    private void initView(){
        title.setText(R.string.title_filter);
        //只允许刷新，以便初次启动时自动刷新
        RefreshLayoutUtil.init(context,refreshLayout,
                RefreshLayoutUtil.Mode.Only_Refresh,true);
        //设置回顶按钮颜色
        btn_toTop.setBackgroundTintList(
                ThemeUtils.getThemeColorStateList(context,R.color.theme_color_primary));

        initGridView();
        addTypeBtn();
        addListener();
    }

    private void initLoad(){
        refreshLayout.autoRefresh();
    }

    private void getData(){
        String listUse = ruleStore.getListRule().get("use");
        if(listUse!=null) use = listUse;
        switch (use){
            case "html":
                call_filter = ComicService.get().getHTML(this, Global.REQUEST_COMIC_FILTER,
                        ruleStore.getListRule().get("url"), curPage);
                break;
            case "webview":
                useWebView();
                break;

        }
        loading = true;
    }

    private String parseUrl(){
        String typeStr = StringUtil.join(filterStore.getOrder(),filterStore.getFilterStatus(), filterStore.getSeparate());
        if(filterStore.getEndStr()!=null && !typeStr.equals(""))
            typeStr += filterStore.getEndStr();
        Log.d(TAG, "类型对应网页字符串: " + typeStr);
        return ruleStore.getHost() +
                ruleStore.getListRule().get("url")
                        .replaceAll("\\{type:.*?\\}",typeStr)
                        .replaceAll("\\{page:.*?\\}",curPage+"");
    }

    private void useWebView(){
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(
                "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
        webView.setWebViewClient(new MyWebView(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: "+url);
                webView.evaluateJavascript("document.getElementsByTagName('html')[0].outerHTML;",new ValueCallback<String>(){
                    @Override
                    public void onReceiveValue(String s) {
                        if(s!=null){
                            s = StringUtil.unicodeDecode(s).replace("\\\"", "\"");
                            Log.d(TAG, "onReceiveValue: "+s);
                            LoadDataTask loadDataTask = new LoadDataTask(Global.REQUEST_COMIC_FILTER,s);
                            loadDataTask.execute();
                        }
                    }
                });
            }
        });
        webView.loadUrl(parseUrl());
    }



    private void initGridView(){
        filterAdapter = new FilterAdapter(context,categories);
        gridView_filter.setAdapter(filterAdapter);
        //根据屏幕宽度设置列数
        int columns = DisplayUtil.getGridNumColumns(context,120);
        int itemWidth = (int) (DisplayUtil.getScreenWidthPX(context)/columns);
        filterRecyclerViewAdapter = new ComicRecyclerViewAdapter(context,comics, itemWidth);
        recyclerView.setLayoutManager(new GridLayoutManager(context,columns));
        recyclerView.setAdapter(filterRecyclerViewAdapter);
    }

    private void addListener(){
        //设置分类表内对象点击事件
        gridView_filter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Category category = categories.get(position);
                //重设选择项，并设置当前的为选择
                resetCategoriesSelected();
                category.setSelected(true);
                filterAdapter.notifyDataSetChanged();
                //修改储存的状态
                filterStore.setFilterStatus(category.getParentName(),category.getValue());
                //打印状态
                filterStore.printStore();
                for(TextView textView : textViews){
                    if((boolean)textView.getTag()){
                        textView.setText(category.getName());
                        if(category.getName().equals("全部")){
                            textView.setText(category.getParentName());
                        }
                    }
                }
                resetTextStatus();
                //处于加载状态，取消上一次请求，延迟下一次请求
                if(loading){
                    call_filter.cancel();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getData();
                        }
                    },500);
                    return;
                }
                //用下拉加载来加载选择的类型
                RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
                refreshLayout.autoRefresh();
            }
        });
        //点击分类下方黑块，隐藏分类表
        view_shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTextStatus();
                view_types.setVisibility(View.GONE);
            }
        });
        //上拉下拉事件
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadingNext = true;
                curPage++;
                getData();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                comics.clear();
                maxPage = -1;
                curPage = 1;
                getData();
            }
        });

        filterRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Comic comic = comics.get(position);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",comic.getComicId());
                startActivity(intent);
            }
        });
        //悬浮回到顶部按钮
        btn_toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerView != null && comics.size()>0){
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    //添加父类型按钮 在标题栏下方
    private void addTypeBtn(){
        List<String> order = filterStore.getOrder();
        if(order!=null && order.size()>0){
            for (final String typeName: order) {
                TextView addView = new TextView(context);
                addView.setText(typeName);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                lp.gravity = Gravity.CENTER;
                addView.setLayoutParams(lp);
                addView.setTextSize(20);
                addView.setTextColor(ContextCompat.getColor(context,R.color.black_888));
                addView.setGravity(Gravity.CENTER);
                addView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getTag()==null || !(boolean)view.getTag()){
                            //重置状态
                            resetTextStatus();
                            //设置选中状态为true
                            view.setTag(true);
                            //设置选中父类型的颜色
                            ((TextView) view).setTextColor(ThemeUtils.getColorById(context,R.color.theme_color_primary));
                            //清空并重新加载数据
                            categories.clear();
                            categories.addAll(ruleStore.getTypeRule().get(typeName));
                            filterAdapter.notifyDataSetChanged();
                            view_types.setVisibility(View.VISIBLE);
                        }else{
                            //如果是当前选中的父类型再点击将隐藏子类型网格，并重置状态
                            resetTextStatus();
                        }
                    }
                });
                textViews.add(addView);
                typeBox.addView(addView);
            }
        }
    }

    //重置文字状态
    private void resetTextStatus(){
        for (TextView textView : textViews){
            //恢复选中前的颜色
            textView.setTextColor(ContextCompat.getColor(context,R.color.black_60));
            //选中状态为false
            textView.setTag(false);
        }
        view_types.setVisibility(View.GONE);
    }

    //重置选择状态
    private void resetCategoriesSelected(){
        for(Category category:categories){
            category.setSelected(false);
        }
    }

    class LoadDataTask extends AsyncTask<Void,Void,Object> {
        private String tag;
        private Object obj;

        public LoadDataTask(String tag, Object obj) {
            this.tag = tag;
            this.obj = obj;
        }


        @Override
        protected Object doInBackground(Void... voids) {
            switch (tag) {
                case Global.REQUEST_COMIC_FILTER:
                    CallBackData callBackData = ComicFetcher.getComicList(obj.toString());
                    List<Comic> tempList = (List<Comic>) callBackData.getObj();
                    if(maxPage == -1){
                        maxPage = (int) callBackData.getArg1();
                    }
                    if(tempList.size()>0) comics.addAll(tempList);
                    return tempList.size();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object resultObj) {
            super.onPostExecute(obj);
            switch (tag) {
                case Global.REQUEST_COMIC_FILTER:
                    if (resultObj!=null && (Integer)resultObj > 0){
                        btn_toTop.setVisibility(View.VISIBLE);
                        //结束刷新或加载状态
                        RefreshLayoutUtil.onFinish(refreshLayout);
                        if(curPage >= maxPage){
                            RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Disable);
                        }else{
                            RefreshLayoutUtil.setMode(refreshLayout,RefreshLayoutUtil.Mode.Only_LoadMore);
                        }
                    }else{
                        onError(getString(R.string.data_load_fail),tag);
                    }
                    loadingNext = false;
                    loading = false;
                    break;
            }
        }
    }

    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMIC_FILTER:
                LoadDataTask loadDataTask = new LoadDataTask(what,data);
                loadDataTask.execute();
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {
        switch (what){
            case Global.REQUEST_COMIC_FILTER:
                if(call_filter==null || !call_filter.isCanceled()){
                    RefreshLayoutUtil.onFinish(refreshLayout);
                    SnackbarUtil.newAddImageColorfulSnackar(
                            coordinatorLayout, getString(R.string.data_load_fail),
                            R.drawable.icon_error,
                            ContextCompat.getColor(context,R.color.star_yellow)).show();
                }
                if(loadingNext) {
                    curPage--;
                }else{
                    RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
                    btn_toTop.setVisibility(View.GONE);
                }
                loadingNext = false;
                break;
        }
        loading = false;
        Log.e(TAG,what + " Error: " + (call_filter!=null && call_filter.isCanceled()?"取消请求":msg));
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filterStore.filterStatusReset();
        if(call_filter !=null && !call_filter.isCanceled()){
            call_filter.cancel();
            Log.d(TAG, "onDestroy: "+"取消网络请求！");
        }
    }
}
