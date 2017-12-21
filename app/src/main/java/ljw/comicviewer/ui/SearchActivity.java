package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.adapter.SearchListAdapter;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.RefreshLayoutUtil;
import ljw.comicviewer.util.SnackbarUtil;
import ljw.comicviewer.util.StringUtil;
import retrofit2.Call;

public class SearchActivity extends AppCompatActivity
        implements ComicService.RequestCallback{
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private List<Comic> comics = new ArrayList<>();
    private String keyword;
    private int curPage = 1;
    private int maxPage = -1;
    private boolean loading = false;
    RuleStore ruleStore = RuleStore.get();
    private SearchListAdapter searchListAdapter;
    private RefreshHeader refreshHeader;
    private Call<String> searchCall;
    private Snackbar notEmptySnackBar;
    @BindView(R.id.search_button)
    Button btn_search;
    @BindView(R.id.search_edit)
    EditText edit_search;
    @BindView(R.id.search_SmartRefreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.search_listView)
    ListView listview;
    @BindView(R.id.search_not_found)
    RelativeLayout tipsView;
    @BindView(R.id.tips_search_by_id)
    TextView txt_searchById;
    @BindView(R.id.search_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        ButterKnife.bind(this);
        initView();
        addListener();
        initListView();
    }

    private void initView(){
        //初始化时，禁用上拉下拉界面
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Disable);
        //设置主题色
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);
        //设置头部主题
        refreshHeader = new ClassicsHeader(context);//使用经典主题
        refreshLayout.setRefreshHeader(new ClassicsHeader(context));
        ((ClassicsHeader)refreshHeader).REFRESH_HEADER_FINISH = "搜索完成";
        ((ClassicsHeader)refreshHeader).REFRESH_HEADER_FAILED = "搜索失败";
        ((ClassicsHeader)refreshHeader).REFRESH_HEADER_LASTTIME = "上次搜索 M-d HH:mm";
        //初始化未输入内容的提示
        notEmptySnackBar = SnackbarUtil.newAddImageColorfulSnackar(
                coordinatorLayout, getString(R.string.alert_search_keyword_no_empty),
                R.drawable.icon_error,
                ContextCompat.getColor(context,R.color.star_yellow));
    }

    private void addListener() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉
                ++curPage;
                loadSearch(keyword);
                Log.d(TAG,"load next page; currentLoadingPage = "+curPage);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉
                curPage = 1;
                maxPage = -1;
                comics.clear();
                searchListAdapter.notifyDataSetChanged();
                loadSearch(keyword);
            }
        });
        //列表对象点击事件
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Comic comic = comics.get(position);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",comic.getComicId());
                intent.putExtra("score",comic.getScore());
                intent.putExtra("title",comic.getName());
                startActivity(intent);
            }
        });
        //搜索文本框编辑事件
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //按下回车键
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    searching(textView);
                }
                return true;
            }
        });
        //搜索按钮点击事件
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searching(view);
            }
        });
    }

    private void initListView() {
        searchListAdapter = new SearchListAdapter(context,comics);
        listview.setAdapter(searchListAdapter);
        searchListAdapter.notifyDataSetChanged();
    }


    public void searching(View view){
        keyword = edit_search.getText().toString();
        if(keyword.matches("id:(\\d+)/")){
            String comicId = StringUtil.getPattern("id:(\\d+)/",keyword,1);
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("id",comicId);
            startActivity(intent);
            return;
        }
        if (!keyword.trim().equals("")){
            //如果处于上一次请求，不允许下一次请求
            if(loading){
                return;
            }
            if(notEmptySnackBar!=null && notEmptySnackBar.isShown())
                notEmptySnackBar.dismiss();

            tipsView.setVisibility(View.GONE);
            txt_searchById.setVisibility(View.GONE);
            //隐藏虚拟键盘
            DisplayUtil.hideKeyboard(view);

            ((ClassicsHeader)refreshHeader).REFRESH_HEADER_REFRESHING =
                    String.format(getString(R.string.alert_search_loading_tips),keyword);
            ((ClassicsHeader)refreshHeader).REFRESH_HEADER_PULLDOWN =
                    String.format(getString(R.string.alert_search_loading_tips),keyword);
            if(loading){

            }

            RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
            refreshLayout.autoRefresh(100);

        } else {
            if(notEmptySnackBar!=null && !notEmptySnackBar.isShown())
                notEmptySnackBar.show();
        }
    }


    public void loadSearch(String keyword){
        searchCall = ComicService.get().getHTML(this,Global.REQUEST_COMICS_SEARCH,
                ruleStore.getSearchRule().get("url"),keyword,curPage);
        loading = true;
    }


    public void onBack(View view) {
        finish();
    }

    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_SEARCH:
                SearchDataTask searchDataTask = new SearchDataTask(data);
                searchDataTask.execute();
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {
        Log.e(TAG, what+" Error: " + msg);
        SnackbarUtil.newAddImageColorfulSnackar(
                coordinatorLayout, getString(R.string.error_network_connections),
                R.drawable.icon_error,
                ContextCompat.getColor(context,R.color.holo_red_light)).show();
        refreshLayout.finishRefresh(false);
        loading = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(searchCall!=null && !searchCall.isCanceled()){
            searchCall.cancel();
            Log.d(TAG, "onDestroy: "+"取消网络请求！");
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
            callbackdata = ComicFetcher.getSearchResults(data.toString());
            comics.addAll((List<Comic>) callbackdata.getObj());
            return comics.size()>0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(!aBoolean){
                tipsView.setVisibility(View.VISIBLE);
            }
            if(maxPage == -1){
                maxPage = (int) callbackdata.getArg1();
            }
            RefreshLayoutUtil.onFinish(refreshLayout);
            if(curPage >= maxPage){
                RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Disable);
            }else {
                RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_LoadMore);
            }
            searchListAdapter.notifyDataSetChanged();
            loading = false;
        }
    }
}
