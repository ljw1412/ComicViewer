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

/**
 * Created by ljw on 2017-10-10 010.
 */

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager mFragmentManager;
    private List<Fragment> fragments;


    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void addFragment(Fragment fragment){
        fragments.add(fragment);
    }

    public void replaceFragment(int position,Fragment fragment){
        fragments.set(position,fragment);
    }


}
