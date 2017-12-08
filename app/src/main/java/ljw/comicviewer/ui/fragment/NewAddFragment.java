package ljw.comicviewer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.adapter.PictureGridAdapter;
import ljw.comicviewer.util.RefreshLayoutUtil;
import ljw.comicviewer.util.SnackbarUtil;


/**
 * Created by ljw on 2017-08-23 023.
 */

public class NewAddFragment extends BaseFragment
        implements AbsListView.OnScrollListener, ComicService.RequestCallback {
    private String TAG = NewAddFragment.class.getSimpleName()+"----";
    private Context context;
    protected PictureGridAdapter pictureGridAdapter;
    List<Comic> comicList = new ArrayList<>();
    private File myCache;
    private int loadedPage = 1;
    private int maxPage = -1;
    private boolean isLoadingNext = false;
    RuleStore ruleStore = RuleStore.get();
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.grid_view)
    GridView gridView;
    @BindView(R.id.grid_net_error)
    TextView txt_netError;
    @BindView(R.id.newAdd_btn_toTop)
    FloatingActionButton btn_toTop;
    @BindView(R.id.comic_grid_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    public NewAddFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_newadd,null);
        ButterKnife.bind(this,rootView);
        initView();
        return rootView;
    }

    @Override
    public void initView() {
        //禁用上拉下拉
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
        //设置主题色
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);
        //下拉到底最后不自动加载，需要再拉一下
        refreshLayout.setEnableAutoLoadmore(false);
        //不在加载更多完成之后滚动内容显示新数据
        refreshLayout.setEnableScrollContentWhenLoaded(false);

        initGridView();
        addListener();
        //数据首次加载
        initLoad();
//        myCache = context.getExternalCacheDir();
    }

    public void addListener() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉加载
                isLoadingNext = true;
                getListItems(++loadedPage);
                Log.d(TAG,"load next page; currentLoadingPage = "+loadedPage);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                loadedPage = 1;
                maxPage = -1;
                comicList.clear();
                pictureGridAdapter.notifyDataSetChanged();
                // 获取对象，重新获取当前目录对象
                getListItems(loadedPage);
            }
        });

        //悬浮回到顶部按钮
        btn_toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gridView != null && comicList.size()>0){
                    gridView.smoothScrollToPosition(0);
                }
            }
        });
    }

    public void initGridView() {
        pictureGridAdapter = new PictureGridAdapter(context,comicList);
        gridView.setAdapter(pictureGridAdapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new ItemClickListener());
        pictureGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void initLoad() {
        //自动刷新，获取数据
        refreshLayout.autoRefresh();
    }

    //获得漫画列表对象并存入comicList
    public void getListItems(int page){
        btn_toTop.setVisibility(View.GONE);
        ComicService.get().getHTML(this, Global.REQUEST_COMIC_NEWADD,
                ruleStore.getListRule().get("url"),page);
    }


    //TODO:滑动事件--------
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
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
    public void onScroll(AbsListView absListView, int firstItem, int visibleItem, int totalItem) {
        //firstItem 为第一个可见对象的下标, visibleItem可见对象的数量, totalItem 可见对象的总数
//        Log.d(TAG, "onScroll: "+firstItem+","+visibleItem+","+totalItem);
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
        int firstVisiblePosition= gridView.getFirstVisiblePosition();
        int lastVisiblePosition = gridView.getLastVisiblePosition();
        if (lastVisiblePosition==-1){
            delayedFlushAdapter();
            return;
        }
        for(int i = 0; i < gridView.getCount();i++){
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

    @Override
    public Object myDoInBackground(String TAG,Object data) {
        switch (TAG) {
            case Global.REQUEST_COMIC_NEWADD:
                CallBackData callBackData = ComicFetcher.getComicList(data.toString());
                List<Comic> tempList = (List<Comic>) callBackData.getObj();
                if(maxPage == -1){
                    maxPage = (int) callBackData.getArg1();
                }
                if(tempList.size()>0) comicList.addAll(tempList);
                return tempList.size();
        }
        return null;
    }

    @Override
    public void myOnPostExecute(String TAG,Object resultObj) {
        switch (TAG){
            case Global.REQUEST_COMIC_NEWADD:
                if (resultObj!=null && (Integer)resultObj > 0){
                    txt_netError.setVisibility(View.GONE);
                    btn_toTop.setVisibility(View.VISIBLE);
                    //结束刷新或加载状态
                    RefreshLayoutUtil.onFinish(refreshLayout);
                    if(loadedPage >= maxPage){
                        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
                    }else{
                        RefreshLayoutUtil.setMode(refreshLayout,RefreshLayoutUtil.Mode.Both);
                    }
                    clearAndLoadImage();
                    isLoadingNext = false;
                }else{
                    netErrorTo();
                }
                break;
        }
    }

    //网络请求，更新UI
    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMIC_NEWADD:
                UIUpdateTask UIUpdateTask = new UIUpdateTask(what,data);
                UIUpdateTask.execute();
                break;
        }
    }

    @Override
    public void onError(String msg ,String what) {
        switch (what){
            case Global.REQUEST_COMICS_UPDATE:
            case Global.REQUEST_COMIC_NEWADD:
                RefreshLayoutUtil.onFinish(refreshLayout);
                if(isLoadingNext) {
                    SnackbarUtil.newAddImageColorfulSnackar(
                            coordinatorLayout, getString(R.string.gird_tips_loading_next_page_fail),
                            R.drawable.icon_error,
                            ContextCompat.getColor(context,R.color.star_yellow)).show();
                }else{
                    netErrorTo();
                }
                break;
        }
        Log.e(TAG,what + " Error: " + msg);
    }

    public void netErrorTo(){
        RefreshLayoutUtil.onFinish(refreshLayout);
        SnackbarUtil.newAddImageColorfulSnackar(
                coordinatorLayout, getString(R.string.data_load_fail),
                R.drawable.icon_error,
                ContextCompat.getColor(context,R.color.star_yellow)).show();
        if (!isLoadingNext){
            RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
            txt_netError.setVisibility(View.VISIBLE);
        }
        isLoadingNext = false;
    }

    //-------------------

    //网格对象点击事件
    class  ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Comic comic = comicList.get(position);
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("id",comic.getComicId());
            intent.putExtra("score",comic.getScore());
            intent.putExtra("title",comic.getName());
            startActivity(intent);
        }
    }

}
