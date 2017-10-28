package ljw.comicviewer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.adapter.PictureGridAdapter;
import retrofit2.Call;


/**
 * Created by ljw on 2017-08-23 023.
 */

public class ComicGridFragment extends Fragment
        implements AbsListView.OnScrollListener, ComicService.RequestCallback  {
    private String TAG = ComicGridFragment.class.getSimpleName()+"----";
    private Context context;
    private PictureGridAdapter pictureGridAdapter;
    private List<Comic> comicList = new ArrayList<>();
    private File myCache;
    private int loadedPage = 1;
    private boolean isLoadingNext = false;
    // map设定：键为null未加载 0加载中 1加载完毕
    private Map<Integer,Integer> imageState = new HashMap<>();
    @BindView(R.id.comic_info_pull_refresh_grid)
    PullToRefreshGridView pullToRefreshGridView;
    GridView gridView;
    @BindView(R.id.grid_net_error)
    TextView txt_netError;
    @BindView(R.id.grid_loading)
    RelativeLayout loading;

    public ComicGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_comic_grid,null);
        ButterKnife.bind(this,rootView);

        initPTRGridView(rootView);
        initGridView();
        myCache = context.getExternalCacheDir();

        pullToRefreshGridView.setMode(PullToRefreshBase.Mode.DISABLED);

        getListItems(1);
        return rootView;
    }

    private void initPTRGridView(View view) {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        pullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(context, "下拉", Toast.LENGTH_SHORT).show();
                loadedPage = 1;
                comicList.clear();
                imageState.clear();
                pictureGridAdapter.notifyDataSetChanged();
                // 获取对象，重新获取当前目录对象
                getListItems(loadedPage);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(context, "上拉", Toast.LENGTH_SHORT).show();
                Glide.get(context).clearMemory();
                isLoadingNext = true;
//                Glide.clear();
                getListItems(++loadedPage);
                Log.d(TAG,"load next page; currentLoadingPage = "+loadedPage);
            }
        });
    }

    private void initGridView() {
        gridView = pullToRefreshGridView.getRefreshableView();

        //根据屏幕宽度设置列数
        gridView.setColumnWidth(GridView.AUTO_FIT);
//        int columns = DisplayUtil.getGridNumColumns(context,120);
//        gridView.setNumColumns(columns);

        pictureGridAdapter = new PictureGridAdapter(context,comicList);
        gridView.setAdapter(pictureGridAdapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new ItemClickListener());
        pictureGridAdapter.notifyDataSetChanged();
    }

    //获得漫画列表对象并存入comicList
    private void getListItems(int page){
        ComicService.get().getListItems(this,page);
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
//                Glide.with(context).pauseRequests();//暂停请求
                break;
            case SCROLL_STATE_IDLE:
                //停止滑动状态
//                Glide.with(context).resumeRequests();//重启请求
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
                    Log.d(TAG, "clearImage: "+i);
                    image.setImageBitmap(null);
                    image.setImageDrawable(null);
                    EndTag.setImageResource(0);
                }else{
                    Log.d(TAG, "loadImage: "+i);
                    pictureGridAdapter.loadCover(i,view);
                }
            }
        }
        System.gc();
        pictureGridAdapter.notifyDataSetChanged();
    }

    //网络请求，更新UI
    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_LIST:
                comicList.addAll(ComicFetcher.getComicList(data.toString()));
//                Toast.makeText(context,"获得数据"+comicList.size(),Toast.LENGTH_LONG).show();
                pictureGridAdapter.notifyDataSetChanged();
                //得到数据立刻取消刷新状态
                pullToRefreshGridView.onRefreshComplete();
                txt_netError.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                pullToRefreshGridView.setMode(PullToRefreshBase.Mode.BOTH);
                isLoadingNext = false;
                clearAndLoadImage();
                break;
        }
    }

    @Override
    public void onError(String msg ,String what) {
        switch (what){
            case Global.REQUEST_COMICS_LIST:
                pullToRefreshGridView.onRefreshComplete();
                if(isLoadingNext) {
                    Toast.makeText(context, R.string.gird_tips_loading_next_page_fail, Toast.LENGTH_LONG).show();
                }else{
                    pullToRefreshGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    loading.setVisibility(View.GONE);
                    txt_netError.setVisibility(View.VISIBLE);
                }
                isLoadingNext = false;
                break;
        }
        Log.e(TAG, "Error: " + msg);

    }

    //-------------------

    //网格对象点击事件
    class  ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Comic comic = comicList.get(position);
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("id",comic.getId());
            intent.putExtra("score",comic.getScore());
            intent.putExtra("title",comic.getName());
            startActivity(intent);
        }
    }



}
