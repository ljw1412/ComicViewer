package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.ui.adapter.PictureGridAdapter;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.RefreshLayoutUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends NewAddFragment {
    private String TAG = NewAddFragment.class.getSimpleName()+"----";
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
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
        //设置主题色
        refreshLayout.setPrimaryColors(DisplayUtil.getAttrColor(context,R.attr.colorPrimary));
        //下拉到底最后不自动加载，需要再拉一下
        refreshLayout.setEnableAutoLoadmore(false);
        //不在加载更多完成之后滚动内容显示新数据
        refreshLayout.setEnableScrollContentWhenLoaded(false);
        initGridView();
        addListener();
    }

    @Override
    public void addListener() {
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                add30(++loadPage);
                RefreshLayoutUtil.onFinish(refreshlayout);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadPage = 1;
                allList.clear();
                comicList.clear();
                pictureGridAdapter.notifyDataSetChanged();
                // 获取对象，重新获取当前目录对象
                getListItems(2);
            }
        });

        btn_toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gridView != null && comicList.size()>0){
                    gridView.smoothScrollToPosition(0);
                }
            }
        });
    }

    @Override
    public void initGridView() {
        pictureGridAdapter = new PictureGridAdapter(context,comicList);
        gridView.setAdapter(pictureGridAdapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new ItemClickListener());
        pictureGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void initLoad() {
        //自动刷新，获取数据
        refreshLayout.autoRefresh();
    }

    @Override
    public void getListItems(int days) {
        btn_toTop.setVisibility(View.GONE);
//        ComicService.get().getUpdateList(this,days);
        ComicService.get().getHTML(this,Global.REQUEST_COMICS_UPDATE,
                ruleStore.getLatestRule().get("url"),days);
    }


    private int maxPage;
    public void add30(int page){
        List<Comic> comics = new ArrayList<>();
        for(int i = (page-1)*30 ; i <(page == maxPage ? allList.size() : page*30); i++){
            comics.add(allList.get(i));
        }
        comicList.addAll(comics);
        pictureGridAdapter.notifyDataSetChanged();
        if(loadPage == maxPage) RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
    }

    @Override
    public Object myDoInBackground(String what, Object data) {
        switch (what) {
            case Global.REQUEST_COMICS_UPDATE:
                List<Comic> tempList = ComicFetcher.getLatestList(data.toString());
                if(tempList.size()>0) allList.addAll(tempList);
                return tempList.size();
        }
        return null;
    }

    @Override
    public void myOnPostExecute(String what, Object resultObj) {
        switch (what) {
            case Global.REQUEST_COMICS_UPDATE:
                if (resultObj!=null && (Integer)resultObj > 0) {
                    maxPage = allList.size() % 30 > 0 ? (allList.size() / 30 + 1) : allList.size() / 30;
                    add30(1);
                    pictureGridAdapter.notifyDataSetChanged();
                    //得到数据立刻取消刷新状态
                    RefreshLayoutUtil.onFinish(refreshLayout);
                    RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Both);
                    txt_netError.setVisibility(View.GONE);
                    btn_toTop.setVisibility(View.VISIBLE);
                    clearAndLoadImage();
                }else{
                    onError(getString(R.string.data_load_fail), what);
                }
                break;
        }
    }


    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_UPDATE:
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
