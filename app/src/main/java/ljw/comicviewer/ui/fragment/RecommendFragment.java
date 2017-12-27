package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.bean.Section;
import ljw.comicviewer.db.SectionDBHolder;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.others.GlideImageLoader;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.DetailsActivity;
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
    @BindView(R.id.banner)
    Banner banner;

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
        initBanner();
        addListener();
    }

    @Override
    public void initLoad() {
        getDataFromDB();
        refreshLayout.autoRefresh();
    }

    public void reload(){
        //清除所有板块
        container.removeAllViews();
        sections.clear();
        getDataFromDB();
        refreshLayout.autoRefresh();
    }

    private void initBanner(){
        //暂时为本地图片加载
        List<Integer> images =new ArrayList<>(Arrays.asList(
                R.drawable.banner_01,R.drawable.banner_02,R.drawable.banner_03));
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用,点击事件请放到start()前
        banner.start();
    }
    //从数据库取出上次的请求数据，用于开始显示
    private void getDataFromDB(){
        SectionDBHolder sectionHolder = new SectionDBHolder(context);
        List<Section> dbSections = sectionHolder.getSectionsByHost(ruleStore.getComeFrom());
        if (dbSections!=null && dbSections.size()>0) {
            sections.addAll(dbSections);
            addSectionViews();
        }
    }
    //网络请求数据
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
                getData();
            }
        });
    }

    private void addSectionViews(){
        //根据屏幕宽度设置列数
        int columns = DisplayUtil.getGridNumColumns(context,115);
        for(Section section : sections){
            if(section.getComics() != null && section.getComics().size()>0) {
                addSectionView(section.getTitle(), section.getComics(),columns,115f);
            }
        }
    }

    private void addSectionView(String title,List<Comic> comics,int columns,float imageWidth){
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
            //图片网络
            RequestOptions options = new RequestOptions();
            options.skipMemoryCache(true).centerCrop().override(300,400);
            Glide.with(context)
                    .asBitmap()
                    .apply(options)
                    .load(comic.getImageUrl())
                    .into(itemViewHolder.image);

            itemViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("id",comic.getComicId());
                    startActivity(intent);
                }
            });
            LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemLp.width = (int) DisplayUtil.dpToPx(context,imageWidth);
            itemView.setLayoutParams(itemLp);
            //将itemView嵌套到一个新的linearLayout 为了让每个漫画界面可以居中 从而实现有间隔
            LinearLayout linearLayoutA = new LinearLayout(context);
            LinearLayout.LayoutParams lpA = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpA.weight = 1;
            linearLayoutA.setGravity(Gravity.CENTER);
            linearLayoutA.setLayoutParams(lpA);
            //itemView添加到linearLayoutA，之后linearLayoutA添加到linearLayout
            linearLayoutA.addView(itemView);
            linearLayout.addView(linearLayoutA);

            //如果达到一行要求的数量或是最后一个元素则添加view并新建新一行的view
            if((i+1)%columns==0 || i==comics.size()-1){
                sectionHolder.content.addView(linearLayout);
                linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(lp);
            }
            //如果有columns-columns*2部显示1行 columns*2部以上只显示2行
            if (comics.size()>=columns*2 && i+1 == columns*2){
                break;
            }else if(comics.size()>columns && comics.size()<columns*2 && i + 1==columns){
                break;
            }



        }
        sectionHolder.title.setText(title);
        container.addView(sectionView);
    }

    @Override
    public Object myDoInBackground(String what, Object data) {
        switch (what){
            case Global.REQUEST_HOME:
                CallBackData callBackData = ComicFetcher.getHome(data.toString());
                List<Section> tempList = (List<Section>) callBackData.getObj();
                if (tempList!=null && tempList.size()>0) {
                    sections.clear();
                    sections.addAll(tempList);
                    Log.d(TAG, "onFinish: 添加板块数"+sections.size());
                    return sections.size();
                }
                return 0;
        }
        return null;
    }

    @Override
    public void myOnPostExecute(String what, Object resultObj) {
        switch (what){
            case Global.REQUEST_HOME:
                if (resultObj!=null && (Integer)resultObj > 0) {
                    //请求成功删除旧的界面
                    container.removeAllViews();
                    //并将此次请求保存入数据库
                    if(sections != null && sections.size()>0) {
                        SectionDBHolder sectionHolder = new SectionDBHolder(context);
                        sectionHolder.addOrUpdateSection(ruleStore.getComeFrom(), sections);
                    }
                    addSectionViews();
                    refreshLayout.finishRefresh();
                }else{
                    onError("网络异常，未添加板块",what);
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
