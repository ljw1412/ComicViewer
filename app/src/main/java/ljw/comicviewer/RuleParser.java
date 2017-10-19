package ljw.comicviewer;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ljw on 2017-10-18 018.
 */

public class RuleParser {
    private static RuleParser ruleParser = null;
    private String ruleStr;
    private JSONObject jsonObject;

    private RuleParser() {
    }

    public static RuleParser get(){
        if(ruleParser == null){
            ruleParser = new RuleParser();
        }
        return ruleParser;
    }

    public RuleParser setRuleStr(String str){
        ruleStr = str;
        setJsonObject();
        return ruleParser;
    }

    public void setJsonObject() {
        jsonObject = JSON.parseObject(ruleStr);
    }

    public Map<String,String> parsePage(String key){
        Map<String,String> map = new HashMap<>();
        JSONObject list = jsonObject.getJSONObject(key);
        map.put("url",list.get("url").toString());
        JSONObject cssQuery = list.getJSONObject("cssQuery");
        for(Object k: cssQuery.keySet()){
            map.put(k.toString(),cssQuery.get(k).toString());
        }
        return map;
    }

    public Map<String,String> parseListPage(){
        return  parsePage("list");
    }

    public Map<String,String> parseLatestPage(){
        return  parsePage("latest-list-url");
    }

    public Map<String,String> parseDetailsPage(){
        return  parsePage("details_page");
    }

    public Map<String, List<Map<String,String>>> parseType(){
        Map<String,List<Map<String,String>>> map = new HashMap<>();
        JSONObject type = jsonObject.getJSONObject("type");
        for(Object key: type.keySet()){
//            Log.d("----", "parseType: "+key);
//            Log.d("----", "parseType: "+type.get(key.toString()).toString());
            JSONArray array = JSON.parseArray(type.get(key.toString()).toString());
            List<Map<String,String>> list = new ArrayList<>();

            for(Object obj : array){
                Map<String,String> inMap = new HashMap<>();
                JSONObject objJson = JSON.parseObject(obj.toString());
                for(Object objKey: objJson.keySet()){
                    inMap.put(objKey.toString(),objJson.get(objKey).toString());
//                    Log.d("----", "parseType: "+objKey+" "+objJson.get(objKey));
                }
                list.add(inMap);
            }
            map.put(key.toString(),list);
        }
        return map;
    }


    public static Map<String, String> parseUrl2(String url) {
        Map<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(url))
            return map;
        Pattern pattern = Pattern.compile("\\{([^{}]*?):([^{}]*?)\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(url);
        return map;
    }



    public static Map<String, String> parseUrl(String url) {
        Map<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(url))
            return map;
        Pattern pattern = Pattern.compile("\\{([^{}]*?):([^{}]*?)\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }
        Pattern pattern2 = Pattern.compile("\\{([^{}]*?):([^{}]*?\\{[^{}]*?\\}[^{}]*?)\\}", Pattern.DOTALL);
        Matcher matcher2 = pattern2.matcher(url);
        while (matcher2.find()) {
            map.put(matcher2.group(1), matcher2.group(2));
        }
        Pattern pattern3 = Pattern.compile("\\{(json):(.*)\\}", Pattern.DOTALL);
        Matcher matcher3 = pattern3.matcher(url);
        while (matcher3.find()) {
            map.put(matcher3.group(1), matcher3.group(2));
        }
        return map;
    }
}
