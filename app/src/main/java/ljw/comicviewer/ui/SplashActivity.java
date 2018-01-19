package ljw.comicviewer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import ljw.comicviewer.R;
import ljw.comicviewer.util.PreferenceUtil;

public class SplashActivity extends AppCompatActivity {
    private int time = 2000;
    @BindView(R.id.splash_slogan)
    TextView slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(PreferenceUtil.getSharedPreferences(this).getBoolean("noSplash",false)){
            toHome();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toHome();
            }
        },time);
    }


    private void toHome(){
        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
