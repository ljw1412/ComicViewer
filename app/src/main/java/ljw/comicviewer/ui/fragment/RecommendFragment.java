package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.bean.Section;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.DetailsActivity;
import ljw.comicviewer.ui.FilterActivity;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.RefreshLayoutUtil;
import ljw.comicviewer.util.SnackbarUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendFragment extends BaseFragment implements ComicService.RequestCallback{
    private String TAG = NewAddFragment.class.getSimpleName()+"----";
    private Context context;
    RuleStore ruleStore = RuleStore.get();
    List<Section> sections = new ArrayList<>();
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.recommend_container)
    LinearLayout container;
    @BindView(R.id.recommend_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        context = getActivity();
        ButterKnife.bind(this,view);
        initView();
        initLoad();
        return view;
    }

    @Override
    public void initView() {
        //只能下拉刷新
        RefreshLayoutUtil.setMode(refreshLayout, RefreshLayoutUtil.Mode.Only_Refresh);
        //设置主题色
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);
        //下拉到底最后不自动加载，需要再拉一下
        refreshLayout.setEnableAutoLoadmore(false);
        //不在加载更多完成之后滚动内容显示新数据
        refreshLayout.setEnableScrollContentWhenLoaded(false);
        addListener();
    }

    @Override
    public void initLoad() {
        refreshLayout.autoRefresh();
    }

    private void getData(){
        ComicService.get().getHTML(this, Global.REQUEST_HOME,
                ruleStore.getHomeRule().get("url"));
    }

    private void addListener(){
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                container.removeAllViews();
                sections.clear();
                getData();
            }
        });
    }

    private void addSectionView(String title,List<Comic> comics){
        View sectionView = LayoutInflater.from(context).inflate(R.layout.addview_section_grid,null);
        SectionHolder sectionHolder = new SectionHolder(sectionView);
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        linearLayout.setLayoutParams(lp);
        for(int i = 0 ; i < comics.size();i++){
            final Comic comic = comics.get(i);
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_comic_grid, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
            itemViewHolder.name.setText(comic.getName());
            itemViewHolder.updateStatus.setText(comic.getUpdateStatus());
            itemViewHolder.score.setVisibility(View.GONE);
            itemViewHolder.update.setVisibility(View.GONE);
            Glide.with(context).asBitmap().load(comic.getImageUrl()).into(itemViewHolder.image);
            itemViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("id",comic.getComicId());
                    startActivity(intent);
                }
            });
            LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemLp.width = (int) DisplayUtil.dpToPx(context,120f);
            itemView.setLayoutParams(itemLp);
            linearLayout.addView(itemView);
            //当加载一半后换行
            if(i + 1 == comics.size()/2 || i == comics.size()-1){
                sectionHolder.content.addView(linearLayout);
                linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(lp);
            }

        }
        sectionHolder.title.setText(title);

        container.addView(sectionView);

    }

    @Override
    public Object myDoInBackground(String TAG, Object data) {
        switch (TAG){
            case Global.REQUEST_HOME:
                CallBackData callBackData = ComicFetcher.getHome(data.toString());
                sections.addAll((List<Section>) callBackData.getObj());
                Log.d(TAG, "onFinish: 添加板块数"+sections.size());
                return sections.size();
        }
        return null;
    }

    @Override
    public void myOnPostExecute(String TAG, Object resultObj) {
        switch (TAG){
            case Global.REQUEST_HOME:
                if (resultObj!=null && (Integer)resultObj > 0) {
                    for(Section section : sections){
                        if(section.getComics() != null && section.getComics().size()>0)
                            addSectionView(section.getTitle(),section.getComics());
                    }
                    refreshLayout.finishRefresh();
                }
                break;
        }
    }

    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_HOME:
                UIUpdateTask uiUpdateTask = new UIUpdateTask(what,data);
                uiUpdateTask.execute();
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {
        refreshLayout.finishRefresh();
        SnackbarUtil.newAddImageColorfulSnackar(
                coordinatorLayout, getString(R.string.gird_tips_loading_next_page_fail),
                R.drawable.icon_error,
                ContextCompat.getColor(context,R.color.star_yellow)).show();
        Log.e(TAG,what + " Error: " + msg);
    }

    class SectionHolder{
        @BindView(R.id.section_title)
        TextView title;
        @BindView(R.id.section_content)
        LinearLayout content;
        View view;

        public SectionHolder(View view) {
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    class ItemViewHolder
    {
        @BindView(R.id.comic_score)
        TextView score;
        @BindView(R.id.comic_updateDate)
        TextView update;
        @BindView(R.id.comic_updateStatus)
        TextView updateStatus;
        @BindView(R.id.comic_name)
        TextView name;
        @BindView(R.id.comic_img)
        ImageView image;
        @BindView(R.id.comic_status)
        ImageView isEnd;
        View view;

        public ItemViewHolder(View view) {
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }
}
