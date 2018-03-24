package ljw.comicviewer.util;


import android.content.Context;

import ljw.comicviewer.R;
import ljw.comicviewer.ui.dialog.BottomDialog;

/**
 * 弹窗工具类
 */

public class DialogUtil {
    //底部弹窗
    public static BottomDialog bulidBottomDialog(Context context){
        BottomDialog bottomDialog = new BottomDialog(context,R.style.bottom_dialog);
        bottomDialog.create();
        bottomDialog.setTitle(R.string.dialog_title_warming);
        bottomDialog.setText(R.string.dialog_content_warming);
        bottomDialog.setTextOk(R.string.dialog_comic_ban_retry);
        bottomDialog.setTextCancel(R.string.dialog_comic_ban_cancel);
        return bottomDialog;
    }
}
