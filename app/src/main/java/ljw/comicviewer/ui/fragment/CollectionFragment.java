package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
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
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.HomeActivity;
import ljw.comicviewer.ui.adapter.ComicRecyclerViewAdapter;
import ljw.comicviewer.ui.dialog.ThemeDialog;
import ljw.comicviewer.ui.listeners.OnItemClickListener;
import ljw.comicviewer.ui.listeners.OnItemLongClickListener;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.RefreshLayoutUtil;
import ljw.comicviewer.util.ThemeUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends BaseFragment
        implements AbsListView.OnScrollListener{
    private String TAG = NewAddFragment.class.getSimpleName()+"----";
    private Context context;
    private List<Comic> comics = new ArrayList<>();
    private List<Comic> searchComics = new ArrayList<>();
    private ComicRecyclerViewAdapter pictureGridAdapter;
    private ComicRecyclerViewAdapter searchGridAdapter,normalGridAdapter;
    private boolean loading = false;
    private boolean searching = false;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
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
    @BindView(R.id.btn_toTop)
    FloatingActionButton btn_toTop;

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
        refreshLayout.setPrimaryColors(ThemeUtil.getThemeColor(context));
        //下拉到底最后不自动加载，需要再拉一下
        refreshLayout.setEnableAutoLoadmore(false);
        //不在加载更多完成之后滚动内容显示新数据
        refreshLayout.setEnableScrollContentWhenLoaded(false);
        //设置回顶按钮颜色
        btn_toTop.setBackgroundTintList(ColorStateList.valueOf(ThemeUtil.getThemeColor(context)));

        if (getActivity() instanceof HomeActivity)
             ((HomeActivity) getActivity()).setTitle(nav_title,getString(R.string.txt_collection));
        initGridView();
        addListener();
    }

    public void initGridView() {
        //根据屏幕宽度设置列数
        int columns = DisplayUtil.getGridNumColumns(context,120);
        int itemWidth = (int) (DisplayUtil.getScreenWidthPX(context)/columns);
        searchGridAdapter = new ComicRecyclerViewAdapter(context, searchComics,itemWidth);//搜索用适配器
        normalGridAdapter = new ComicRecyclerViewAdapter(context,comics,itemWidth);//平时用适配器
        pictureGridAdapter = normalGridAdapter;//设置当前适配器为正常
        recyclerView.setLayoutManager(new GridLayoutManager(context,columns));
        recyclerView.setAdapter(pictureGridAdapter);
        pictureGridAdapter.notifyDataSetChanged();
    }

    public void addListener() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉
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
                recyclerView.setAdapter(pictureGridAdapter);

                search(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //设置网格元素长按事件
        normalGridAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                Comic comic = comics.get(position);
                if(searching){
                    comic = searchComics.get(position);
                }
                showItemDialog(comic);
                Log.d(TAG, "onItemLongClick: "+comic.getName());
                return true;
            }
        });
        searchGridAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                Comic comic = comics.get(position);
                if(searching){
                    comic = searchComics.get(position);
                }
                showItemDialog(comic);
                Log.d(TAG, "onItemLongClick: "+comic.getName());
                return true;
            }
        });

        //网格点击事件
        normalGridAdapter.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void OnItemClick(View view, int position) {
                Comic comic = searching ? searchComics.get(position) : comics.get(position);
                goToDetails(comic);
            }
        });
        searchGridAdapter.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void OnItemClick(View view, int position) {
                Comic comic = searching ? searchComics.get(position) : comics.get(position);
                goToDetails(comic);
            }
        });

        btn_toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void showItemDialog(final Comic comic){
        if (comic==null) return;
        String[] items = {"查看详情","删除收藏"};
        ThemeDialog themeDialog = new ThemeDialog(context);
        themeDialog.setTitle(comic.getName())
                .setItems(items,new ThemeDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                //查看详情
                                goToDetails(comic);
                                break;
                            case 1:
                                //删除收藏
                                CollectionHolder collectionHolder = new CollectionHolder(context);
                                collectionHolder.deleteComic(comic.getComicId());
                                pictureGridAdapter.remove(comic.getComicId());
                                break;
                        }
                        dialog.dismiss();
                    }
        });
        themeDialog.show();
    }


    @Override
    public void initLoad() {
        //取消搜索状态,恢复正常模式
        if(searching) {
            changeNormalMode();
        }
        btn_toTop.setVisibility(View.GONE);
        loading = true;

        comics.clear();
        searchComics.clear();
        getDataFromDB();
        pictureGridAdapter.notifyDataSetChanged();
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
        recyclerView.setAdapter(pictureGridAdapter);
        searching = false;
    }

    //从数据库获得的数据
    public void getDataFromDB(){
        //数据库处理，获取对象
        CollectionHolder collectionHolder = new CollectionHolder(context);
        List<Comic> allComics = collectionHolder.getComics(RuleStore.get().getComeFrom());
        if (getActivity() instanceof HomeActivity)
            ((HomeActivity) getActivity()).setTitle(nav_title,getString(R.string.txt_collection)+"("+allComics.size()+")");
        comics.addAll(allComics);
        if (allComics.size()>0) btn_toTop.setVisibility(View.VISIBLE);
        RefreshLayoutUtil.onFinish(refreshLayout);
    }

    //搜索事件
    private void search(String keyword){
        searchComics.clear();
        for(Comic comic:comics){
            if(comic.getName().toLowerCase().contains(keyword.toLowerCase())){
                searchComics.add(comic);
            }
        }
    }

    private void goToDetails(Comic comic){
        if(comic!=null && comic.getComicId().length()>0){
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
                        String delComicId = data.getStringExtra("comicId");
                        if(delComicId != null){
                            pictureGridAdapter.remove(delComicId);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
}
