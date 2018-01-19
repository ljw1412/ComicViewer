package ljw.comicviewer.others;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.bilibili.magicasakura.utils.ThemeUtils;

import ljw.comicviewer.R;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.util.PreferenceUtil;
import ljw.comicviewer.util.ThemeUtil;

/**
 * Created by ljw on 2018-01-19 019.
 */

public class MyApplication extends Application implements ThemeUtils.switchColor {
    @Override
    public void onCreate() {
        super.onCreate();
        AppStatusStore.get().initThemes(this);
        ThemeUtils.setSwitchColor(this);
    }

    @Override
    public int replaceColorById(Context context, @ColorRes int colorId) {
        int color = ThemeUtil.getThemeColor(context);
        if(context instanceof Activity) {
            ((Activity) context).getWindow().setStatusBarColor(color);
            //虚拟导航栏是否跟随变色
            if(PreferenceUtil.getSharedPreferences(context).getBoolean("nav_bar_auto",false)){
                ((Activity) context).getWindow().setNavigationBarColor(color);
            }else{
                ((Activity) context).getWindow().setNavigationBarColor(
                        ContextCompat.getColor(context, R.color.black));
            }
        }
        return color;
    }

    @Override
    public int replaceColor(Context context, @ColorInt int color) {
        color = ThemeUtil.getThemeColor(context);
        if(context instanceof Activity) {
            ((Activity) context).getWindow().setStatusBarColor(color);
            //虚拟导航栏是否跟随变色
            if(PreferenceUtil.getSharedPreferences(context).getBoolean("nav_bar_auto",false)){
                ((Activity) context).getWindow().setNavigationBarColor(color);
            }else{
                ((Activity) context).getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.black));
            }
        }
        return color;
    }
}
