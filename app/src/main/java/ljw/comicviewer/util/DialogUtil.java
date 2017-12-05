package ljw.comicviewer.util;

import android.content.Context;

import ljw.comicviewer.R;
import ljw.comicviewer.others.BottomDialog;

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

}
