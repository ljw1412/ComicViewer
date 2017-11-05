package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.ui.HomeActivity;
import ljw.comicviewer.ui.adapter.MyFragmentPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment{
    private String TAG = getClass().getSimpleName()+"----";
    HomeActivity context;

    @BindView(R.id.home_fragment_viewPager)
    ViewPager viewPager;
    ComicGridFragment comicGridFragment;
    UpdateFragment updateFragment;
    CategoryFragment categoryFragment;
    BaseFragment currentFragment;
    MyFragmentPagerAdapter myFragmentPagerAdapter;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = (HomeActivity) getActivity();
        ButterKnife.bind(this,view);
        initView();

        return view;
    }

    @Override
    public void initLoad() {
        //用于更新检查和公告发布
    }

    @Override
    public void initView() {
        initViewPager();
    }

    //采用了当界面处于当前页时加载并缓存页面
    public void initViewPager(){
        FragmentManager fm = getChildFragmentManager();
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        comicGridFragment = new ComicGridFragment();
        updateFragment = new UpdateFragment();
        categoryFragment = new CategoryFragment();

        myFragmentPagerAdapter.addFragment(comicGridFragment);
        myFragmentPagerAdapter.addFragment(updateFragment);
        myFragmentPagerAdapter.addFragment(categoryFragment);

        currentFragment = myFragmentPagerAdapter.getItem(0);
        loadFragment(0);

        myFragmentPagerAdapter.notifyDataSetChanged();

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                context.changeToolBarOption(position);
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

    public void setPagePosition(int position){
        viewPager.setCurrentItem(position);
    }

}
