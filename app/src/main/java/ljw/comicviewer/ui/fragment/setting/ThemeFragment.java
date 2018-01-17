package ljw.comicviewer.ui.fragment.setting;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.util.PreferenceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeFragment extends Fragment {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    @BindView(R.id.theme_content)
    LinearLayout content;
    String[] colorName = {"热情红","知乎蓝","哔哩粉"};
    Integer[] colorId ={R.color.accent_red,R.color.accent_blue,R.color.accent_pink};
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
        for(int i = 0 ; i< colorName.length;i++){
            addSelector(i);
        }
    }

    public void addSelector(final int index){
        final View view = LayoutInflater.from(context).inflate(R.layout.item_theme,null);
        final ThemeHolder themeHolder = new ThemeHolder(view);
        themeHolder.color.setEnabled(false);
        themeHolder.color.setBackgroundTintList(ContextCompat.getColorStateList(context,colorId[index]));
        themeHolder.color.setButtonTintList(ContextCompat.getColorStateList(context,colorId[index]));
        themeHolder.name.setText(colorName[index]);
        themeHolder.name.setTextColor(ContextCompat.getColor(context,colorId[index]));
        themeHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean before = themeHolder.color.isChecked();
                if(before) return;
                clearCheck();
                themeHolder.color.setChecked(true);
                PreferenceUtil.modify(context,"theme",index);
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
}
