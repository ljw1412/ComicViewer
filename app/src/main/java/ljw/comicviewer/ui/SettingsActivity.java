package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.http.ComicService;

public class SettingsActivity extends AppCompatActivity
        implements ComicService.RequestCallback {
    private String TAG = SettingsActivity.class.getSimpleName()+"----";
    private Context context;

    @BindView(R.id.nav_child_title)
    TextView title;
    @BindView(R.id.debug)
    TextView debug;

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
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }

    @Override
    public void onFinish(Object data, String what) {

    }

    @Override
    public void onError(String msg, String what) {

    }
}