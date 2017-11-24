package ljw.comicviewer.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;

public class AuthorComicsActivity extends Activity {
    private String TAG = this.getClass().getSimpleName()+"----";
    private Context context;
    @BindView(R.id.nav_child_title)
    TextView nav_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_comics);
        context = this;
        ButterKnife.bind(this);
    }

    private void initView(){

    }


    @Override
    public void setTitle(CharSequence title) {
        nav_title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        nav_title.setText(getString(titleId));
    }

}
