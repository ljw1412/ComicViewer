package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.db.DBHelper;
import ljw.comicviewer.rule.RuleParser;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.fragment.CollectionFragment;
import ljw.comicviewer.ui.fragment.HomeFragment;
import ljw.comicviewer.ui.fragment.MineFragment;
import ljw.comicviewer.util.FileUtil;
import ljw.comicviewer.util.SnackbarUtil;

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
    @BindView(R.id.home_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


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
        if(RuleStore.get().getCurrentRule()!=null){
            RuleParser.get().parseAll();
        }else{
            throw new RuntimeException("初始化失败");
        }
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
        if(currentFragment instanceof CollectionFragment){
            if (((CollectionFragment) currentFragment).isLoading())
                ((CollectionFragment) currentFragment).initLoad();
        }
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

    //更新顶部导航栏
    private void changeTitleBar(){
        if(img_comic.isSelected()){
            nav_Tabs.setVisibility(View.VISIBLE);
            nav_title.setVisibility(View.GONE);
        }else if(img_collection.isSelected() || img_mine.isSelected()){
            nav_Tabs.setVisibility(View.GONE);
            nav_title.setVisibility(View.VISIBLE);
        }
    }

    //设置内容和顶部导航栏
    public void setContent(Fragment fragment){
        if(currentFragment == fragment) return;
        btnSearch.setVisibility(View.VISIBLE);
        if(fragment instanceof HomeFragment){
            setTitle("");
        }else if(fragment instanceof CollectionFragment){
            setTitle(R.string.txt_collection);
        }else if(fragment instanceof MineFragment){
            setTitle(R.string.txt_mine);
            btnSearch.setVisibility(View.GONE);
        }
        setCurrentFragment(fragment);
        changeTitleBar();
    }

    //标题栏界面更新相关
    private int[] tabIds = {R.id.option1,R.id.option2,R.id.option3};
    public void changeToolBarOption(int position){
        for(int i = 0 ; i < tabIds.length ; i++){
            TextView tabText = (TextView) findViewById(tabIds[i]);
            tabText.setTextColor(Color.WHITE);
        }
        ((TextView) findViewById(tabIds[position])).setTextColor(ContextCompat.getColor(context,R.color.green));
    }

    //设置标题栏点击事件
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

    //点击事件
    @OnClick({R.id.goto_comic,R.id.goto_collection,R.id.goto_mine,R.id.nav_btn_search})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goto_comic:
                setBtnSelected(img_comic);
                setContent(homeFragment);
                break;
            case R.id.goto_collection:
                setBtnSelected(img_collection);
                if(collectionFragment == null){
                    collectionFragment = new CollectionFragment();
                }
                setContent(collectionFragment);
                break;
            case R.id.goto_mine:
                setBtnSelected(img_mine);
                if(mineFragment == null){
                    mineFragment = new MineFragment();
                }
                setContent(mineFragment);
                break;
            case R.id.nav_btn_search:
                Intent intent = new Intent(context,SearchActivity.class);
                startActivity(intent);
                break;
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

    //点击两次返回退出相关对象
    private Snackbar exitSnackBar;
    private boolean confirmed = false;//返回2次退出程序标志
    @Override
    public void onBackPressed() {
        exitSnackBar = Snackbar.make(coordinatorLayout,getString(R.string.alert_confirm_exit), Global.SNACKBAR_DURATION)
            .addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (event == DISMISS_EVENT_SWIPE ||
                            event == DISMISS_EVENT_TIMEOUT ||
                            event == DISMISS_EVENT_CONSECUTIVE) {
                            confirmed = false;
                    }
                }
            });
        SnackbarUtil.snackbarAddView(exitSnackBar,R.drawable.icon_loudly_crying_face);
        if (confirmed) {
            // 返回桌面
            super.onBackPressed();
        } else {
            exitSnackBar.show();
            confirmed = true;
        }

    }
}
