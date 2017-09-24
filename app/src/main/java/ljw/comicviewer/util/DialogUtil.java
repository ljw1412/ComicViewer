package ljw.comicviewer.util;

import android.content.Context;
import android.view.View;

import ljw.comicviewer.others.BottomDialog;
import ljw.comicviewer.R;

/**
 * Created by ljw on 2017-09-04 004.
 */

public class DialogUtil {

    public static BottomDialog showBottomDialog(Context context){
        BottomDialog bottomDialog = new BottomDialog(context,R.style.bottom_dialog);
        bottomDialog.create();
        bottomDialog.setTitle(R.string.dialog_title_warming);
        bottomDialog.setText(R.string.dialog_content_warming);
        bottomDialog.setTextOk(R.string.dialog_text_ok);
        bottomDialog.setTextCancel(R.string.dialog_text_cancel);
        return bottomDialog;
    }

    public static void showLoadingDialog(Context context){

    }

}
