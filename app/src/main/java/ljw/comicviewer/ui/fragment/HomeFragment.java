package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.FilterActivity;
import ljw.comicviewer.ui.SearchActivity;
import ljw.comicviewer.ui.adapter.MyFragmentPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment{
    private String TAG = getClass().getSimpleName()+"----";
    Context context;
    NewAddFragment newAddFragment;
    UpdateFragment updateFragment;
    RecommendFragment recommendFragment;
    BaseFragment currentFragment;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    RuleStore ruleStore = RuleStore.get();
    @BindView(R.id.home_fragment_viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.nav_btn_filter)
    ImageView btn_filter;
    @BindView(R.id.nav_btn_search)
    ImageView btn_search;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        ButterKnife.bind(this,view);
        initLoad();
        initView();
        addListener();
        return view;
    }

    @Override
    public void initLoad() {
        //用于更新检查和公告发布
    }

    @Override
    public void initView() {
        if(RuleStore.get().getSearchRule()==null){
            btn_search.setVisibility(View.GONE);
        }
        initViewPager();
        //tab标题栏绑定viewpager
        tabLayout.setupWithViewPager(viewPager);
    }


    //采用了当界面处于当前页时加载并缓存页面
    public void initViewPager(){
        FragmentManager fm = getChildFragmentManager();
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        if(ruleStore.getHomeRule()!=null) {
            recommendFragment = new RecommendFragment();
            myFragmentPagerAdapter.addFragment(recommendFragment, getString(R.string.opt_recommend));
        }
        if(ruleStore.getListRule()!=null){
            String hasAddNew = null;
            if(ruleStore.getConfigRule()!=null) {
                hasAddNew = ruleStore.getConfigRule().get("hasAddNew");
            }
            if(hasAddNew==null || hasAddNew.equals("true")) {
                newAddFragment = new NewAddFragment();
                myFragmentPagerAdapter.addFragment(newAddFragment, getString(R.string.opt_new));
            }
        }
        if(ruleStore.getLatestRule()!=null) {
            updateFragment = new UpdateFragment();
            myFragmentPagerAdapter.addFragment(updateFragment, getString(R.string.opt_update));
        }


        currentFragment = myFragmentPagerAdapter.getItem(0);
        //第一个加载必须在那个fragment中执行，这里仅修改数组加载状态
        myFragmentPagerAdapter.setLoaded(0);

        myFragmentPagerAdapter.notifyDataSetChanged();
    }

    public void addListener(){
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilterActivity.class);
                startActivity(intent);
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,SearchActivity.class);
                startActivity(intent);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                currentFragment = myFragmentPagerAdapter.getItem(position);
                if (!myFragmentPagerAdapter.isLoaded(position)){
                    myFragmentPagerAdapter.loadFragment(position,currentFragment);
                }
            }
        });
    }
}
