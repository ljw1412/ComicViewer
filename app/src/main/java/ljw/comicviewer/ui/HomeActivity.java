package ljw.comicviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.utils.ThemeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.ui.fragment.CollectionFragment;
import ljw.comicviewer.ui.fragment.HomeFragment;
import ljw.comicviewer.ui.fragment.MineFragment;
import ljw.comicviewer.util.PreferenceUtil;
import ljw.comicviewer.util.SnackbarUtil;
import ljw.comicviewer.util.StoreUtil;
import ljw.comicviewer.util.ThemeUtil;

/**
 * 主界面
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
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
    @BindView(R.id.home_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
//        getWindow().setNavigationBarColor(DisplayUtil.getAttrColor(context,R.attr.colorPrimary));
//        深色系
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {//android6.0以后可以对状态栏文字颜色和图标进行修改
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            getWindow().setStatusBarColor(DisplayUtil.getAttrColor(context,R.attr.colorPrimary));
//        }
        setContentView(R.layout.activity_home);
        //view绑定代码生成
        ButterKnife.bind(this);

        //fragment事务管理
        fragmentManager = getSupportFragmentManager();

        if(homeFragment ==null){
            homeFragment = new HomeFragment();
        }
        setCurrentFragment(homeFragment);

        //默认漫画标签
        img_comic.setSelected(true);

        //初始化ruleStore
        int currentSelected = PreferenceUtil.getSharedPreferences(context).getInt("sourceId",0);
        switch (currentSelected){
            case 0:
                StoreUtil.initRuleStore(context,R.raw.manhuagui);
                break;
            case 1:
                StoreUtil.initRuleStore(context,R.raw.manhuatai);
                break;
            case 2:
                StoreUtil.initRuleStore(context,R.raw.zymk);
                break;
        }
    }

    public void resetView(){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(mineFragment != null){
            ft.remove(mineFragment);
            mineFragment = null;
        }
        if(homeFragment != null){
            ft.remove(homeFragment);
            homeFragment = null;
        }
        if(collectionFragment != null){
            ft.remove(collectionFragment);
            collectionFragment = null;
        }
        ft.commit();
        if(mineFragment == null){
            mineFragment = new MineFragment();
        }
        currentFragment = null;
        setCurrentFragment(mineFragment);
    }

    public void changeTheme(){
        ThemeUtil.updateTheme(this,
                ThemeUtils.getColorById(this, R.color.theme_color_primary));
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
        if(currentFragment == fragment) return;
        if(fragment == homeFragment && AppStatusStore.get().isSourceReplace()){
            //如果换源新建Home页，并移除旧的
            homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragment = homeFragment;
            AppStatusStore.get().setSourceReplace(false);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            ft.add(R.id.content_home, fragment);
        }
        hideAllFragment(ft);
        ft.show(fragment).commit();
        currentFragment = fragment;
        if(currentFragment == collectionFragment && collectionFragment.isLoading()){
            collectionFragment.initLoad();
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

    //点击事件
    @OnClick({R.id.goto_comic,R.id.goto_collection,R.id.goto_mine})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goto_comic:
                setBtnSelected(img_comic);
                if(homeFragment ==null){
                    homeFragment = new HomeFragment();
                }
                setCurrentFragment(homeFragment);
                break;
            case R.id.goto_collection:
                setBtnSelected(img_collection);
                if(collectionFragment == null){
                    collectionFragment = new CollectionFragment();
                }
                setCurrentFragment(collectionFragment);
                break;
            case R.id.goto_mine:
                setBtnSelected(img_mine);
                if(mineFragment == null){
                    mineFragment = new MineFragment();
                }
                setCurrentFragment(mineFragment);
                break;
        }
    }

    //修改标题给该activity的fragment用的
    public void setTitle(TextView textView,CharSequence title) {
        textView.setText(title);
    }
    public void setTitle(TextView textView,int titleId) {
        textView.setText(getString(titleId));
    }

    //点击两次返回退出相关对象
    private Snackbar exitSnackBar;
    private boolean confirmed = false;//返回2次退出程序标志
    @Override
    public void onBackPressed() {
        if(currentFragment == collectionFragment && collectionFragment.isSearching()){
            collectionFragment.changeNormalMode();
            return;
        }

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
