package ljw.comicviewer.ui.fragment.setting;


import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Theme;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.ui.SettingsActivity;
import ljw.comicviewer.ui.adapter.ThemeGridAdapter;
import ljw.comicviewer.util.PreferenceUtil;
import ljw.comicviewer.util.ThemeUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeFragment extends Fragment {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private ThemeGridAdapter themeGridAdapter;
    @BindView(R.id.theme_content)
    LinearLayout content;
    @BindView(R.id.theme_swich_navbar)
    SwitchCompat switch1;
    @BindView(R.id.theme_grid)
    GridView gridView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme,null);
        context = getActivity();
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    public void changeSwitch(boolean checked){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checked) {
                switch1.setThumbTintList(ColorStateList.valueOf(ThemeUtil.getThemeColor(context)));
                switch1.setTrackTintList(ColorStateList.valueOf(ThemeUtil.getThemeColor(context)));
            } else {
                switch1.setThumbTintList(ContextCompat.getColorStateList(context, R.color.black));
                switch1.setTrackTintList(ContextCompat.getColorStateList(context, R.color.black_pressed));
            }
        }
    }


    public void initView(){
        switch1.setChecked(
                PreferenceUtil.getSharedPreferences(context).getBoolean("nav_bar_auto",false));

        changeSwitch(switch1.isChecked());
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeSwitch(b);
                PreferenceUtil.modify(context,"nav_bar_auto",b);
                //即使切换主题色
                ((SettingsActivity) getActivity()).changeThemeColor(ThemeUtil.getThemeColor(context));
            }
        });

        themeGridAdapter = new ThemeGridAdapter(context,AppStatusStore.get().getThemes(context));
        gridView.setAdapter(themeGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PreferenceUtil.modify(context,"theme",position);
                changeSwitch(switch1.isChecked());
                //即使切换主题色
                ((SettingsActivity) getActivity()).changeThemeColor(
                        ((Theme)themeGridAdapter.getItem(position)).getColor());
            }
        });
    }



}
