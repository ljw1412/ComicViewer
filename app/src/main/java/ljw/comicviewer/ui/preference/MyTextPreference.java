package ljw.comicviewer.ui.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ljw.comicviewer.R;

/**
 * Created by ljw on 2018-02-23 023.
 */

public class MyTextPreference extends Preference{
    private TextView textView;
    private CharSequence charSequence;
    public MyTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWidgetLayoutResource(R.layout.preference_text);
    }

    public MyTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWidgetLayoutResource(R.layout.preference_text);
    }

    public MyTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.preference_text);
    }

    public MyTextPreference(Context context) {
        super(context);
        setWidgetLayoutResource(R.layout.preference_text);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        textView = (TextView) view.findViewById(R.id.preference_text);
        if(charSequence!=null){
            textView.setText(charSequence);
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return super.onCreateView(parent);
    }

    public void setText(CharSequence ch){
        charSequence = ch;
        notifyChanged();
    }
}
