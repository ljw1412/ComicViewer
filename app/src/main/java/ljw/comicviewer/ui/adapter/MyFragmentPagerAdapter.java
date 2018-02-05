package ljw.comicviewer.ui.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.ui.fragment.BaseFragment;

/**
 * 主页分页适配器
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

    public void removeFragment(BaseFragment fragment){
        int index = fragments.indexOf(fragment);
        if(index!=-1){
            fragments.remove(index);
            pageTitles.remove(index);
            loaded.remove(index);
        }
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
