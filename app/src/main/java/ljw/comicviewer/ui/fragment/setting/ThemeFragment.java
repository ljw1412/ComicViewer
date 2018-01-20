package ljw.comicviewer.ui.fragment.setting;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.bilibili.magicasakura.utils.ThemeUtils;

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
    private List<Theme> themes;
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
        themes = AppStatusStore.get().getThemes(context);
        initView();
        return view;
    }

    public void initView(){
        switch1.setChecked(
                PreferenceUtil.getSharedPreferences(context).getBoolean("nav_bar_auto",false));

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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
                if(themes.get(position).isChecked()) return;
                PreferenceUtil.modify(context,"theme",position);
                if (((Theme)themeGridAdapter.getItem(position)).getName().contains("夜间")){
                    PreferenceUtil.modify(context,"night_mode",true);
                }else {
                    PreferenceUtil.modify(context,"night_mode",false);
                }
                updateSelected(position);
                //即使切换主题色
                ((SettingsActivity) getActivity()).changeThemeColor(
                        themes.get(position).getColor());
            }
        });
        ThemeUtils.updateNightMode(context.getResources(),true);
    }

    //刷新选择的主题
    public void updateSelected(int position){
        //修改选择状态
        for (Theme theme : themes){
            theme.setChecked(false);
        }
        themes.get(position).setChecked(true);
    }

}
