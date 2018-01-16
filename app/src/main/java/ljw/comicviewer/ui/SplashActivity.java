package ljw.comicviewer.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;

public class SplashActivity extends AppCompatActivity {
    private int time = 2000;
    @BindView(R.id.splash_slogan)
    TextView slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        if(true){
//            toHome();
//            return;
//        }


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
