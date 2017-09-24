package ljw.comicviewer.others;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ljw.comicviewer.R;
/**
 * Created by ljw on 2017-09-04 004.
 */

public class BottomDialog extends Dialog{
    private Context context;
    private View.OnClickListener ClickOK;
    @BindView(R.id.dialog_title)
    TextView txt_title;
    @BindView(R.id.dialog_content)
    TextView txt_content;
    @BindView(R.id.dialog_ok)
    Button btn_ok;
    @BindView(R.id.dialog_cancel)
    Button btn_cancel;
    @BindView(R.id.dialog_btns)
    LinearLayout btns;

    public BottomDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public BottomDialog(Context context, int themeResId, View.OnClickListener ClickOK) {
        super(context, themeResId);
        this.context = context;
        this.ClickOK = ClickOK;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        ButterKnife.bind(this);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0,0,0,0);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y=0;
        window.setAttributes(lp);


    }

    @Override
    public void show() {
        super.show();
        btn_ok.setOnClickListener(ClickOK);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public View.OnClickListener getClickOK() {
        return ClickOK;
    }

    public void setClickOK(View.OnClickListener clickOK) {
        ClickOK = clickOK;
    }

    public void hiddenTitle(boolean hidden){
        txt_title.setVisibility(hidden ? View.GONE : View.VISIBLE);
    }

    public void hiddenButton(boolean hidden){
        btns.setVisibility(hidden ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setTitle(CharSequence title) {
        txt_title.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        txt_title.setText(context.getResources().getString(titleId));
    }

    public void setText(CharSequence title) {
        txt_content.setText(title);
    }

    public void setText(int titleId) {
        txt_content.setText(context.getResources().getString(titleId));
    }

    public void setTextOk(CharSequence title) {
        btn_ok.setText(title);
    }

    public void setTextOk(int titleId) {
        btn_ok.setText(context.getResources().getString(titleId));
    }

    public void setTextCancel(CharSequence title) {
        btn_cancel.setText(title);
    }

    public void setTextCancel(int titleId) {
        btn_cancel.setText(context.getResources().getString(titleId));
    }


}
