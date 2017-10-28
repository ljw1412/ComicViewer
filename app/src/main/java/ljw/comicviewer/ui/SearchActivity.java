package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.Toast;

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

public class SearchActivity extends AppCompatActivity
        implements ComicService.RequestCallback{
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private List<Comic> comics = new ArrayList<>();
    private String keyword;
    private int curPage = 1;
    private int maxPage = -1;
    private boolean Searching = false;
    private boolean loading = false;
    private SearchListAdapter searchListAdapter;
    private LinearLayoutManager linearLayoutManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        ButterKnife.bind(this);
        initPTRGridView();
        initGridView();
        initTitleBar();
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
                curPage = 1;
                loadSearch(keyword,curPage);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                Toast.makeText(context, "上拉", Toast.LENGTH_SHORT).show();
                Glide.get(context).clearMemory();
                loading = true;
//                Glide.clear();
                loadSearch(keyword,++curPage);
                Log.d(TAG,"load next page; currentLoadingPage = "+curPage);
            }
        });
        //未加载时，禁用上拉下拉界面
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    private void initGridView() {
        listview = pullToRefreshListView.getRefreshableView();

        searchListAdapter = new SearchListAdapter(context,comics);
        listview.setAdapter(searchListAdapter);
//        listview.setOnScrollListener(this);
//        listview.setOnItemClickListener(new ComicGridFragment.ItemClickListener());
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
            curPage = 1;
            maxPage = -1;
            Toast.makeText(context,keyword,Toast.LENGTH_LONG).show();
            tipsView.setVisibility(View.GONE);
            comics.clear();
            searchListAdapter.notifyDataSetChanged();
            pullToRefreshListView.setFocusableInTouchMode(true);
            pullToRefreshListView.requestFocus();
            HideKeyboard(view);
            loadSearch(keyword,curPage);
        } else {
            Toast.makeText(context,"关键字不能为空白！",Toast.LENGTH_LONG).show();
        }
    }


    public void loadSearch(String keyword, int page){
        searchCall = ComicService.get().getComicSearch(this,keyword,page);
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
