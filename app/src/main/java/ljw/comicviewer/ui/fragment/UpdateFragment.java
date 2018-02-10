package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.adapter.ComicRecyclerViewAdapter;
import ljw.comicviewer.ui.listeners.OnItemClickListener;
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
        //只允许刷新，以便初次启动时自动刷新
        RefreshLayoutUtil.init(context,refreshLayout,
                RefreshLayoutUtil.Mode.Only_Refresh,true);
        //设置回顶按钮颜色
        btn_toTop.setBackgroundTintList(
                ThemeUtils.getThemeColorStateList(context,R.color.theme_color_primary));
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

        pictureGridAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Comic comic = comicList.get(position);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",comic.getComicId());
                intent.putExtra("score",comic.getScore());
                intent.putExtra("title",comic.getName());
                startActivity(intent);
            }
        });

        btn_toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerView != null && comicList.size()>0){
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    @Override
    public void initGridView() {
        //根据屏幕宽度设置列数
        int columns = DisplayUtil.getGridNumColumns(context,Global.ITEM_COMIC_VIEW_WIDTH);
        int itemWidth = (int) (DisplayUtil.getScreenWidthPX(context)/columns);
        pictureGridAdapter = new ComicRecyclerViewAdapter(context,comicList,itemWidth);
        recyclerView.setLayoutManager(new GridLayoutManager(context,columns));
        recyclerView.setAdapter(pictureGridAdapter);
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
                ruleStore.getUpdateRule().get("url"),days);
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
