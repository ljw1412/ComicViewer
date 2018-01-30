package ljw.comicviewer.ui.preference;

import android.content.Context;
import android.os.Build;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import ljw.comicviewer.R;

/**
 * Created by ljw on 2018-01-30 030.
 */

public class MySwitchPreference extends SwitchPreference{
    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySwitchPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        if(Build.VERSION.SDK_INT >= 24) {
            setWidgetLayoutResource(R.layout.preference_widget_switch);
        }
        return super.onCreateView(parent);
    }
}
