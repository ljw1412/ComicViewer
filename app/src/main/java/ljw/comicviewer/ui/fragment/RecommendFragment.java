package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
import ljw.comicviewer.ui.FilterActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendFragment extends BaseFragment {
    private String TAG = NewAddFragment.class.getSimpleName()+"----";
    private Context context;
    @BindView(R.id.recommend_btn_all_comics)
    TextView view_btn_all_comics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        context = getActivity();
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    @Override
    public void initView() {

        addListener();
    }

    public void addListener(){
        view_btn_all_comics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilterActivity.class);
                startActivity(intent);
            }
        });
    }
}
