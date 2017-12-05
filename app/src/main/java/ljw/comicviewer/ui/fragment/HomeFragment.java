package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    CategoryFragment categoryFragment;
    BaseFragment currentFragment;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    @BindView(R.id.home_fragment_viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
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

        newAddFragment = new NewAddFragment();
        updateFragment = new UpdateFragment();
        categoryFragment = new CategoryFragment();

        myFragmentPagerAdapter.addFragment(newAddFragment,getString(R.string.opt_new));
        myFragmentPagerAdapter.addFragment(updateFragment,getString(R.string.opt_update));
        myFragmentPagerAdapter.addFragment(categoryFragment,getString(R.string.opt_category));

        currentFragment = myFragmentPagerAdapter.getItem(0);
        //第一个加载必须在那个fragment中执行，这里仅修改数组加载状态
        myFragmentPagerAdapter.setLoaded(0);

        myFragmentPagerAdapter.notifyDataSetChanged();
    }

    public void addListener(){
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
                loadFragment(position);
            }
        });
    }

    private void loadFragment(int position){
        if (!myFragmentPagerAdapter.isLoaded(position)){
            myFragmentPagerAdapter.loadFragment(position,currentFragment);
        }
    }

}
