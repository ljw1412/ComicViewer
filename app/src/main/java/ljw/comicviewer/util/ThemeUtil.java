package ljw.comicviewer.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bilibili.magicasakura.utils.ThemeUtils;

import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.Theme;
import ljw.comicviewer.store.AppStatusStore;

/**
 * Created by ljw on 2018-01-18 018.
 */

public class ThemeUtil {

    public static int getThemePosition(Context context){
        return PreferenceUtil.getSharedPreferences(context).getInt("theme",0);
    }

    public static int getThemeColor(Context context){
        List<Theme> themes = AppStatusStore.get().getThemes(context);
        if(themes.size()==0) return R.color.accent_red;
        int position = getThemePosition(context);
        if(position>themes.size()) position = 0;
        //修改选择状态
        for (Theme theme : themes){
            theme.setChecked(false);
        }
        themes.get(position).setChecked(true);
        return themes.get(position).getColor();
    }

    public static String getThemeColorName(Context context){
        List<Theme> themes = AppStatusStore.get().getThemes(context);
        if(themes.size()==0) return null;
        int position = getThemePosition(context);
        if(position>themes.size()){
            position = 0;
        }
        return themes.get(position).getName();
    }


    public static void setThemeByColorId(final Context context, @ColorRes final int colorId){
        ThemeUtils.refreshUI(context, new ThemeUtils.ExtraRefreshable() {
            @Override
            public void refreshGlobal(Activity activity) {
                ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(null, null,
                        ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary));
                ((Activity)context).setTaskDescription(description);
                ((Activity)context).getWindow().setStatusBarColor(ThemeUtils.getColorById(context, colorId));
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
                ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(null, null,
                        ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary));
                ((Activity)context).setTaskDescription(description);
                ((Activity)context).getWindow().setStatusBarColor(ThemeUtils.getColor(context, color));
            }

            @Override
            public void refreshSpecificView(View view) {

            }
        });
    }
}
