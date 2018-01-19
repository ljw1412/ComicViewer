package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener(){
            private float x,y,dx,lastX,lastY;
            private View view;
            private int scrollX;
            private Scroller mScroller = new Scroller(context, new LinearInterpolator());
            private int maxLength=400;
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获得触摸的view
                        view = rv.findChildViewUnder(x,y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dx = lastX - x;
                        scrollX = view.getScrollX();
                        if (scrollX > 0 && scrollX <= maxLength || (scrollX==0 && dx > 0) ) {//向左滑动
                            view.scrollBy((int) dx, 0);
                        }
                        if(scrollX<0){
                            view.scrollTo(0,0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(scrollX > maxLength/2 && scrollX != maxLength) {
                            view.scrollTo(maxLength, 0);
                            mScroller.startScroll(scrollX,0,maxLength - scrollX,0,200);
                        } else if(scrollX == 0){
                            return false;
                        }else if(scrollX > 0 && x < view.getWidth() - scrollX){
                            view.scrollTo(0,0);
                            mScroller.startScroll(scrollX,0,-scrollX,0,200);
                        }
                        return true;
                }
                lastX = x;
                lastY = y;
                return super.onInterceptTouchEvent(rv,event);
            }
        });
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
//            @Override
//            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//                return makeMovementFlags(0,ItemTouchHelper.START);
//            }
//            //上下拖动回调次方法。
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//            //左右滑动回调此方法。
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                Log.d(TAG, "onSwiped: "+direction);
//            }
//        });
//        itemTouchHelper.attachToRecyclerView(recyclerView);
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
