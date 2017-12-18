package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.db.CollectionHolder;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.HomeActivity;
import ljw.comicviewer.ui.adapter.PictureGridAdapter;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.RefreshLayoutUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends BaseFragment
        implements AbsListView.OnScrollListener{
    private String TAG = NewAddFragment.class.getSimpleName()+"----";
    private Context context;
    private List<Comic> comics = new ArrayList<>();
    private List<Comic> searchComics = new ArrayList<>();
    private List<Comic> allComics;
    private int currentPage = 1;
    private PictureGridAdapter pictureGridAdapter;
    private PictureGridAdapter searchGridAdapter,normalGridAdapter;
    private boolean loading = false;
    private boolean searching = false;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.grid_view)
    GridView gridView;
    @BindView(R.id.grid_net_error)
    TextView txt_netError;
    @BindView(R.id.title)
    TextView nav_title;
    @BindView(R.id.nav_btn_search)
    ImageView btn_search;
    @BindView(R.id.nav_search_view)
    RelativeLayout view_search;//搜索界面
    @BindView(R.id.nav_btn_cancel)
    ImageView btn_cancel;//取消按钮
    @BindView(R.id.nav_search_edit)
    EditText edit_search;//输入框

    public CollectionFragment() {}

    public boolean isLoading() {
        return loading;
    }

    public boolean isSearching() {
        return searching;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.bind(this,view);
        context = getActivity();
        initView();
        //数据首次加载
        initLoad();
        return view;
    }

    @Override
    public void initView() {
        //只允许刷新，以便初次启动时自动刷新
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
        //设置主题色
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary,R.color.white);
        //下拉到底最后不自动加载，需要再拉一下
        refreshLayout.setEnableAutoLoadmore(false);
        //不在加载更多完成之后滚动内容显示新数据
        refreshLayout.setEnableScrollContentWhenLoaded(false);
        if (getActivity() instanceof HomeActivity)
             ((HomeActivity) getActivity()).setTitle(nav_title,getString(R.string.txt_collection));
        initGridView();
        addListener();
    }

    public void initGridView() {
        searchGridAdapter = new PictureGridAdapter(context, searchComics);//搜索用适配器
        normalGridAdapter = new PictureGridAdapter(context,comics);//平时用适配器
        pictureGridAdapter = normalGridAdapter;//设置当前适配器为正常
        gridView.setAdapter(pictureGridAdapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new ItemClickListener());
        pictureGridAdapter.notifyDataSetChanged();
    }

    public void addListener() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉
                add20(++currentPage);
                Log.d(TAG,"load next page; currentLoadingPage = "+ currentPage);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉
                currentPage = 1;
                comics.clear();
                searchComics.clear();
                //重新获取当前目录对象
                getDataFromDB();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSearchMode();
            }
        });
        //取消搜索按钮点击事件
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNormalMode();
            }
        });
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //按下回车键
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //隐藏虚拟键盘
                    DisplayUtil.hideKeyboard(edit_search);
                }
                return true;
            }
        });
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged: "+charSequence);
                if(charSequence.length()>0){
                    pictureGridAdapter = searchGridAdapter;
                }else{
                    pictureGridAdapter = normalGridAdapter;
                }
                gridView.setAdapter(pictureGridAdapter);

                search(charSequence.toString());
                clearAndLoadImage();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void initLoad() {
        //取消搜索状态,恢复正常模式
        if(searching) {
            changeNormalMode();
        }
        loading = true;
        //执行自动刷新,此处不采用动画自动加载
//        refreshLayout.autoRefresh();
        currentPage = 1;
        comics.clear();
        searchComics.clear();
        getDataFromDB();


    }
    //切换为搜索模式
    private void changeSearchMode(){
        RefreshLayoutUtil.setMode(refreshLayout,RefreshLayoutUtil.Mode.Only_LoadMore);
        view_search.setVisibility(View.VISIBLE);
        edit_search.requestFocus();
        //显示虚拟键盘
        DisplayUtil.showKeyboard(edit_search);
        searching = true;
    }
    //切换为正常模式
    public void changeNormalMode(){
        RefreshLayoutUtil.setMode(refreshLayout,RefreshLayoutUtil.Mode.Both);
        view_search.setVisibility(View.GONE);
        edit_search.setText(null);
        //隐藏虚拟键盘
        DisplayUtil.hideKeyboard(edit_search);
        pictureGridAdapter = normalGridAdapter;
        gridView.setAdapter(pictureGridAdapter);
        searching = false;
    }

    //从数据库获得的数据
    public void getDataFromDB(){
        //数据库处理，获取对象
        CollectionHolder collectionHolder = new CollectionHolder(context);
        allComics = collectionHolder.getComics();
        if (getActivity() instanceof HomeActivity)
            ((HomeActivity) getActivity()).setTitle(nav_title,getString(R.string.txt_collection)+"("+allComics.size()+")");
        maxPage = allComics.size() % 20 > 0 ? (allComics.size() / 20 + 1) : allComics.size() / 20;
        add20(currentPage);
        delayedFlushAdapter();
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Both);
    }

    private int maxPage;
    public void add20(int page){
        List<Comic> comicList = new ArrayList<>();
        for(int i = (page-1)*20 ; maxPage != 0 && i <(page == maxPage ? allComics.size() : page*20); i++){
            comicList.add(allComics.get(i));
        }
        comics.addAll(comicList);
        pictureGridAdapter.notifyDataSetChanged();
        RefreshLayoutUtil.onFinish(refreshLayout);
        if(currentPage == maxPage || maxPage == 0)
            RefreshLayoutUtil.setMode(refreshLayout,RefreshLayoutUtil.Mode.Only_Refresh);
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
                    pictureGridAdapter.loadCover(i, view);
                }
            }
        }
        System.gc();
        pictureGridAdapter.notifyDataSetChanged();
    }

    //搜索事件
    private void search(String keyword){
        searchComics.clear();
        for(Comic comic:comics){
            if(comic.getName().contains(keyword)||
                    (comic.getAuthor()!=null && comic.getAuthor().contains(keyword))){
                searchComics.add(comic);
            }
        }
    }



    //网格对象点击事件
    class  ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Comic comic = comics.get(position);
            if(searching){
                comic = searchComics.get(position);
            }
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
