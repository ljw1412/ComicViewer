package ljw.comicviewer.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;

import com.bilibili.magicasakura.utils.ThemeUtils;

import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.Theme;
import ljw.comicviewer.store.AppStatusStore;

/**
 * 主题工具类
 */

public class ThemeUtil {

    public static int getTheme(Context context){
        return PreferenceUtil.getSharedPreferences(context).getInt("theme",0);
    }

    public static boolean isDefaultTheme(Context context) {
        return getTheme(context) == 0;
    }

    public static int getThemeColor(Context context){
        List<Theme> themes = AppStatusStore.get().getThemes(context);
        if(themes.size()==0) return R.color.theme_red;
        int position = getTheme(context);
        if(position>themes.size()) position = 0;
        return themes.get(position).getColor();
    }

    public static String getThemePrefix(Context context){
        List<Theme> themes = AppStatusStore.get().getThemes(context);
        if(themes.size()==0) return "theme_red";
        int position = getTheme(context);
        if(position>themes.size()) position = 0;
        return themes.get(position).getPrefix();
    }

    public static String getThemeColorName(Context context){
        List<Theme> themes = AppStatusStore.get().getThemes(context);
        if(themes.size()==0) return null;
        int position = getTheme(context);
        if(position>themes.size()){
            position = 0;
        }
        return themes.get(position).getName();
    }


    public static void setThemeByColorId(final Context context, @ColorRes final int colorId){
        ThemeUtils.refreshUI(context, new ThemeUtils.ExtraRefreshable() {
            @Override
            public void refreshGlobal(Activity activity) {
                int color = ContextCompat.getColor(context,colorId);
                updateTheme(context,color);
            }

            @Override
            public void refreshSpecificView(View view) {

            }
        });
    }

    public static void setThemeByColor(final Context context, @ColorInt final int color){
        ThemeUtils.refreshUI(context, new ThemeUtils.ExtraRefreshable() {
            @Override
            public void refreshGlobal(Activity activity) {
                updateTheme(context,color);
            }

            @Override
            public void refreshSpecificView(View view) {

            }
        });
    }

    public static void updateTheme(Context context,@ColorInt int color){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ((Activity)context).getWindow();
            //修改状态栏颜色
            window.setStatusBarColor(ThemeUtils.getColor(context, color));
            //是否虚拟按键自适应
            if(PreferenceUtil.getSharedPreferences(context).getBoolean("nav_bar_auto",false)){
                window.setNavigationBarColor(ThemeUtils.getColor(context,color));
            }else{
                window.setNavigationBarColor(ContextCompat.getColor(context, R.color.black));
            }
            //修改多任务状态时的颜色
            ActivityManager.TaskDescription description =
                    new ActivityManager.TaskDescription(null, null,ThemeUtils.getColor(context,color));
            ((Activity)context).setTaskDescription(description);
        }
    }

}
