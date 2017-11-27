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
        setRuleStr();
        return ruleParser;
    }

    public static void setRuleStr(){
        ruleStore = RuleStore.get();
        ruleStr = ruleStore.getCurrentRule();
        if(ruleStr!=null){
            setJsonObject();
        }else{
            throw new NullPointerException("set rule fail.");
        }
    }

    private static void setJsonObject() {
        jsonObject = JSON.parseObject(ruleStr);
    }

    public void parseAll(){
        parseHost();
        parseDomain();
        parseImgHost();
        parseListPage();
        parseLatestPage();
        parseDetailsPage();
        parseDetailsChapter();
        parseSearchPage();
    }

    private void parseHost(){
        String host = jsonObject.get("host").toString();
        ruleStore.setHost(host);
    }

    private void parseDomain(){
        Object domain = jsonObject.get("domain");
        ruleStore.setDomain(domain==null ? null : domain.toString());
    }

    private void parseImgHost(){
        Object imghost = jsonObject.get("imghost");
        ruleStore.setImgHost(imghost==null ? null : imghost.toString());
    }

    private void parseListPage(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setListRule(parsePage("list"));
    }

    private void parseLatestPage(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setLatestRule(parsePage("latest-list-url"));
    }

    private void parseDetailsPage(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setDetailsRule(parsePage("details_page"));
    }

    private void parseDetailsChapter(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setDetailsChapterRule(parsePage("details_page_chapter"));
    }

    private void parseSearchPage(){
        if(ruleStr == null)
            throw new NullPointerException("规则没有定义！");
        ruleStore.setSearchRule(parsePage("search_page"));
    }

    private Map<String,String> parsePage(String key){
        Map<String,String> map = new HashMap<>();
        JSONObject list = jsonObject.getJSONObject(key);
        if(list.get("url")!=null)
            map.put("url",list.get("url").toString());
        JSONObject cssQuery = list.getJSONObject("cssQuery");
        for(Object k: cssQuery.keySet()){
            map.put(k.toString(),cssQuery.get(k).toString());
        }
        return map;
    }

    private void parseType(){
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
