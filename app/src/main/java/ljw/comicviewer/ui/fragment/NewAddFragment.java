package ljw.comicviewer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.adapter.ComicRecyclerViewAdapter;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.RefreshLayoutUtil;
import ljw.comicviewer.util.SnackbarUtil;
import ljw.comicviewer.util.ThemeUtil;


/**
 * Created by ljw on 2017-08-23 023.
 */

public class NewAddFragment extends BaseFragment
        implements  ComicService.RequestCallback {
    private String TAG = NewAddFragment.class.getSimpleName()+"----";
    private Context context;
    protected ComicRecyclerViewAdapter pictureGridAdapter;
    List<Comic> comicList = new ArrayList<>();
    private int curPage = 1;
    private int maxPage = -1;
    private boolean isLoadingNext = false;
    RuleStore ruleStore = RuleStore.get();
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.grid_net_error)
    TextView txt_netError;
    @BindView(R.id.btn_toTop)
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
        View rootView = inflater.inflate(R.layout.comic_recyclerview_top,null);
        ButterKnife.bind(this,rootView);
        initView();
        return rootView;
    }

    @Override
    public void initView() {
        //只能下拉刷新
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
        //设置主题色
        refreshLayout.setPrimaryColors(ThemeUtil.getThemeColor(context),
                ContextCompat.getColor(context,R.color.window_background));
        //下拉到底最后不自动加载，需要再拉一下
//        refreshLayout.setEnableAutoLoadmore(false);
        //不在加载更多完成之后滚动内容显示新数据
        refreshLayout.setEnableScrollContentWhenLoaded(false);
        //设置回顶按钮颜色
        btn_toTop.setBackgroundTintList(ColorStateList.valueOf(ThemeUtil.getThemeColor(context)));

        initGridView();
        addListener();
        //数据首次加载
//        initLoad();
//        myCache = context.getExternalCacheDir();
    }

    public void addListener() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉加载
                isLoadingNext = true;
                getListItems(++curPage);
                Log.d(TAG,"load next page; currentLoadingPage = "+ curPage);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                curPage = 1;
                maxPage = -1;
                comicList.clear();
                pictureGridAdapter.notifyDataSetChanged();
                // 获取对象，重新获取当前目录对象
                getListItems(curPage);
            }
        });

        //悬浮回到顶部按钮
        btn_toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerView != null && comicList.size()>0){
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    public void initGridView() {
        //根据屏幕宽度设置列数
        int columns = DisplayUtil.getGridNumColumns(context,Global.ITEMVIEWWIDTH);
        int itemWidth = (int) (DisplayUtil.getScreenWidthPX(context)/columns);
        pictureGridAdapter = new ComicRecyclerViewAdapter(context,comicList,itemWidth);
        recyclerView.setLayoutManager(new GridLayoutManager(context,columns));
        recyclerView.setAdapter(pictureGridAdapter);
        pictureGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void initLoad() {
        //自动刷新，获取数据
        refreshLayout.autoRefresh();
    }

    //获得漫画列表对象并存入comicList
    public void getListItems(int page){
        ComicService.get().getHTML(this, Global.REQUEST_COMIC_NEWADD,
                ruleStore.getNewAddRule().get("url"),page);
    }

    @Override
    public Object myDoInBackground(String what,Object data) {
        switch (what) {
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
    public void myOnPostExecute(String what,Object resultObj) {
        switch (what){
            case Global.REQUEST_COMIC_NEWADD:
                if (resultObj!=null && (Integer)resultObj > 0){
                    txt_netError.setVisibility(View.GONE);
                    btn_toTop.setVisibility(View.VISIBLE);
                    //结束刷新或加载状态
                    RefreshLayoutUtil.onFinish(refreshLayout);
                    if(curPage >= maxPage){
                        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
                    }else{
                        RefreshLayoutUtil.setMode(refreshLayout,RefreshLayoutUtil.Mode.Both);
                    }
                }else{
                    onError(getString(R.string.data_load_fail),what);
                }
                isLoadingNext = false;
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
                SnackbarUtil.newAddImageColorfulSnackar(
                        coordinatorLayout, getString(R.string.data_load_fail),
                        R.drawable.icon_error,
                        ContextCompat.getColor(context,R.color.star_yellow)).show();
                if(isLoadingNext) {
                    curPage--;
                }else{
                    RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
                    txt_netError.setVisibility(View.VISIBLE);
                    btn_toTop.setVisibility(View.GONE);
                }
                isLoadingNext = false;
                break;
        }
        Log.e(TAG,what + " Error: " + msg);
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
