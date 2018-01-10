package ljw.comicviewer.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.R;

/**
 * Created by ljw on 2017-08-29 029.
 */

public class DisplayUtil {
    private static String TAG = "DisplayUtil----";

    public static float pxToDp(Context context,float px){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return px/dm.density;
    }

    public static float dpToPx(Context context,float dp){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dp*dm.density;
    }

    public static float getScreenWidth(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        //屏幕宽度算法:"屏幕宽度（像素）/屏幕密度"
        return  dm.widthPixels/dm.density;//屏幕宽度(dp)
    }

    //根据屏幕大小获取网格列数
    public static int getGridNumColumns(Context context,int itemWidth){
        float screenWidth = getScreenWidth(context);
        int columns = Math.round(screenWidth/itemWidth);
        Log.d(TAG, "getGridNumColumns: 屏幕宽度(DP):"+screenWidth+",屏幕列数:"+columns);
        return columns;
    }

    public static int getStatusBarHeight(Context context){
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.e(TAG, "getStatusBarHeight状态栏高度:" + statusBarHeight);
        return statusBarHeight;
    }

    public static List<String> strArrayToList(String[] array){
        List<String> list = new ArrayList<>();
        for (int i = 0;i<array.length;i++){
            list.add(array[i]);
        }
        return list;
    }

    public static String[] strListToArray(List<String> list){
        String[] array = new String[list.size()];
        for (int i = 0;i<list.size();i++){
            array[i] = list.get(i);
        }
        return array;
    }

    //unicode解码
    public static String unicodeDecode(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] == 'u') {
                char cc = 0;
                for (int j = 0; j < 4; j++) {
                    char ch = Character.toLowerCase(chars[i + 2 + j]);
                    if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
                        cc |= (Character.digit(ch, 16) << (3 - j) * 4);
                    } else {
                        cc = 0;
                        break;
                    }
                }
                if (cc > 0) {
                    i += 5;
                    sb.append(cc);
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    //隐藏虚拟键盘
    public static void hideKeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive() ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken() , 0 );
        }
    }

    //显示虚拟键盘
    public static void showKeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive() ) {
            imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static int getAttrColor(Context context,int id){
        TypedValue typedValue = new  TypedValue();
        context.getTheme().resolveAttribute(id, typedValue, true);
        final int color = typedValue.data;
        return color;
    }

}
