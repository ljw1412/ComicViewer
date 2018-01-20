package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;

/**
 * 关于界面
 */
public class AboutActivity extends BaseActivity {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    @BindView(R.id.nav_child_title)
    TextView title;
    @BindView(R.id.app_version)
    TextView txt_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        context = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        title.setText(R.string.mine_about);
        try {
            txt_version.setText(
                "V"+getPackageManager().getPackageInfo(getPackageName(),0).versionName
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }
}
