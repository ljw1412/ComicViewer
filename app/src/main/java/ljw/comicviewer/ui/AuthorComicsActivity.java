package ljw.comicviewer.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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
import ljw.comicviewer.ui.adapter.SearchListAdapter;
import ljw.comicviewer.util.RefreshLayoutUtil;
import retrofit2.Call;

public class AuthorComicsActivity extends AppCompatActivity
        implements ComicService.RequestCallback{
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private List<Comic> comics = new ArrayList<>();
    private String aName,aMark;
    private int curPage = 1;
    private int maxPage = -1;
    private boolean flag = false;//是否调用备用方法
    private SearchListAdapter searchListAdapter;
    private Call loadCall;
    @BindView(R.id.nav_child_title)
    TextView nav_title;
    @BindView(R.id.author_comics_SmartRefreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.author_comics_listView)
    ListView listview;
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

    public void loadAuthorComics(){
        if(aMark!=null && !flag){
            loadCall = ComicService.get().getHTML(this,aMark+"index_p"+curPage+".html",Global.REQUEST_AUTHOR_COMICS);
        }else {
            //备用方法：使用搜索功能来找作者相关漫画
            loadCall = ComicService.get().getComicSearch(this, aName, curPage);
        }
    }


    private void initView(){
        initListView();
        setTitle(String.format(getString(R.string.title_author_comics),aName));
        //未加载时，禁用上拉下拉界面
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
        //设置主题色
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);
        //设置头部主题
        RefreshHeader refreshHeader = new ClassicsHeader(context);//使用经典主题
        refreshLayout.setRefreshHeader(refreshHeader);
        ((ClassicsHeader)refreshHeader).REFRESH_HEADER_REFRESHING =
                String.format(getString(R.string.title_author_comics),aName);
        ((ClassicsHeader)refreshHeader).REFRESH_HEADER_PULLDOWN =
                String.format(getString(R.string.title_author_comics),aName);
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
                Log.d(TAG,"load next page; currentLoadingPage = "+curPage);
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
        searchListAdapter = new SearchListAdapter(context,comics);
        listview.setAdapter(searchListAdapter);
//        listview.setOnScrollListener(this);
//        listview.setOnItemClickListener(new NewAddFragment.ItemClickListener());
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
            case Global.REQUEST_COMICS_SEARCH:
                AuthorComicsDataTask authorComicsDataTask = new AuthorComicsDataTask(data);
                authorComicsDataTask.execute();
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
        if(loadCall!=null && !loadCall.isCanceled()){
            loadCall.cancel();
            Log.d(TAG, "onDestroy: "+"取消网络请求！");
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
