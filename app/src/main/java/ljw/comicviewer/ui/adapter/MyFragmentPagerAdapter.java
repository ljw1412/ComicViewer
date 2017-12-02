package ljw.comicviewer.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.ui.fragment.BaseFragment;

/**
 * Created by ljw on 2017-10-10 010.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager mFragmentManager;
    private List<BaseFragment> fragments;
    private List<Boolean> loaded;
    private List<String> pageTitles = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        fragments = new ArrayList<>();
        loaded = new ArrayList<>();
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(BaseFragment fragment){
        fragments.add(fragment);
        loaded.add(false);
    }

    public void addFragment(BaseFragment fragment,String pageTitle){
        fragments.add(fragment);
        pageTitles.add(pageTitle);
        loaded.add(false);
    }

    public void loadFragment(int position,BaseFragment fragment){
        fragment.initLoad();
        loaded.set(position,true);
    }

    public void setLoaded(int position){
        loaded.set(position,true);
    }

    public boolean isLoaded(int position){
        return loaded.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }
}
