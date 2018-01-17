package ljw.comicviewer.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by ljw on 2018-01-17 017.
 */

public class PreferenceUtil {
    public static String PreferenceName = "preference";
    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PreferenceName,Context.MODE_APPEND);
    }

    public static void modify(Context context,String key,Object value){
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }else if (value instanceof String){
            editor.putString(key, (String) value);
        }else if (value instanceof Long){
            editor.putLong(key, (Long) value);
        }else if (value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value);
        }else if (value instanceof Float){
            editor.putFloat(key, (Float) value);
        }else if (value instanceof Set){
            editor.putStringSet(key, (Set<String>) value);
        }
        editor.commit();
    }

}
