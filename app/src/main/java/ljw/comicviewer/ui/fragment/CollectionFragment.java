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

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.db.CollectionHolder;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.HomeActivity;
import ljw.comicviewer.ui.adapter.PictureGridAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends BaseFragment
        implements AbsListView.OnScrollListener{
    private String TAG = NewAddFragment.class.getSimpleName()+"----";
    private Context context;
    private List<Comic> comics = new ArrayList<>();
    private List<Comic> allComics;
    private int currentPage = 1;
    private PictureGridAdapter pictureGridAdapter;
    private boolean isLoading = false;
    @BindView(R.id.comic_info_pull_refresh_grid)
    PullToRefreshGridView pullToRefreshGridView;
    GridView gridView;
    @BindView(R.id.grid_net_error)
    TextView txt_netError;
    @BindView(R.id.grid_loading)
    RelativeLayout loading;
    @BindView(R.id.title)
    TextView nav_title;

    public CollectionFragment() {}

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.bind(this,view);
        context = getActivity();
        initView();
        return view;
    }

    @Override
    public void initView() {
        //禁用上拉下拉
        pullToRefreshGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        initPTRGridView();
        initGridView();
        initLoad();
    }

    public void initPTRGridView() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        pullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                //下拉
                currentPage = 1;
                comics.clear();

                pictureGridAdapter.notifyDataSetChanged();
                // 获取对象，重新获取当前目录对象
                initLoad();
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                //上拉
                Glide.get(context).clearMemory();
                add20(++currentPage);
                Log.d(TAG,"load next page; currentLoadingPage = "+ currentPage);
            }
        });
    }

    public void initGridView() {
        gridView = pullToRefreshGridView.getRefreshableView();

        //根据屏幕宽度设置列数
//        int columns = DisplayUtil.getGridNumColumns(context,120);
//        gridView.setNumColumns(columns);

        pictureGridAdapter = new PictureGridAdapter(context,comics);
        gridView.setAdapter(pictureGridAdapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new ItemClickListener());
        pictureGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void initLoad() {
        //数据库处理
        currentPage = 1;
        comics.clear();
        CollectionHolder collectionHolder = new CollectionHolder(context);
        allComics = collectionHolder.getComics();
        if (getActivity() instanceof HomeActivity)
            ((HomeActivity) getActivity()).setTitle(nav_title,getString(R.string.txt_collection)+"("+allComics.size()+")");
        maxPage = allComics.size() % 20 > 0 ? (allComics.size() / 20 + 1) : allComics.size() / 20;
        add20(currentPage);
        loading.setVisibility(View.GONE);
        delayedFlushAdapter();
        isLoading = true;
    }


    private int maxPage;
    public void add20(int page){
        List<Comic> comicList = new ArrayList<>();
        for(int i = (page-1)*20 ; maxPage != 0 && i <(page == maxPage ? allComics.size() : page*20); i++){
            comicList.add(allComics.get(i));
        }
        comics.addAll(comicList);
        pictureGridAdapter.notifyDataSetChanged();
        pullToRefreshGridView.onRefreshComplete();
        if(currentPage == maxPage || maxPage == 0) pullToRefreshGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
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
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

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

    //网格对象点击事件
    class  ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Comic comic = comics.get(position);
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("id",comic.getComicId());
            intent.putExtra("score",comic.getScore());
            intent.putExtra("title",comic.getName());
            startActivityForResult(intent, Global.CollectionToDetails);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Global.CollectionToDetails:
                Log.d(TAG, "onActivityResult: "+data);
                if( data!=null ){
                    if(data.getBooleanExtra("like_change",false)){
                        initLoad();
                    }
                }
                break;
        }
    }
}
