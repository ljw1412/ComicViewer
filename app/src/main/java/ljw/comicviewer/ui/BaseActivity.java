package ljw.comicviewer.ui;

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
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ThemeUtil.updateTheme(this,
                ThemeUtils.getColorById(this, R.color.theme_color_primary));
    }
}
