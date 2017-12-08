package ljw.comicviewer.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ljw on 2017-10-31 031.
 */

public class StringUtil {
    private static boolean DEBUG = false;

    /**
     * 正则表达式获得内容
     * @param reg 正则表达式
     * @param str 需要匹配的文字
     * @param i   结果下标
     * @return
     */
    public static String getPattern(String reg,String str,int i){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            return matcher.group(i);
        }
        return null;
    }

    public static String[] getPatternAll(String reg,String str){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        List<String> list = new ArrayList<>();
        while (matcher.find()){
            list.add(matcher.group());
        }
        if (list.size()>0){
            return list.toArray(new String[list.size()]);
        }
        return null;
    }

    public static boolean isExits(String reg, String str){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        boolean res = matcher.find();
        if(DEBUG) Log.d("StringUtil----", "isExits: {"+reg+","+str+"}"+res);
        return res;
    }

    public static String join(String[] strings, String separate) {
        String res = "";
        for(int i = 0 ; i < strings.length ; i++){
            res += strings[i] + (i != strings.length - 1 ? separate : "");
        }
        return res;
    }

    public static String join(List<String> list, String separate) {
        String res = "";
        for(int i = 0 ; i < list.size() ; i++){
            if(list.get(i).equals("")) continue;
            res += list.get(i) + (i != list.size() - 1 ? separate : "");
        }
        return res;
    }
}
