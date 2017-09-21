package ljw.comicviewer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ljw.comicviewer.R;
import ljw.comicviewer.ui.fragment.CollectionFragment;
import ljw.comicviewer.ui.fragment.ComicGridFragment;
import ljw.comicviewer.ui.fragment.MineFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = HomeActivity.class.getSimpleName()+"----";
    private static Context context;
    private Fragment currentFragment;
    private ComicGridFragment comicGridFragment;
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
    @BindView(R.id.title)
    TextView nav_title;
    @BindView(R.id.nav_btn_search)
    ImageView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_home);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //fragment事务管理
        fragmentManager = getSupportFragmentManager();

        if(comicGridFragment==null){
            comicGridFragment = new ComicGridFragment();
        }
        setCurrentFragment(comicGridFragment);

        //view绑定代码生成
        ButterKnife.bind(this);
        //默认漫画标签
        img_comic.setSelected(true);
    }


    //隐藏所有的fragment
    private void hideAllFragment(FragmentTransaction ft){
        if(mineFragment != null){
            ft.hide(mineFragment);
        }
        if(comicGridFragment != null){
            ft.hide(comicGridFragment);
        }
        if(collectionFragment != null){
            ft.hide(collectionFragment);
        }
    }

    //设置当前要显示的fragment
    private void setCurrentFragment(Fragment fragment){
        FragmentTransaction ft=fragmentManager.beginTransaction();
        if(!fragment.isAdded()){
            ft.add(R.id.content_home,fragment);//.commit();
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

    //点击事件
    @OnClick({R.id.goto_comic,R.id.goto_collection,R.id.goto_mine,R.id.nav_btn_search})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goto_comic:
                setBtnSelected(img_comic);
                setCurrentFragment(comicGridFragment);
                setTitle(R.string.app_name);
                btnSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.goto_collection:
                setBtnSelected(img_collection);
                if(collectionFragment == null){
                    collectionFragment = new CollectionFragment();
                }
                setCurrentFragment(collectionFragment);
                setTitle(R.string.txt_collection);
                btnSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.goto_mine:
                setBtnSelected(img_mine);
                if(mineFragment == null){
                    mineFragment = new MineFragment();
                }
                setCurrentFragment(mineFragment);
                setTitle(R.string.txt_mine);
                btnSearch.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent =new Intent(HomeActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
