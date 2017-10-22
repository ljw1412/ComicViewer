package ljw.comicviewer.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.rule.RuleFetcher;
import ljw.comicviewer.rule.RuleParser;
import ljw.comicviewer.store.RuleStore;

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
        rule = readJson();
        debug.setText(rule);
        RuleParser.get().setRuleStr(rule);
        getListItems(1);


//        for(Map.Entry<String,String> entry : map.entrySet()){
//            Log.d(TAG, "onCreate: "+entry.getKey()+" "+entry.getValue());
//        }
    }

    private void initView(){
        title.setText(R.string.mine_setting);
    }

    private String readJson(){
        String content = "fail";
        InputStream in = getResources().openRawResource(R.raw.manhuagui);
        try {
            byte buffer[]=new byte[in.available()];
            in.read(buffer);
            content = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private void getListItems(int page){
        ComicService.get().getListItems(this,page);
    }



    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }

    @Override
    public void onFinish(Object data, String what) {
        switch (what) {
            case Global.REQUEST_COMICS_LIST:
                RuleFetcher.get().getComicList(data.toString());
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {

    }
}
