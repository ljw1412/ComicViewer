package ljw.comicviewer.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.Global;
import ljw.comicviewer.R;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.store.RuleStore;
import ljw.comicviewer.ui.AboutActivity;
import ljw.comicviewer.ui.HistoryActivity;
import ljw.comicviewer.ui.HomeActivity;
import ljw.comicviewer.ui.SettingsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends BaseFragment {
    private static String TAG = MineFragment.class.getSimpleName()+"----";
    private Context context;
    @BindView(R.id.layout_my_setting)
    RelativeLayout btn_setting;
    @BindView(R.id.layout_my_history)
    RelativeLayout btn_history;
    @BindView(R.id.layout_my_about)
    RelativeLayout btn_about;
    @BindView(R.id.title)
    TextView nav_title;

    public MineFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        context = getActivity();
        ButterKnife.bind(this,view);
        initView();
        initListener();
        return view;
    }

    @Override
    public void initView() {
        if(getActivity() instanceof HomeActivity)
            ((HomeActivity) getActivity()).setTitle(nav_title,R.string.txt_mine);
    }

    @Override
    public void initLoad() {

    }

    private void initListener(){
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HistoryActivity.class);
                startActivity(intent);
            }
        });
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivityForResult(intent, Global.HomeToSetting);
            }
        });
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AboutActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Global.HomeToSetting:
                if(data!=null && data.getBooleanExtra("theme_change",false)){
                    Log.d(TAG, "onActivityResult: "+data.getBooleanExtra("theme_change",false));
                    ((HomeActivity) context).changeTheme();
                    ((HomeActivity) context).resetView();
                }
                if(!RuleStore.get().getComeFrom().equals(AppStatusStore.get().getCurrentSource())){
                    AppStatusStore.get().setSourceReplace(true);
                    AppStatusStore.get().setCurrentSource(RuleStore.get().getComeFrom());
                    Log.d(TAG, "onActivityResult: 切换成功");
                }
                break;
        }
    }
}
