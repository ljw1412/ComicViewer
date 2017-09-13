package ljw.comicviewer.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by ljw on 2017-08-29 029.
 */

public class DisplayUtil {
    private static String TAG = "DisplayUtil----";

    //根据屏幕大小获取网格列数
    public static int getGridNumColumns(Context context,int itemWidth){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        //屏幕宽度算法:"屏幕宽度（像素）/屏幕密度"
        float screenWidth = dm.widthPixels/dm.density;//屏幕宽度(dp)
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

}
