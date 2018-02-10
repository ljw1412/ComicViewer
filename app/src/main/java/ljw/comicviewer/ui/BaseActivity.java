package ljw.comicviewer.ui;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bilibili.magicasakura.utils.ThemeUtils;

import ljw.comicviewer.R;
import ljw.comicviewer.util.ThemeUtil;

/**
 * Created by ljw on 2018-01-19 019.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主题色
        TypedArray styles = getResources().obtainTypedArray(R.array.theme_style);
        int index = ThemeUtil.getTheme(this);
        setTheme(styles.getResourceId(index,0));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ThemeUtil.updateTheme(this,
                ThemeUtils.getColorById(this, R.color.theme_color_primary));
    }
}
