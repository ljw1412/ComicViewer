package ljw.comicviewer.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.http.ComicService;

public class SearchActivity extends AppCompatActivity implements ComicService.RequestCallback{
    private String TAG = this.getClass().getSimpleName()+"----";
    Context context;
    boolean Searching = false;

    @BindView(R.id.search_button)
    Button btn_search;
    @BindView(R.id.search_edit)
    EditText edit_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        ButterKnife.bind(this);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = edit_search.getText().toString();
                if (!keyword.trim().equals("")){
                    Toast.makeText(context,keyword,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context,"关键字不能为空白！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void search(String keyword){
        ComicService.get().getComicSearch(this,keyword);
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    public void onFinish(Object data, String what) {
        switch (what){
            case Global.REQUEST_COMICS_SEARCH:
                String html = (String) data;
                Log.d(TAG,html);
                break;
        }
    }

    @Override
    public void onError(String msg, String what) {

    }
}
