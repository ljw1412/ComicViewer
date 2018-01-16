package ljw.comicviewer.ui.fragment.setting;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import ljw.comicviewer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeFragment extends PreferenceFragment {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;

    public ThemeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_setting);
    }



}
