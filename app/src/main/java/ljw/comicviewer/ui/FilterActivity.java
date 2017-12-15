package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import ljw.comicviewer.store.FilterStore;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.adapter.FilterAdapter;
import ljw.comicviewer.ui.adapter.PictureGridAdapter;
import ljw.comicviewer.util.RefreshLayoutUtil;
import ljw.comicviewer.util.SnackbarUtil;
import retrofit2.Call;

public class FilterActivity extends AppCompatActivity
        implements ComicService.RequestCallback {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private FilterAdapter filterAdapter;
    private PictureGridAdapter pictureGridAdapter;
    private boolean loadingNext = false;
    private boolean loading = false;
    private int curPage = 1;
    private int maxPage = -1;
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
    @BindView(R.id.grid_view)
    GridView gridView_comics;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.newAdd_btn_toTop)
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
        //只能下拉刷新
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
        //设置主题色
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);
        //下拉到底最后不自动加载，需要再拉一下
        refreshLayout.setEnableAutoLoadmore(false);
        //不在加载更多完成之后滚动内容显示新数据
        refreshLayout.setEnableScrollContentWhenLoaded(false);

        title.setText(R.string.title_filter);
        initGridView();
        addTypeBtn();
        addListener();
    }

    private void initLoad(){
        refreshLayout.autoRefresh();
    }

    private void getData(){
        call_filter = ComicService.get().getHTML(this, Global.REQUEST_COMIC_FILTER,
                ruleStore.getListRule().get("url"),curPage);
        loading = true;
    }

    private void initGridView(){
        filterAdapter = new FilterAdapter(context,categories);
        gridView_filter.setAdapter(filterAdapter);
        pictureGridAdapter = new PictureGridAdapter(context,comics);
        gridView_comics.setAdapter(pictureGridAdapter);
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
        //漫画网格滚动事件
        gridView_comics.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState){
                    case SCROLL_STATE_TOUCH_SCROLL:
                        //手指接触状态
                        break;
                    case SCROLL_STATE_FLING:
                        //屏幕处于滑动状态
                        break;
                    case SCROLL_STATE_IDLE:
                        //停止滑动状态
                        clearAndLoadImage();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        gridView_comics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
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
                if(gridView_comics != null && comics.size()>0){
                    gridView_comics.smoothScrollToPosition(0);
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
                addView.setTextColor(ContextCompat.getColor(context,R.color.black_60));
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
                            ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.smmcl_green));
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

    //延迟刷新适配器，防止第一次加载不显示封面
    public void delayedFlushAdapter(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pictureGridAdapter.notifyDataSetChanged();
                clearAndLoadImage();
            }
        },200);
    }

    //清理不可见是item,加载可见的item。如果是第一次就加载这加载整页
    public void clearAndLoadImage(){
        int firstVisiblePosition= gridView_comics.getFirstVisiblePosition();
        int lastVisiblePosition = gridView_comics.getLastVisiblePosition();
        if (lastVisiblePosition==-1){
            delayedFlushAdapter();
            return;
        }
        for(int i = 0; i < gridView_comics.getCount();i++){
            View view = pictureGridAdapter.getViewMap().get(i);
            if (view!=null){
                ImageView image = (ImageView) view.findViewById(R.id.comic_img);
                ImageView EndTag = (ImageView) view.findViewById(R.id.comic_status);
                if(i<firstVisiblePosition || i>lastVisiblePosition){
                    //Log.d(TAG, "clearImage: "+i);
                    image.setImageBitmap(null);
                    image.setImageDrawable(null);
                    EndTag.setImageResource(0);
                }else{
                    //Log.d(TAG, "loadImage: "+i);
                    pictureGridAdapter.loadCover(i,view);
                }
            }
        }
        System.gc();
        pictureGridAdapter.notifyDataSetChanged();
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
                        clearAndLoadImage();
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
                if(!call_filter.isCanceled()){
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
        Log.e(TAG,what + " Error: " + (call_filter.isCanceled()?"取消请求":msg));
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
