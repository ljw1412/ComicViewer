package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import ljw.comicviewer.util.SnackbarUtil;
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
    private SearchListAdapter searchListAdapter;
    private Snackbar snackbar;
    private Call searchCall;
    @BindView(R.id.search_button)
    Button btn_search;
    @BindView(R.id.search_edit)
    EditText edit_search;
    @BindView(R.id.search_pull_refresh_list)
    PullToRefreshListView pullToRefreshListView;
    ListView listview;
    @BindView(R.id.search_not_found)
    RelativeLayout tipsView;
    @BindView(R.id.loading)
    RelativeLayout view_loading;
    @BindView(R.id.search_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        ButterKnife.bind(this);
        initPTRGridView();
        initListView();
        initTitleBar();
    }

    private void initPTRGridView() {
        // 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉
                curPage = 1;
                comics.clear();
                searchListAdapter.notifyDataSetChanged();
                loadSearch(keyword);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉
                Glide.get(context).clearMemory();
                ++curPage;
                loadSearch(keyword);
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
        searchListAdapter.notifyDataSetChanged();
    }

    private void initTitleBar(){
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                   searching(textView);
                }
                return true;
            }
        });

        btn_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //按住
                        btn_search.setBackgroundResource(R.color.black_shadow);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        //抬起
                        btn_search.setBackgroundResource(R.color.blue_A1E0F4);
                        break;
                }
                return false;
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searching(view);
            }
        });
    }

    public void searching(View view){
        keyword = edit_search.getText().toString();
        if (!keyword.trim().equals("")){
            if(loading){
                searchCall.cancel();
            }
            curPage = 1;
            maxPage = -1;
            snackbar = SnackbarUtil.newAddImageColorfulSnackar(
                    coordinatorLayout,
                    String.format(getString(R.string.alert_search_loading_tips),keyword),
                    R.drawable.icon_loading,
                    ContextCompat.getColor(context,R.color.smmcl_green));
            snackbar.show();
            tipsView.setVisibility(View.GONE);
            comics.clear();
            searchListAdapter.notifyDataSetChanged();
            pullToRefreshListView.setFocusableInTouchMode(true);
            pullToRefreshListView.requestFocus();
            HideKeyboard(view);
            loadSearch(keyword);
        } else {
            SnackbarUtil.newAddImageColorfulSnackar(
                    coordinatorLayout, getString(R.string.alert_search_keyword_no_empty),
                    R.drawable.icon_error,
                    ContextCompat.getColor(context,R.color.star_yellow)).show();
        }
    }


    public void loadSearch(String keyword){
        searchCall = ComicService.get().getComicSearch(this,keyword,curPage);
        loading = true;
        view_loading.setVisibility(View.VISIBLE);
    }

    //隐藏虚拟键盘
    public static void HideKeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive() ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken() , 0 );
        }
    }


    public void onBack(View view) {
        finish();
    }

    @Override
    public void onFinish(Object data, String what) {
        switch (what){
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
                loading = false;
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
        if(searchCall!=null && !searchCall.isCanceled()){
            searchCall.cancel();
            Log.d(TAG, "onDestroy: "+"取消网络请求！");
        }
    }
}
