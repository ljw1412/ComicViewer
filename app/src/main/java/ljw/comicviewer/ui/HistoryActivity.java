package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.History;
import ljw.comicviewer.db.HistoryHolder;
import ljw.comicviewer.ui.adapter.HistoryRecyclerViewAdapter;

public class HistoryActivity extends AppCompatActivity {
    private String TAG = SettingsActivity.class.getSimpleName()+"----";
    private Context context;
    private HistoryRecyclerViewAdapter recyclerViewAdapter;
    private List<History> histories = new ArrayList<>();
    @BindView(R.id.nav_child_title)
    TextView title;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        context = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        title.setText(R.string.mine_history);
        initRecyclerView();
    }

    private void initRecyclerView(){
        getHistoriesByDB();
        recyclerViewAdapter = new HistoryRecyclerViewAdapter(context,histories);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recyclerViewAdapter);
    }



    private void getHistoriesByDB(){
        HistoryHolder historyHolder = new HistoryHolder(context);
        histories = historyHolder.getHistories(100);
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }
}
