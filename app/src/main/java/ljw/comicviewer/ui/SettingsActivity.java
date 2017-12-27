package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.util.StoreUtil;

public class SettingsActivity extends AppCompatActivity
        implements ComicService.RequestCallback {
    private String TAG = SettingsActivity.class.getSimpleName()+"----";
    private Context context;
    @BindView(R.id.nav_child_title)
    TextView title;
    @BindView(R.id.debug)
    TextView debug;
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
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        Button button1 = new Button(context);
        button1.setText("漫画柜");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreUtil.initRuleStore(context,R.raw.manhuagui);
            }
        });
        Button button2 = new Button(context);
        button2.setText("漫画台");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreUtil.initRuleStore(context,R.raw.manhuatai);
            }
        });
        LinearLayout.LayoutParams params = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        button1.setLayoutParams(params);
        button2.setLayoutParams(params);
        layout.addView(button1);
        layout.addView(button2);
        content.addView(layout);
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