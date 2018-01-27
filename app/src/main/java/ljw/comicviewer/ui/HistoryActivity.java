package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.History;
import ljw.comicviewer.db.HistoryHolder;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.adapter.HistoryRecyclerViewAdapter;
import ljw.comicviewer.ui.listeners.OnItemClickListener;
import ljw.comicviewer.util.DisplayUtil;

/**
 * 历史记录界面
 */
public class HistoryActivity extends BaseActivity {
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
        recyclerView.addItemDecoration(new DividerItemDecoration(context,1));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                History history = histories.get(position);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",history.getComicId());
                context.startActivity(intent);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            private float viewWidth= DisplayUtil.getScreenWidthPX(context);
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.LEFT);
            }
            //上下拖动回调次方法。
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            //当item视图变化时调用

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View view = viewHolder.itemView;
                float width = view.getWidth();
                float height = view.getHeight();
                float top = view.getTop();
                float right = view.getWidth();
                float bottom = view.getBottom();
                float alpha = Math.abs(dX)/width;
                //画背景

                Paint p = new Paint();
                p.setColor(ContextCompat.getColor(context,R.color.theme_red));
                p.setAlpha(alpha>=0.5?255: (int) (255 * alpha * 2));
                RectF rectf = new RectF(width-Math.abs(dX),top,right,bottom);
                c.drawRect(rectf,p);
                //写文字
                Paint textPaint = new Paint();
                textPaint.setColor(ContextCompat.getColor(context,R.color.white));
                textPaint.setTextSize(60);
                textPaint.setTextAlign(Paint.Align.CENTER);
                Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
                float x= rectf.centerX();
                float y = (rectf.bottom + rectf.top - fontMetrics.bottom - fontMetrics.top) / 2;
                c.drawText(getString(Math.abs(dX)<width/2?R.string.history_delete_hint:R.string.history_delete_up),
                        x,y,textPaint);
                viewHolder.itemView.setAlpha(1-alpha);
            }

            //左右滑动回调此方法。
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                recyclerViewAdapter.remove(viewHolder.getAdapterPosition());
            }
            /**
             * 获取删除方块的宽度
             */
            public int getSlideLimitation(RecyclerView.ViewHolder viewHolder){
                ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
                Log.d(TAG, "getSlideLimitation: "+viewGroup.getWidth());
                return viewGroup.getWidth();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void getHistoriesByDB(){
        HistoryHolder historyHolder = new HistoryHolder(context);
        histories = historyHolder.getHistories(100, RuleStore.get().getComeFrom());
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }

}
