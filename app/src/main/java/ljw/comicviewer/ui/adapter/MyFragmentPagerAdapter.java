package ljw.comicviewer.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljw on 2017-10-10 010.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

    private List<Fragment> fragments;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
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

    public void addFragment(Fragment fragment){
        fragments.add(fragment);
    }
}
