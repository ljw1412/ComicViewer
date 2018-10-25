package ljw.comicviewer.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.RuleGuide;
import ljw.comicviewer.http.ComicService;
import ljw.comicviewer.ui.adapter.RulesRecyclerViewAdapter;

public class RuleManagerActivity extends BaseActivity
        implements ComicService.RequestCallback{
    private String TAG = this.getClass().getSimpleName()+"----";
    @BindView(R.id.nav_child_title)
    TextView title;
    @BindView(R.id.rule_recycerView)
    RecyclerView recyclerView;
    private RuleGuide ruleGuide;
    private RulesRecyclerViewAdapter rulesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_manager);
        ButterKnife.bind(this);
        initView();
        requestRuleList();
    }

    private void initView(){
        title.setText(R.string.mine_source);
    }

    private void setData(RuleGuide ruleGuide){
        rulesRecyclerViewAdapter = new RulesRecyclerViewAdapter(this,ruleGuide);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(rulesRecyclerViewAdapter);
        rulesRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void requestRuleList(){
        ComicService.get().getHTML(this, Global.REQUEST_RULE_LIST ,
                "https://raw.githubusercontent.com/ljw1412/ComicViewer/master/app/src/main/res/raw/rules.json");
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }

    private void updateUI(String data){

    }

    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_RULE_LIST:
                new UpdateUI(data.toString()).execute();
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {

    }

    class UpdateUI extends AsyncTask<Void,Void,Boolean>{
        private String json;

        public UpdateUI(String json) {
            this.json = json;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ruleGuide = JSON.parseObject(json, RuleGuide.class);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if(isSuccess) setData(ruleGuide);
        }
    }
}
