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
public class HomeFragment extends Fragment {
    private String TAG = getClass().getSimpleName()+"----";
    HomeActivity context;

    @BindView(R.id.home_fragment_viewPager)
    ViewPager viewPager;
    MyFragmentPagerAdapter myFragmentPagerAdapter;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = (HomeActivity) getActivity();
        ButterKnife.bind(this,view);
        initViewPager();

        return view;
    }

    public void initViewPager(){
        FragmentManager fm = getChildFragmentManager();
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        ComicGridFragment comicGridFragment = new ComicGridFragment();
        myFragmentPagerAdapter.addFragment(comicGridFragment);

        CategoryFragment categoryFragment = new CategoryFragment();
        myFragmentPagerAdapter.addFragment(categoryFragment);

        myFragmentPagerAdapter.notifyDataSetChanged();

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                context.changeToolBarOption(position);
            }
        });
    }

}
