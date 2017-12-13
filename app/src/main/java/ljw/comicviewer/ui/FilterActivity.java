package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Category;
import ljw.comicviewer.store.FilterStore;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.adapter.FilterAdapter;

public class FilterActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    private FilterAdapter filterAdapter;
    FilterStore filterStore = FilterStore.get();
    RuleStore ruleStore = RuleStore.get();
    List<Category> categories = new ArrayList<>();
    List<TextView> textViews = new ArrayList<>();
    @BindView(R.id.nav_child_title)
    TextView title;
    @BindView(R.id.filter_type_box)
    LinearLayout typeBox;
    @BindView(R.id.filter_layout_types)
    RelativeLayout view_types;//下面gridView的父级
    @BindView(R.id.filter_grid_view)
    GridView gridView;
    @BindView(R.id.filter_type_shadow)
    RelativeLayout view_shadow;//gridView下面的阴影

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        context = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        title.setText(R.string.title_filter);
        initGridView();
        addTypeBtn();
        addListener();
    }

    private void initGridView(){
        filterAdapter = new FilterAdapter(context,categories);
        gridView.setAdapter(filterAdapter);
    }

    private void addListener(){
        view_shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTextStatus();
                view_types.setVisibility(View.GONE);
            }
        });
    }

    //添加父类型按钮 在标题栏下方
    private void addTypeBtn(){
        List<String> order = filterStore.getOrder();
        if(order!=null && order.size()>0){
            for (final String typeName: order) {
                TextView addView = new TextView(context);
                addView.setText(typeName);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                lp.gravity = Gravity.CENTER;
                addView.setLayoutParams(lp);
                addView.setTextSize(20);
                addView.setTextColor(ContextCompat.getColor(context,R.color.black_60));
                addView.setGravity(Gravity.CENTER);
                addView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getTag()==null || !(boolean)view.getTag()){
                            //重置状态
                            resetTextStatus();
                            //设置选中状态为true
                            view.setTag(true);
                            //设置选中父类型的颜色
                            ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.smmcl_green));
                            //清空并重新加载数据
                            categories.clear();
                            categories.addAll(ruleStore.getTypeRule().get(typeName));
                            String aa= "";
                            for(Category category:categories){
                                aa+=category.getName()+" ";
                            }
                            Log.d(TAG, "onClick: "+aa);
                            filterAdapter.notifyDataSetChanged();
                            view_types.setVisibility(View.VISIBLE);
                        }else{
                            //如果是当前选中的父类型再点击将隐藏子类型网格，并重置状态
                            resetTextStatus();
                            view_types.setVisibility(View.GONE);
                        }
                    }
                });
                textViews.add(addView);
                typeBox.addView(addView);
            }
        }
    }

    //重置文字状态
    private void resetTextStatus(){
        for (TextView textView : textViews){
            //恢复选中前的颜色
            textView.setTextColor(ContextCompat.getColor(context,R.color.black_60));
            //选中状态为false
            textView.setTag(false);
        }
    }

    //按标题栏返回按钮
    public void onBack(View view) {
        finish();
    }
}
