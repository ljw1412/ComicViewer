package ljw.comicviewer.others;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.bilibili.magicasakura.utils.ThemeUtils;

import ljw.comicviewer.R;
import ljw.comicviewer.store.AppStatusStore;
import ljw.comicviewer.util.ThemeUtil;

/**
 * Created by ljw on 2018-01-19 019.
 */

public class MyApplication extends Application implements ThemeUtils.switchColor {
    @Override
    public void onCreate() {
        super.onCreate();
        //加载本地主题数组
        AppStatusStore.get().initThemes(this);
        //哔哩哔哩主题绑定
        ThemeUtils.setSwitchColor(this);
    }

    @Override
    public int replaceColorById(Context context, @ColorRes int colorId) {
        if (ThemeUtil.isDefaultTheme(context)) {
            return ContextCompat.getColor(context,colorId);
        }
        String theme = getTheme(context);
        if (theme != null) {
            colorId = getThemeColorId(context, colorId, theme);
        }
        return ContextCompat.getColor(context,colorId);
    }

    @Override
    public int replaceColor(Context context, @ColorInt int originColor) {
        if (ThemeUtil.isDefaultTheme(context)) {
            return originColor;
        }
        String theme = getTheme(context);
        int colorId = -1;

        if (theme != null) {
            colorId = getThemeColor(context, originColor, theme);
        }
        return colorId == -1 ? originColor : ContextCompat.getColor(context,colorId);
    }

    private String getTheme(Context context){
        return ThemeUtil.getThemePrefix(context);
    }

    private @ColorRes int getThemeColorId(Context context, int colorId, String theme) {
        switch (colorId) {
            case R.color.theme_color_primary:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case R.color.theme_color_primary_dark:
                return context.getResources().getIdentifier(theme + "_dark", "color", getPackageName());
            case R.color.theme_color_primary_trans:
                return context.getResources().getIdentifier(theme + "_trans", "color", getPackageName());
        }
        return colorId;
    }

    private @ColorRes int getThemeColor(Context context, int color, String theme) {
        int color1 = ContextCompat.getColor(context,R.color.theme_color_primary);
        int color2 = ContextCompat.getColor(context,R.color.theme_color_primary_dark);
        int color3 = ContextCompat.getColor(context,R.color.theme_color_primary_trans);
        if (color == color1){
            return context.getResources().getIdentifier(theme, "color", getPackageName());
        }else if (color == color2){
            return context.getResources().getIdentifier(theme + "_dark", "color", getPackageName());
        }else if (color == color3){
            return context.getResources().getIdentifier(theme + "_trans", "color", getPackageName());
        }
        return -1;
    }

}
