package ljw.comicviewer.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.ui.fragment.setting.SettingFragment;
import ljw.comicviewer.util.ThemeUtil;

/**
 * 设置页
 */
public class SettingsActivity extends BaseActivity {
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

    //切换二级设置页
    public void changePref(Fragment fragment,String subTitle){
        title.setText(subTitle);
        fragmentManager.beginTransaction()
                .hide(settingFragment).add(R.id.setting_content,fragment).commit();
        currentFragment = fragment;
    }

    public void changeThemeColor(@ColorInt int color){
        //即使反馈，修改当前页面的主题
        ThemeUtil.setThemeByColor(context,color);
        setResult(Global.THEME_CHANGE,new Intent().putExtra("theme_change",true));
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