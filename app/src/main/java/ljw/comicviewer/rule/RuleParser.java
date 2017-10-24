package ljw.comicviewer.rule;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljw.comicviewer.store.RuleStore;

/**
 * Created by ljw on 2017-10-18 018.
 */

public class RuleParser {
    private final String TAG = this.getClass().getSimpleName()+"----";
    private static RuleParser ruleParser = null;
    private static String ruleStr;
    private static JSONObject jsonObject;
    private static RuleStore ruleStore;

    private RuleParser() {
    }

    public static RuleParser get(){
        if(ruleParser == null){
            ruleParser = new RuleParser();
        }
        ruleStore = RuleStore.get();
        ruleStr = ruleStore.getCurrentRule();
        if(ruleStr!=null){
            setJsonObject();
        }else{
            throw new NullPointerException("set rule fail.");
        }
        return ruleParser;
    }

    public RuleParser setRuleStr(String str){
        ruleStr = str;
        setJsonObject();
        return ruleParser;
    }

    private static void setJsonObject() {
        jsonObject = JSON.parseObject(ruleStr);
    }


    public void parseListPage(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setListRule(parsePage("list"));
    }

    public void parseLatestPage(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setLatestRule(parsePage("latest-list-url"));
    }

    public void parseDetailsPage(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setDetailsRule(parsePage("details_page"));
    }

    public void parseSearchPage(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setSearchRule(parsePage("search_page"));
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

    public void parseType(){
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
        for (Map.Entry<String,List<Map<String,String>>> entry:map.entrySet()){
            Log.d(TAG, "onCreate: "+entry.getKey());
            for (Map<String,String> m : map.get(entry.getKey())){
                for (Map.Entry<String,String> kv: m.entrySet()){
                    Log.d(TAG, "onCreate: "+kv.getKey()+" "+kv.getValue());
                }
                Log.d(TAG, "onCreate: ");
            }
        }
        ruleStore.setTypeRule(map);
    }





}
