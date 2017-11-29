package ljw.comicviewer.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.bean.History;
import ljw.comicviewer.db.HistoryHolder;
import ljw.comicviewer.http.ComicFetcher;
import ljw.comicviewer.http.ComicService;

public class SettingsActivity extends AppCompatActivity
        implements ComicService.RequestCallback {
    private String TAG = SettingsActivity.class.getSimpleName()+"----";
    private static Context context;
    private String rule;
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

        HistoryHolder historyHolder = new HistoryHolder(context);
        List<History> chapters = historyHolder.getHistories();
        debug.setText(chapters.size()+"\n");
        for (History history : chapters){
            debug.append(history.toString());
        }
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
