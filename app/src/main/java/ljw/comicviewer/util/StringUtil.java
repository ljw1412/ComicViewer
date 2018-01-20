package ljw.comicviewer.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
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

    /**
     * 根据顺序拼接字符串
     * @param order 顺序
     * @param map 选择类型情况 当值为""不加入拼接
     * @param separate 分隔符
     * @return
     */
    public static String join(List<String> order,Map<String,String> map, String separate){
        String res = "";
        List<String> joinList = new ArrayList<>();
        for(int i = 0 ; i < order.size() ; i++){
            if(order.get(i)==null && order.get(i).equals("")) continue;
            String typeValue = map.get(order.get(i));
            if(typeValue!=null && !typeValue.equals("")){
                joinList.add(typeValue);
            }
        }
        for(int i = 0 ; i < joinList.size() ; i++){
            res += joinList.get(i) + (i != joinList.size() - 1 ? separate : "");
        }
        return res;
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
}
