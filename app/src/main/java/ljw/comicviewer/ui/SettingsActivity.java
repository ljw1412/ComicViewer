package ljw.comicviewer.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.others.MyAppCompatActivity;
import ljw.comicviewer.ui.fragment.setting.SettingFragment;
import ljw.comicviewer.ui.fragment.setting.ThemeFragment;

public class SettingsActivity extends MyAppCompatActivity{
    private String TAG = SettingsActivity.class.getSimpleName()+"----";
    private Context context;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private SettingFragment settingFragment;
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
        initView();
    }

    private void initView(){
        title.setText(R.string.mine_setting);
        if(settingFragment==null){
            settingFragment = new SettingFragment();
        }
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.setting_content, settingFragment).commit();
        currentFragment = settingFragment;
    }

    public void changePref(Fragment fragment){
        fragmentManager.beginTransaction()
                .hide(settingFragment).add(R.id.setting_content,fragment).commit();
        currentFragment = fragment;
    }

    public void changePref(Fragment fragment,String subTitle){
        title.setText(subTitle);
        fragmentManager.beginTransaction()
                .hide(settingFragment).add(R.id.setting_content,fragment).commit();
        currentFragment = fragment;
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        if(currentFragment!=settingFragment){
            fragmentManager.beginTransaction()
                    .remove(currentFragment).show(settingFragment).commit();
            title.setText(getString(R.string.mine_setting));
            currentFragment = settingFragment;
        }else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBack(null);
    }
}