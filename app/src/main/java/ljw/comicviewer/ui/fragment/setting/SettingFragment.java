package ljw.comicviewer.ui.fragment.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import ljw.comicviewer.R;
import ljw.comicviewer.ui.SettingsActivity;
import ljw.comicviewer.util.PreferenceUtil;
import ljw.comicviewer.util.StoreUtil;

/**
 * Created by ljw on 2018-01-16 016.
 */

public class SettingFragment extends PreferenceFragment {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        getPreferenceManager().setSharedPreferencesName(PreferenceUtil.PreferenceName);
        addPreferencesFromResource(R.xml.preferences_setting);
        initView();
        addListener();
    }

    private void initView(){
        int currentSelected = getPreferenceManager().getSharedPreferences().getInt("sourceId",0);
        if(currentSelected>items.length) currentSelected = 0;
        Preference preference_source = findPreference("setting_source");
        preference_source.setSummary(
                String.format(getString(R.string.setting_sub_summary_source),items[currentSelected]));
    }

    private String[] items = {"漫画柜","漫画台","知音漫客"};
    private void addListener(){
        Preference preference_source = findPreference("setting_source");
        preference_source.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                int currentSelected = PreferenceUtil.getSharedPreferences(context).getInt("sourceId",0);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.setting_sub_title_source);
                builder.setSingleChoiceItems(items,currentSelected,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                StoreUtil.initRuleStore(context,R.raw.manhuagui);
                                break;
                            case 1:
                                StoreUtil.initRuleStore(context,R.raw.manhuatai);
                                break;
                            case 2:
                                StoreUtil.initRuleStore(context,R.raw.zymk);
                                break;
                        }
                        preference.setSummary(
                                String.format(getString(R.string.setting_sub_summary_source),items[i]));
                        PreferenceUtil.modify(context,"sourceId",i);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("取消", null);
                builder.show();
                return true;
            }
        });
        Preference preference_theme = findPreference("setting_theme");
        preference_theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "onPreferenceClick: setting_theme");
                ((SettingsActivity) context).changePref(new ThemeFragment(),preference.getTitle().toString());
                return true;
            }
        });
    }

}
