package ljw.comicviewer.ui.fragment.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import ljw.comicviewer.R;
import ljw.comicviewer.ui.SettingsActivity;
import ljw.comicviewer.ui.dialog.ThemeDialog;
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

    private Preference preference_source;
    private Preference preference_theme;
    private Preference preference_preload;
    private Preference preference_readMode;
    private int currentSelected,preloadNum,readMode;
    private void initView(){
        preference_source = findPreference("setting_source");
        preference_theme = findPreference("setting_theme");
        preference_preload = findPreference("preloadPageNumber");
        preference_readMode = findPreference("readMode");

        currentSelected = getPreferenceManager()
                .getSharedPreferences().getInt("sourceId",0);
        if(currentSelected>items.length) currentSelected = 0;
        preference_source.setSummary(
                String.format(getString(R.string.setting_sub_summary_source),items[currentSelected]));
        preloadNum = getPreferenceManager()
                .getSharedPreferences().getInt("preloadPageNumber",2);
        preference_preload.setSummary(
                String.format(getString(R.string.setting_sub_summary_preload),preloadNum));
        readMode = getPreferenceManager()
                .getSharedPreferences().getInt("readMode",0);
        preference_readMode.setSummary(
                String.format(getString(R.string.setting_sub_summary_read_mode),modes[readMode]));
    }

    private String[] items = {"漫画柜","漫画台","知音漫客"};
    private String[] numbers = {"1","2","3","4"};
    private String[] modes = {"从左往右","从右往左","竖屏阅读"};
    private void addListener(){
        preference_source.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                ThemeDialog themeDialog = new ThemeDialog(context);
                themeDialog.setTitle(R.string.setting_sub_title_source)
                        .setSingleChoiceItems(items, currentSelected, new ThemeDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                StoreUtil.switchSource(context,i);
                                preference.setSummary(
                                        String.format(getString(R.string.setting_sub_summary_source),items[i]));
                                PreferenceUtil.modify(context,"sourceId",i);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("取消", new ThemeDialog.OnButtonClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                return true;
            }
        });
        preference_theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "onPreferenceClick: setting_theme");
                ((SettingsActivity) context).changePref(new ThemeFragment(),preference.getTitle().toString());
                return true;
            }
        });
        preference_preload.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                ThemeDialog themeDialog = new ThemeDialog(context);
                themeDialog.setTitle(R.string.setting_sub_title_preload)
                        .setMessage(R.string.setting_preload_tip)
                        .setSingleChoiceItems(numbers,preloadNum-1, new ThemeDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                preloadNum = Integer.valueOf(numbers[i]);
                                PreferenceUtil.modify(context,"preloadPageNumber",preloadNum);
                                preference.setSummary(
                                        String.format(getString(R.string.setting_sub_summary_preload),preloadNum));
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("取消", new ThemeDialog.OnButtonClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                return true;
            }
        });
        preference_readMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                ThemeDialog themeDialog = new ThemeDialog(context);
                themeDialog.setTitle(R.string.setting_sub_title_read_mode)
                        .setSingleChoiceItems(modes,readMode, new ThemeDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                readMode = i;
                                PreferenceUtil.modify(context,"readMode",readMode);
                                preference.setSummary(
                                        String.format(getString(R.string.setting_sub_summary_read_mode),modes[readMode]));
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("取消", new ThemeDialog.OnButtonClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                return true;
            }
        });
    }

}
