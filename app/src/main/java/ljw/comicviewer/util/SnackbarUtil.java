package ljw.comicviewer.util;

import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ljw.comicviewer.Global;
import ljw.comicviewer.R;

/**
 * Created by ljw on 2017-11-25 025.
 */

public class SnackbarUtil {

    public static Snackbar newColorfulSnackbar(View view, String msg, int bgColor){
        Snackbar snackbar = Snackbar.make(view,msg, Global.SNACKBAR_DURATION);
        View snackbarView = snackbar.getView();
        if (snackbarView!=null){
            snackbarView.setBackgroundColor(bgColor);
        }
        return snackbar;
    }

    public static Snackbar newColorfulSnackbar(View view, String msg, int bgColor, int msgColor){
        Snackbar snackbar = Snackbar.make(view,msg, Global.SNACKBAR_DURATION);
        View snackbarView = snackbar.getView();
        if (snackbarView!=null){
            snackbarView.setBackgroundColor(bgColor);
            ((TextView) snackbarView.findViewById(R.id.snackbar_text)).setTextColor(msgColor);//获取Snackbar的message控件，修改字体颜色
        }
        return snackbar;
    }

    public static Snackbar newAddImageSnackar(View view, String msg,int resId){
        Snackbar snackbar = Snackbar.make(view,msg, Global.SNACKBAR_DURATION);
        snackbarAddView(snackbar,resId);
        return snackbar;
    }

    public static Snackbar newAddImageColorfulSnackar(View view, String msg,int resId, int bgColor){
        Snackbar snackbar = newColorfulSnackbar(view,msg,bgColor);
        snackbarAddView(snackbar,resId);
        return snackbar;
    }

    public static Snackbar newAddImageColorfulSnackar(View view, String msg,int resId, int bgColor, int msgColor){
        Snackbar snackbar = newColorfulSnackbar(view,msg,bgColor,msgColor);
        snackbarAddView(snackbar,resId);
        return snackbar;
    }

    /**
     * 向Snackbar文字左边中添加图片
     * @param snackbar
     * @param resId
     */
    public static void snackbarAddView(Snackbar snackbar,int resId) {
        View snackbarView = snackbar.getView();
        TextView msgView = ((TextView) snackbarView.findViewById(R.id.snackbar_text));
        Snackbar.SnackbarLayout snackbarLayout =(Snackbar.SnackbarLayout)snackbarView;
        View add_view = LayoutInflater.from(snackbarView.getContext()).inflate(R.layout.snackbar_addview,null);
        ImageView imgView = (ImageView) add_view.findViewById(R.id.snackbar_image);
        imgView.setImageResource(resId);
        msgView.setPadding(imgView.getMaxWidth()+10,msgView.getPaddingTop(),msgView.getRight(),msgView.getPaddingBottom());

        snackbarLayout.addView(add_view, 0);
    }

}
