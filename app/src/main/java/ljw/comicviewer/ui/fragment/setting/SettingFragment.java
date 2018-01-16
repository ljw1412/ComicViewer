package ljw.comicviewer.ui.fragment.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.db.CollectionHolder;
import ljw.comicviewer.store.RuleStore;
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
        addPreferencesFromResource(R.xml.preferences_setting);
        addListener();
    }

    private void addListener(){
        Preference preference_source = findPreference("setting_source");
        preference_source.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int currentSelected = 0;
                switch (RuleStore.get().getComeFrom()){
                    case "manhuagui":
                        currentSelected = 0;
                        break;
                    case "manhuatai":
                        currentSelected = 1;
                        break;
                    case "zymk":
                        currentSelected = 2;
                        break;
                }
                Log.d(TAG, "onPreferenceClick: setting_source");
                String[] items = {"漫画柜","漫画台","知音漫客"};
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
                return true;
            }
        });
    }
}
