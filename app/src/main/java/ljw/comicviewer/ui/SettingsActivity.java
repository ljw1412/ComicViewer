package ljw.comicviewer.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.app.FragmentManager;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.others.MyAppCompatActivity;
import ljw.comicviewer.ui.fragment.setting.SettingFragment;
import ljw.comicviewer.ui.fragment.setting.ThemeFragment;
import ljw.comicviewer.util.StoreUtil;

public class SettingsActivity extends MyAppCompatActivity{
    private String TAG = SettingsActivity.class.getSimpleName()+"----";
    private Context context;
    private FragmentManager fragmentManager;
    private SettingFragment settingFragment;
    private ThemeFragment themeFragment;
    @BindView(R.id.nav_child_title)
    TextView title;
    @BindView(R.id.setting_content)
    RelativeLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        ButterKnife.bind(this);
        fragmentManager = getFragmentManager();
        initView();
    }

    private void initView(){
        title.setText(R.string.mine_setting);
        if(settingFragment==null){
            settingFragment = new SettingFragment();
        }
        fragmentManager.beginTransaction()
                .add(R.id.setting_content, settingFragment).commit();
    }


    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }


}