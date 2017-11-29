package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.ui.adapter.PictureGridAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends ComicGridFragment{
    private String TAG = ComicGridFragment.class.getSimpleName()+"----";
    private Context context;
    private List<Comic> allList = new ArrayList<>();
    private int loadPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        //禁用上拉下拉
        pullToRefreshGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        initPTRGridView();
        initGridView();
    }

    @Override
    public void initPTRGridView() {
        pullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                loadPage = 1;
                allList.clear();
                comicList.clear();
                pictureGridAdapter.notifyDataSetChanged();
                // 获取对象，重新获取当前目录对象
                initLoad();
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                add30(++loadPage);
                pullToRefreshGridView.onRefreshComplete();
            }
        });
    }

    @Override
    public void initGridView() {
        gridView = pullToRefreshGridView.getRefreshableView();

        pictureGridAdapter = new PictureGridAdapter(context,comicList);
        gridView.setAdapter(pictureGridAdapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new ItemClickListener());
        pictureGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void initLoad() {
        getListItems(2);
    }

    @Override
    public void getListItems(int days) {
        ComicService.get().getUpdateList(this,days);
    }


    private int maxPage;
    public void add30(int page){
        List<Comic> comics = new ArrayList<>();
        for(int i = (page-1)*30 ; i <(page == maxPage ? allList.size() : page*30); i++){
            comics.add(allList.get(i));
        }
        comicList.addAll(comics);
        pictureGridAdapter.notifyDataSetChanged();
        if(loadPage == maxPage) pullToRefreshGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    public Object myDoInBackground(String TAG, Object data) {
        switch (TAG) {
            case Global.REQUEST_COMICS_LATEST:
                List<Comic> tempList = ComicFetcher.getLatestList(data.toString());
                if(tempList.size()>0) allList.addAll(tempList);
                return tempList.size();
        }
        return null;
    }

    @Override
    public void myOnPostExecute(String TAG,Object resultObj) {
        switch (TAG) {
            case Global.REQUEST_COMICS_LATEST:
                if (resultObj!=null && (Integer)resultObj > 0) {
                    maxPage = allList.size() % 30 > 0 ? (allList.size() / 30 + 1) : allList.size() / 30;
                    pullToRefreshGridView.setMode(PullToRefreshBase.Mode.BOTH);
                    add30(1);
                    pictureGridAdapter.notifyDataSetChanged();
                    //得到数据立刻取消刷新状态
                    pullToRefreshGridView.onRefreshComplete();
                    txt_netError.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    clearAndLoadImage();
                }else{
                    netErrorTo();
//                    Toast.makeText(context,getString(R.string.data_load_fail),Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_LATEST:
                UIUpdateTask UIUpdateTask = new UIUpdateTask(what,data);
                UIUpdateTask.execute();
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {
        super.onError(msg, what);
    }
}
