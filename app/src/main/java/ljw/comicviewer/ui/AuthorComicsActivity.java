package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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
import retrofit2.Call;

public class AuthorComicsActivity extends AppCompatActivity
        implements ComicService.RequestCallback{
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private List<Comic> comics = new ArrayList<>();
    private String aName,aMark;
    private int curPage = 1;
    private int maxPage = -1;
    private SearchListAdapter searchListAdapter;
    private Call loadCall;
    @BindView(R.id.nav_child_title)
    TextView nav_title;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView pullToRefreshListView;
    ListView listview;
    @BindView(R.id.search_not_found)
    RelativeLayout tipsView;
    @BindView(R.id.loading)
    RelativeLayout view_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_comics);
        context = this;
        ButterKnife.bind(this);
        aName = getIntent().getStringExtra("aName");
        aMark = getIntent().getStringExtra("aMark");
        initView();
        initPTRGridView();
        initListView();
        loadAuthorComics();
    }

    public void loadAuthorComics(){
        view_loading.setVisibility(View.VISIBLE);
        //备用方法：使用搜索功能来找作者相关漫画
        loadCall = ComicService.get().getComicSearch(this,aName,curPage);
    }


    private void initView(){
        setTitle(String.format(getString(R.string.title_author_comics),aName));
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    }

    private void initPTRGridView() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                Toast.makeText(context, "下拉", Toast.LENGTH_SHORT).show();
                curPage = 1;
                comics.clear();
                searchListAdapter.notifyDataSetChanged();
                loadAuthorComics();
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                Toast.makeText(context, "上拉", Toast.LENGTH_SHORT).show();
                Glide.get(context).clearMemory();
                ++curPage;
                loadAuthorComics();
                Log.d(TAG,"load next page; currentLoadingPage = "+curPage);
            }
        });
        //未加载时，禁用上拉下拉界面
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    private void initListView() {
        listview = pullToRefreshListView.getRefreshableView();

        searchListAdapter = new SearchListAdapter(context,comics);
        listview.setAdapter(searchListAdapter);
//        listview.setOnScrollListener(this);
//        listview.setOnItemClickListener(new ComicGridFragment.ItemClickListener());
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
                //
            case Global.REQUEST_COMICS_SEARCH:
                String html = (String) data;
                CallBackData callbackdata = ComicFetcher.getSearchResults(html);
                comics.addAll((List<Comic>) callbackdata.getObj());
                view_loading.setVisibility(View.GONE);
                if(comics.size()==0){
                    tipsView.setVisibility(View.VISIBLE);
                }
                if(maxPage == -1){
                    maxPage = (int) callbackdata.getArg1();
                }
                pullToRefreshListView.onRefreshComplete();
                pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                searchListAdapter.notifyDataSetChanged();
                if(curPage == maxPage){
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loadCall!=null && !loadCall.isCanceled()){
            loadCall.cancel();
            Log.d(TAG, "onDestroy: "+"取消网络请求！");
        }
    }
}
