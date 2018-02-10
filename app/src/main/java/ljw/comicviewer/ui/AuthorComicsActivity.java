package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
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
import ljw.comicviewer.ui.adapter.SearchRecyclerViewAdapter;
import ljw.comicviewer.ui.listeners.OnItemClickListener;
import ljw.comicviewer.util.RefreshLayoutUtil;
import retrofit2.Call;

/**
 * 作者相关界面
 */
public class AuthorComicsActivity extends BaseActivity
        implements ComicService.RequestCallback {
    private String TAG = this.getClass().getSimpleName() + "----";
    private Context context;
    private List<Comic> comics = new ArrayList<>();
    private String aName, aMark;
    private int curPage = 1;
    private int maxPage = -1;
    private boolean flag = false;//是否调用备用方法
    RuleStore ruleStore = RuleStore.get();
    private SearchRecyclerViewAdapter searchListAdapter;
    private Call loadCall;
    @BindView(R.id.nav_child_title)
    TextView nav_title;
    @BindView(R.id.author_comics_SmartRefreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.search_not_found)
    RelativeLayout tipsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_comics);
        context = this;
        ButterKnife.bind(this);
        aName = getIntent().getStringExtra("aName");
        aMark = getIntent().getStringExtra("aMark");
        initView();
        addListener();
    }

    public void loadAuthorComics() {
        if (aMark != null && !flag) {
            loadCall = ComicService.get().getHTML(this, Global.REQUEST_AUTHOR_COMICS,
                    ruleStore.getAuthorRule().get("url"), aMark, curPage);
        } else {
            //备用方法：使用搜索功能来找作者相关漫画
            loadCall = ComicService.get().getHTML(this, Global.REQUEST_COMICS_SEARCH,
                    ruleStore.getSearchRule().get("url"), aName, curPage);
        }
    }


    private void initView() {
        initListView();
        setTitle(String.format(getString(R.string.title_author_comics), aName));
        //只允许刷新，以便初次启动时自动刷新
        RefreshLayoutUtil.init(context, refreshLayout,
                RefreshLayoutUtil.Mode.Only_Refresh, true);
        //设置头部主题
        RefreshHeader refreshHeader = new ClassicsHeader(context);//使用经典主题
        refreshLayout.setRefreshHeader(refreshHeader);
        ((ClassicsHeader) refreshHeader).REFRESH_HEADER_REFRESHING =
                String.format(getString(R.string.title_author_comics), aName);
        ((ClassicsHeader) refreshHeader).REFRESH_HEADER_PULLDOWN =
                String.format(getString(R.string.title_author_comics), aName);
        refreshLayout.autoRefresh();
    }

    private void addListener() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉
                ++curPage;
                loadAuthorComics();
                Log.d(TAG, "load next page; currentLoadingPage = " + curPage);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉
                curPage = 1;
                comics.clear();
                searchListAdapter.notifyDataSetChanged();
                loadAuthorComics();
            }
        });
    }

    private void initListView() {
        searchListAdapter = new SearchRecyclerViewAdapter(context, comics);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(searchListAdapter);
        searchListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Comic comic = comics.get(position);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",comic.getComicId());
                intent.putExtra("score",comic.getScore());
                intent.putExtra("title",comic.getName());
                startActivity(intent);
            }
    });
    searchListAdapter.notifyDataSetChanged();
}

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }

    @Override
    public void setTitle(CharSequence title) {
        nav_title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        nav_title.setText(getString(titleId));
    }

    @Override
    public void onFinish(Object data, String what) {
        switch (what) {
            case Global.REQUEST_AUTHOR_COMICS:
                AuthorComicsDataTask authorComicsDataTask = new AuthorComicsDataTask(data);
                authorComicsDataTask.execute();
                break;
            case Global.REQUEST_COMICS_SEARCH:
                SearchDataTask searchDataTask = new SearchDataTask(data);
                searchDataTask.execute();
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {
        switch (what) {
            case Global.REQUEST_AUTHOR_COMICS:
                flag = true;
                maxPage = -1;
                loadAuthorComics();
                break;
            case Global.REQUEST_COMICS_SEARCH:
                tipsView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadCall != null && !loadCall.isCanceled()) {
            loadCall.cancel();
            Log.d(TAG, "onDestroy: " + "取消网络请求！");
        }
    }

class AuthorComicsDataTask extends AsyncTask<Void,Void,Boolean>{
        private Object data;
        private CallBackData callbackdata;

        public AuthorComicsDataTask(Object data) {
            this.data = data;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String html = (String) data;
            callbackdata = ComicFetcher.getAuthorResults(html);
            comics.addAll((List<Comic>) callbackdata.getObj());
            return comics.size()>0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(maxPage == -1){
                maxPage = (int) callbackdata.getArg1();
            }
            Log.d(TAG, "onPostExecute: "+maxPage);
            if(!aBoolean){
                if (!flag){
                    flag = true;
                    maxPage = -1;
                    loadAuthorComics();
                }else {
                    tipsView.setVisibility(View.VISIBLE);
                }
            }
            RefreshLayoutUtil.onFinish(refreshLayout);
            if(curPage >= maxPage){
                RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Disable);
            }else {
                RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_LoadMore);
            }
            searchListAdapter.notifyDataSetChanged();
        }
    }

    class SearchDataTask extends AsyncTask<Void,Void,Boolean>{
        private Object data;
        private CallBackData callbackdata;

        public SearchDataTask(Object data) {
            this.data = data;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String html = (String) data;
            callbackdata = ComicFetcher.getSearchResults(html);
            comics.addAll((List<Comic>) callbackdata.getObj());
            return comics.size()>0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(maxPage == -1){
                maxPage = (int) callbackdata.getArg1();
            }
            Log.d(TAG, "onPostExecute: "+maxPage);
            if(!aBoolean){
                if (!flag){
                    flag = true;
                    maxPage = -1;
                    loadAuthorComics();
                }else {
                    tipsView.setVisibility(View.VISIBLE);
                }
            }
            RefreshLayoutUtil.onFinish(refreshLayout);
            if(curPage >= maxPage){
                RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Disable);
            }else {
                RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_LoadMore);
            }
            searchListAdapter.notifyDataSetChanged();
        }
    }
}
