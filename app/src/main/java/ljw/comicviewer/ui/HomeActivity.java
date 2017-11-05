package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ljw.comicviewer.R;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.fragment.CollectionFragment;
import ljw.comicviewer.ui.fragment.HomeFragment;
import ljw.comicviewer.ui.fragment.MineFragment;
import ljw.comicviewer.util.FileUtil;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = HomeActivity.class.getSimpleName()+"----";
    private static Context context;
    private Fragment currentFragment;
    private HomeFragment homeFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    private CollectionFragment collectionFragment;

    @BindView(R.id.goto_comic)
    LinearLayout myBtn_comic;
    @BindView(R.id.goto_collection)
    LinearLayout myBtn_collection;
    @BindView(R.id.goto_mine)
    LinearLayout myBtn_mine;
    @BindView(R.id.img_comic)
    ImageView img_comic;
    @BindView(R.id.img_collection)
    ImageView img_collection;
    @BindView(R.id.img_mine)
    ImageView img_mine;

    @BindView(R.id.nav_bar_default)
    View nav_def;
    @BindView(R.id.title)
    TextView nav_title;
    @BindView(R.id.nav_btn_search)
    ImageView btnSearch;
    @BindView(R.id.nav_bar_tabs)
    LinearLayout nav_Tabs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_home);

        //fragment事务管理
        fragmentManager = getSupportFragmentManager();

        if(homeFragment ==null){
            homeFragment = new HomeFragment();
        }
        setCurrentFragment(homeFragment);

        //view绑定代码生成
        ButterKnife.bind(this);
        //默认漫画标签
        img_comic.setSelected(true);

        initRuleStore();
        changeTitleBar();
        setToolBarClick();
        changeToolBarOption(0);
    }

    private void initRuleStore(){
        String rule = FileUtil.readJson(context);
        RuleStore.get().setCurrentRule(rule.equals("fail") ? null : rule);
    }

    //隐藏所有的fragment
    private void hideAllFragment(FragmentTransaction ft){
        if(mineFragment != null){
            ft.hide(mineFragment);
        }
        if(homeFragment != null){
            ft.hide(homeFragment);
        }
        if(collectionFragment != null){
            ft.hide(collectionFragment);
        }
    }

    //设置当前要显示的fragment
    private void setCurrentFragment(Fragment fragment){
        FragmentTransaction ft=fragmentManager.beginTransaction();
        if(!fragment.isAdded()){
            ft.add(R.id.content_home,fragment);
        }
        hideAllFragment(ft);
        ft.show(fragment).commit();
        currentFragment = fragment;
    }

    //让所有底部导航栏为未选中状态
    private void setAllBtnNoSelected(){
        img_comic.setSelected(false);
        img_collection.setSelected(false);
        img_mine.setSelected(false);
    }

    //让点击的导航栏为选中状态
    private void setBtnSelected(View view){
        if (!view.isSelected()){
            setAllBtnNoSelected();
            view.setSelected(true);
        }
    }

    private void changeTitleBar(){
        if(img_comic.isSelected()){
            nav_Tabs.setVisibility(View.VISIBLE);
            nav_title.setVisibility(View.GONE);
        }else if(img_collection.isSelected() || img_mine.isSelected()){
            nav_Tabs.setVisibility(View.GONE);
            nav_title.setVisibility(View.VISIBLE);
        }
    }

    //点击事件
    @OnClick({R.id.goto_comic,R.id.goto_collection,R.id.goto_mine,R.id.nav_btn_search})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goto_comic:
                setBtnSelected(img_comic);
                setCurrentFragment(homeFragment);
                setTitle(R.string.app_name);
                btnSearch.setVisibility(View.VISIBLE);
                changeTitleBar();
                break;
            case R.id.goto_collection:
                setBtnSelected(img_collection);
                if(collectionFragment == null){
                    collectionFragment = new CollectionFragment();
                }
                setCurrentFragment(collectionFragment);
                setTitle(R.string.txt_collection);
                btnSearch.setVisibility(View.VISIBLE);
                changeTitleBar();
                break;
            case R.id.goto_mine:
                setBtnSelected(img_mine);
                if(mineFragment == null){
                    mineFragment = new MineFragment();
                }
                setCurrentFragment(mineFragment);
                setTitle(R.string.txt_mine);
                btnSearch.setVisibility(View.GONE);
                changeTitleBar();
                break;
            case R.id.nav_btn_search:
                Intent intent = new Intent(context,SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private int[] tabIds = {R.id.option1,R.id.option2,R.id.option3};
    public void changeToolBarOption(int position){
        for(int i = 0 ; i < tabIds.length ; i++){
            TextView tabText = (TextView) findViewById(tabIds[i]);
            tabText.setTextColor(Color.rgb(255,255,255));
        }
        ((TextView) findViewById(tabIds[position])).setTextColor(Color.rgb(108,226,108));
    }

    public void setToolBarClick(){
        for(int i = 0 ; i < tabIds.length ; i++){
            TextView tabText = (TextView) findViewById(tabIds[i]);
            final int finalI = i;
            tabText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeToolBarOption(finalI);
                    homeFragment.setPagePosition(finalI);
                }
            });
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        nav_title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        nav_title.setText(getString(titleId));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
