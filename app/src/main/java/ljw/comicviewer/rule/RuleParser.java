package ljw.comicviewer.rule;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljw.comicviewer.bean.Category;
import ljw.comicviewer.store.FilterStore;
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
    private static FilterStore filterStore;
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
        filterStore = FilterStore.get();
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
        parseReadPage();
        try{
            parseType();
        }catch (Exception e){
            Log.e(TAG, "parseAll: typeRuleError");
            Log.e(TAG, "parseAll: ", e);
        }
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

    private void parseCookie(){
        Object cookie = jsonObject.get("cookie");
        ruleStore.setCookie(cookie==null ? null : cookie.toString());
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

    private void parseReadPage(){
        if (ruleStr == null){
            throw new NullPointerException("规则没有定义！");
        }
        ruleStore.setReadRule(parsePage("read_page"));
    }

    private Map<String,String> parsePage(String key){
        Map<String,String> map = new HashMap<>();
        JSONObject list = jsonObject.getJSONObject(key);
        if(list.get("url")!=null)
            map.put("url",list.get("url").toString());
        if(list.get("wv-js")!=null){
            map.put("wv-js",list.get("wv-js").toString());
        }
        if(list.get("cssQuery")!=null) {
            JSONObject cssQuery = list.getJSONObject("cssQuery");
            for (Object k : cssQuery.keySet()) {
                map.put(k.toString(), cssQuery.get(k).toString());
            }
        }
        return map;
    }

    //解析type的json，一般type是list的子对象;
    private void parseType(){
        Map<String,List<Category>> map = new HashMap<>();
        JSONObject list = jsonObject.getJSONObject("list");
        JSONObject type = list.getJSONObject("type");
        if (type!=null){
            for(Object key: type.keySet()){
                JSONArray array = JSON.parseArray(type.get(key.toString()).toString());
                List<Category> categories = new ArrayList<>();
                for(Object obj : array){
                    JSONObject objJson = JSON.parseObject(obj.toString());
                    Category category = new Category();
                    Object name = objJson.get("name");
                    Object value = objJson.get("value");
//                    Log.d(TAG, "parseType: "+name+"  "+value);
                    category.setName(name != null ? name.toString() : null);
                    category.setValue(value != null ? value.toString() : null);
                    categories.add(category);
                }
                if(key.toString().equals("order")){//如果是顺序相关再次解析
                    parseOrder(categories);
                }else{
                    map.put(key.toString(),categories);
                }
            }
            ruleStore.setTypeRule(map);
            filterStore.filterStatusReset();//初始化filterStatus
            filterStore.printStore();
        }else{
            ruleStore.setTypeRule(null);
            Log.e(TAG, "parseType: 未找到类型规则" );
        }
    }

    private void parseOrder(List<Category> categories){
        List<String> order = new ArrayList<>();
        for(Category category:categories){
            String name = category.getName();
            String value = category.getValue();
            if (name == null || value == null) continue;
            if(name.equals("separate")){
                filterStore.setSeparate(value);
            }else if(name.equals("endStr")){
                filterStore.setEndStr(value);
            }else{
                try {
                    order.add(Integer.valueOf(name),value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        filterStore.setOrder(order);
    }

}
