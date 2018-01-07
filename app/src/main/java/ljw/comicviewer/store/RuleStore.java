package ljw.comicviewer.store;

import android.util.Log;

import java.util.List;
import java.util.Map;

import ljw.comicviewer.bean.Category;

/**
 * Created by ljw on 2017-10-22 022.
 */

public class RuleStore {
    private final String TAG = this.getClass().getSimpleName()+"----";
    private static RuleStore ruleStore = null;
    private String comeFrom;
    private String host;
    private String domain;
    private String cookie;
    private String imgHost;
    private String currentRule;
    private Map<String,String> configRule = homeRule=listRule=latestRule=detailsRule=searchRule=detailsChapterRule=readRule=authorRule = null;
    private Map<String,List<Category>> typeRule;
    private Map<String,String> homeRule;
    private Map<String,String> listRule;
    private Map<String,String> latestRule;
    private Map<String,String> detailsRule;
    private Map<String,String> searchRule;
    private Map<String,String> detailsChapterRule;
    private Map<String,String> readRule;
    private Map<String,String> authorRule;

    private RuleStore(){}

    public static RuleStore get(){
        if(ruleStore == null){
            ruleStore = new RuleStore();
        }
        return ruleStore;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getImgHost() {
        return imgHost;
    }

    public void setImgHost(String imgHost) {
        this.imgHost = imgHost;
    }

    public String getCurrentRule() {
        return currentRule;
    }

    public void setCurrentRule(String currentRule) {
        this.currentRule = currentRule;
    }

    public Map<String, String> getConfigRule() {
        return configRule;
    }

    public void setConfigRule(Map<String, String> configRule) {
        this.configRule = configRule;
    }

    public Map<String, String> getHomeRule() {
        return homeRule;
    }

    public void setHomeRule(Map<String, String> homeRule) {
        this.homeRule = homeRule;
    }

    public Map<String,List<Category>> getTypeRule() {
        return typeRule;
    }

    public void setTypeRule(Map<String,List<Category>> typeRule) {
        this.typeRule = typeRule;
    }

    public Map<String, String> getListRule() {
        return listRule;
    }

    public void setListRule(Map<String, String> listRule) {
        this.listRule = listRule;
    }

    public Map<String, String> getLatestRule() {
        return latestRule;
    }

    public void setLatestRule(Map<String, String> latestRule) {
        this.latestRule = latestRule;
    }

    public Map<String, String> getDetailsRule() {
        return detailsRule;
    }

    public void setDetailsRule(Map<String, String> detailsRule) {
        this.detailsRule = detailsRule;
    }

    public Map<String, String> getSearchRule() {
        return searchRule;
    }

    public void setSearchRule(Map<String, String> searchRule) {
        this.searchRule = searchRule;
    }

    public Map<String, String> getDetailsChapterRule() {
        return detailsChapterRule;
    }

    public void setDetailsChapterRule(Map<String, String> detailsChapterRule) {
        this.detailsChapterRule = detailsChapterRule;
    }

    public Map<String, String> getReadRule() {
        return readRule;
    }

    public void setReadRule(Map<String, String> readRule) {
        this.readRule = readRule;
    }

    public Map<String, String> getAuthorRule() {
        return authorRule;
    }

    public void setAuthorRule(Map<String, String> authorRule) {
        this.authorRule = authorRule;
    }

    public void clearAll(){
        comeFrom = host = domain = cookie = imgHost
                = null;
        typeRule = null;
        configRule=homeRule=listRule=latestRule=detailsRule=searchRule=detailsChapterRule=readRule=authorRule = null;
    }

    public void printRules(){
        Log.d(TAG, "RuleStore情况:typeRule:" + (typeRule==null?"null":"exits") +
                " , listRule:" + (listRule==null?"null":"exits") +
                " , latestRule:" + (latestRule==null?"null":"exits") +
                " , detailsRule:" + (detailsRule==null?"null":"exits") +
                " , searchRule:" + (searchRule==null?"null":"exits") +
                " , detailsChapterRule:" + (detailsChapterRule==null?"null":"exits") +
                " , readRule:" + (readRule==null?"null":"exits")+
                " , homeRule:" + (homeRule==null?"null":"exits"));

    }
}
