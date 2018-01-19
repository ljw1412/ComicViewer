package ljw.comicviewer.ui.fragment.setting;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.ui.SettingsActivity;
import ljw.comicviewer.util.PreferenceUtil;
import ljw.comicviewer.util.ThemeUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeFragment extends Fragment {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    @BindView(R.id.theme_content)
    LinearLayout content;
    @BindView(R.id.theme_grid)
    GridView gridView;
    List<ThemeHolder> themeHolders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme,null);
        context = getActivity();
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int index = PreferenceUtil.getSharedPreferences(context).getInt("theme",0);
        themeHolders.get(index).color.setChecked(true);
    }

    public void initView(){
        for(int i = 0; i< AppStatusStore.get().getThemes(context).size(); i++){
            addSelector(i);
        }
    }

    public void addSelector(final int index){
        final String colorName = AppStatusStore.get().getThemes(context).get(index).getName();
        final int color = AppStatusStore.get().getThemes(context).get(index).getColor();

        final View view = LayoutInflater.from(context).inflate(R.layout.item_theme,null);
        final ThemeHolder themeHolder = new ThemeHolder(view);
        themeHolder.color.setEnabled(false);
        themeHolder.color.setBackgroundTintList(ColorStateList.valueOf(color));
        themeHolder.color.setButtonTintList(ColorStateList.valueOf(color));
        themeHolder.name.setText(colorName);
        themeHolder.name.setTextColor(color);
        themeHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean before = themeHolder.color.isChecked();
                if(before) return;
                clearCheck();
                themeHolder.color.setChecked(true);
                PreferenceUtil.modify(context,"theme",index);
                //即使切换主题色
                ((SettingsActivity) getActivity()).changeThemeColor(color);
            }
        });
        content.addView(view);
        themeHolders.add(themeHolder);
    }

    private void clearCheck(){
        for (ThemeHolder themeHolder : themeHolders){
            themeHolder.color.setChecked(false);
        }
    }


    class ThemeHolder{
        @BindView(R.id.theme_color)
        CheckBox color;
        @BindView(R.id.theme_color_name)
        TextView name;
        View view;

        public ThemeHolder(View view) {
            ButterKnife.bind(this,view);
            this.view = view;
        }
    }

    class ThemeGridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
}
