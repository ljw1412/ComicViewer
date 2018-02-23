package ljw.comicviewer.ui.fragment.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bumptech.glide.Glide;

import java.io.File;

import ljw.comicviewer.R;
import ljw.comicviewer.ui.SettingsActivity;
import ljw.comicviewer.ui.dialog.ThemeDialog;
import ljw.comicviewer.ui.preference.MyTextPreference;
import ljw.comicviewer.util.DisplayUtil;
import ljw.comicviewer.util.FileUtil;
import ljw.comicviewer.util.PreferenceUtil;
import ljw.comicviewer.util.SnackbarUtil;
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
        initData();
        initView();
        addListener();
        calculatingImagesSize();
    }

    private Preference preference_source;
    private Preference preference_theme;
    private Preference preference_preload;
    private Preference preference_readMode;
    private MyTextPreference preference_clearCache;
    private int currentSelected,preloadNum,readMode;
    private void initData(){
        //对象绑定
        preference_source = findPreference("setting_source");
        preference_theme = findPreference("setting_theme");
        preference_preload = findPreference("preloadPageNumber");
        preference_readMode = findPreference("readMode");
        preference_clearCache = (MyTextPreference) findPreference("clear_image_cache");
        //获取数组
        items = getResources().getStringArray(R.array.source_name);
        //获取首选项
        currentSelected = getPreferenceManager()
                .getSharedPreferences().getInt("sourceId",0);
        if(currentSelected>items.length) currentSelected = 0;
        preloadNum = getPreferenceManager()
                .getSharedPreferences().getInt("preloadPageNumber",2);
        readMode = getPreferenceManager()
                .getSharedPreferences().getInt("readMode",0);
    }

    private void initView(){
        preference_source.setSummary(
                String.format(getString(R.string.setting_sub_summary_source),items[currentSelected]));
        preference_preload.setSummary(
                String.format(getString(R.string.setting_sub_summary_preload),preloadNum));
        preference_readMode.setSummary(
                String.format(getString(R.string.setting_sub_summary_read_mode),modes[readMode]));
    }

    private String[] items /*= {"漫画柜","漫画台","知音漫客","比比猴"}*/;
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
        preference_clearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Glide.get(context).clearMemory();
                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        Glide.get(context).clearDiskCache();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if(!getActivity().isDestroyed()) {
                            SnackbarUtil.newAddImageColorfulSnackar(
                                    ((SettingsActivity) context).getCoordinatorLayout(),
                                    getString(R.string.tips_clear_success),
                                    R.drawable.icon_ok,
                                    ThemeUtils.getColorById(context, R.color.theme_color_primary)
                            ).show();
                            calculatingImagesSize();
                        }
                    }
                }.execute();
                return true;
            }
        });
    }

    private void calculatingImagesSize(){
        File appCacheDir = context.getExternalCacheDir();
        File glideCacheDir = new File(appCacheDir,"GlideCache");
        if (glideCacheDir.exists()){
            preference_clearCache.setText(getString(R.string.tips_calculating));
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    long size = FileUtil.getGlideCacheSize(context);
                    preference_clearCache.setText(DisplayUtil.bytesToHumanReadable(size));
                }
            });
        }else{
            preference_clearCache.setText("0.0 B");
        }
    }
}
