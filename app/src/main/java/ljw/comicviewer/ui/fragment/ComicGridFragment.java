package ljw.comicviewer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.ui.adapter.PictureGridAdapter;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.http.ComicFetcher;



/**
 * Created by ljw on 2017-08-23 023.
 */

public class ComicGridFragment extends Fragment
        implements OnRefreshListener, AbsListView.OnScrollListener, ComicService.RequestCallback {
    private String TAG = ComicGridFragment.class.getSimpleName()+"----";
    private Context context;
    private PictureGridAdapter pictureGridAdapter;
    private List<Comic> comicList = new ArrayList<>();
    private File myCache;
    @BindView(R.id.swipe_refresh_list)
    SwipeRefreshLayout fileListSwipe;
    @BindView(R.id.comic_info_view)
    GridView gridView;
    @BindView(R.id.grid_net_error)
    TextView txt_netError;

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

        //根据屏幕宽度设置列数
        int columns = DisplayUtil.getGridNumColumns(context,120);
        gridView.setNumColumns(columns);

        pictureGridAdapter = new PictureGridAdapter(context,comicList);
        gridView.setAdapter(pictureGridAdapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new ItemClickListener());
        myCache = context.getExternalCacheDir();

        //(3) 下拉刷新
        fileListSwipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fileListSwipe.setOnRefreshListener(this);//增加刷新方法
        getListItems(1);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //获得漫画列表对象并存入comicList
    private void getListItems(int page){
        ComicService.get().getListItems(this,page);
    }

    @Override
    public void onRefresh() {
        txt_netError.setVisibility(View.GONE);
        comicList.clear();
        imageState.clear();
        fileListSwipe.setRefreshing(true);
        // 获取对象，重新获取当前目录对象
        getListItems(1);
        //2秒刷新事件
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fileListSwipe.setRefreshing(false);
            }
        }, 2000);
    }

    //TODO:滑动事件--------
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
       
    }

    @Override
    public void onScroll(AbsListView absListView, int firstItem, int visibleItem, int totalItem) {
        //firstItem 为第一个可见对象的下标, visibleItem可见对象的数量, totalItem 可见对象的总数
//        getCover(firstItem,firstItem+visibleItem);
    }

    //TODO:网络请求，更新UI
    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_LIST:
                comicList.addAll(ComicFetcher.getComicList(data.toString()));
                Toast.makeText(context,"获得数据"+comicList.size(),Toast.LENGTH_LONG).show();
                pictureGridAdapter.notifyDataSetChanged();
                //得到数据立刻取消刷新状态
                fileListSwipe.setRefreshing(false);
                break;
            case Global.REQUEST_COMICS_IMAGE:
                CallBackData callBackData = (CallBackData) data;
                int position = (int) callBackData.getArg1();
                comicList.get(position).setCover((Bitmap) callBackData.getObj());
                imageState.put(position,1);
                pictureGridAdapter.notifyDataSetChanged();
                Log.d(TAG,callBackData.getMsg());
                break;
        }
    }

    @Override
    public void onError(String msg) {
        Log.e(TAG, "Error: " + msg);
        txt_netError.setVisibility(View.VISIBLE);
    }

    //-------------------

    //TODO:网格对象点击事件
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

    // map设定：键为null未加载 0加载中 1加载完毕
    private Map<Integer,Integer> imageState = new HashMap<>();
    //请求封面从start到end
    public void getCover(int start,int end){
        for (int i =start;i<end;i++) {
            if(imageState.get(i)!=null &&
                    (imageState.get(i)==0 || imageState.get(i)==1)){
                continue;
            }

            if(comicList.get(i).getImageUrl()!=null && comicList.get(i).getCover()==null){
                imageState.put(i,0);
                ComicService.get().getImage(this,comicList.get(i).getImageUrl(),i);
            }
        }

    }

}
