package ljw.comicviewer.others;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ljw.comicviewer.R;

/**
 * Created by ljw on 2018-01-10 010.
 */

public class MyAppCompatActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //修改主题色
        setTheme(R.style.AppTheme_NoActionBar_Blue);
        super.onCreate(savedInstanceState);
    }
}
