package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.db.CollectionHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends BaseFragment{
    private Context context;
    @BindView(R.id.dbtest)
    TextView test;
    @BindView(R.id.dbtestbtn)
    Button btn_test;

    public CollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.bind(this,view);
        context = getActivity();
        initLoad();
        return view;
    }

    @Override
    public void initLoad() {
        //数据库处理
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test.setText("");
                CollectionHolder collectionHolder = new CollectionHolder(context);
                List<Comic> comics = collectionHolder.getComics();
                for(Comic comic : comics){
                    test.append(comic.toString());
                }
            }
        });
    }

    @Override
    public void initView() {

    }
}
