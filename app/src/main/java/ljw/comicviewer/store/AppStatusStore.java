package ljw.comicviewer.store;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.R;
import ljw.comicviewer.bean.Theme;

/**
 * Created by ljw on 2017-12-27 027.
 */

public class AppStatusStore {
    private final String TAG = this.getClass().getSimpleName()+"----";
    private static AppStatusStore appStatusStore;
    private String currentSource;
    private boolean sourceReplace = false;//是否更换源
    private List<Theme> themes = new ArrayList<>();

    private AppStatusStore() {
    }
    public static AppStatusStore get(){
        if (appStatusStore == null){
            appStatusStore = new AppStatusStore();
        }
        return appStatusStore;
    }

    public String getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(String currentSource) {
        this.currentSource = currentSource;
    }

    public boolean isSourceReplace() {
        return sourceReplace;
    }

    public void setSourceReplace(boolean sourceReplace) {
        this.sourceReplace = sourceReplace;
    }

    public List<Theme> getThemes(Context context) {
        initThemes(context);
        return themes;
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }

    public void initThemes(Context context){
        if(themes.size()>0) return;
        Resources resources = context.getResources();
        String[] colorsName = resources.getStringArray(R.array.theme_name);
        int[] colors = resources.getIntArray(R.array.theme_color);
        int length = colorsName.length < colors.length ? colorsName.length : colors.length;
        for(int i = 0 ;i<length;i++){
            Theme theme = new Theme();
            theme.setName(colorsName[i]);
            theme.setColor(colors[i]);
            themes.add(theme);
        }
    }
}
