package ljw.comicviewer.rule;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.util.StringUtil;

/**
 * Created by ljw on 2017-10-22 022.
 */

public class RuleFetcher {
    private final String TAG = this.getClass().getSimpleName()+"----";
    private final boolean DEBUG_MODE = false;
    private static RuleFetcher ruleFetcher = null;

    private RuleFetcher() {
    }

    public static RuleFetcher get(){
        if(ruleFetcher == null){
            ruleFetcher = new RuleFetcher();
        }
        return ruleFetcher;
    }

    //拆分js代码
    private List<String> splitJS(String js){
        //优化js代码更好的管道化
        if(DEBUG_MODE) Log.e(TAG, "splitJS: "+js);
        js = js.replaceAll("==",".==").replaceAll("!=",".!=");
        List<String> ruleList = new ArrayList<>();
        char[] chars = js.toCharArray();
        String temp = "";
        boolean inBrackets = false; //在括号中
        for(int i = 0 ; i < chars.length; i++){
            if(chars[i]=='(' || chars[i]==')'){
                inBrackets = !inBrackets;
            }
            if(chars[i]=='.' && !inBrackets){
                ruleList.add(temp);
                temp = "";
            }else{
                temp += chars[i];
            }
        }
        ruleList.add(temp);//将最后一条也放进去
        return ruleList;
    }

    //解析
    public Object parser(Object element,String rule){
        if(!(element instanceof Document) && !(element instanceof Element) && !(element instanceof Elements)){
            throw new RuntimeException("第一个参数的类型错误：只能是Document或Element或Elements");
        }

        List<String> ruleList = splitJS(rule);

        String[] regexps = {
                "\\$\\('(.+)'\\)" ,
                "attr\\('(.+)'\\)" ,
                "match\\('(.+)'\\)\\[(.+)\\]",
                "text\\(\\)",
                "html\\(\\)",
                "val\\(\\)",
                "last\\(\\)",
                "first\\(\\)",
                "get\\((.+)\\)",
                "eq\\((.+)\\)",
                "==(.+)",
                "!=(.+)",
                "find\\('(.+)'\\)",
                "children\\('(.+)'\\)",
                "replace\\('(.+)','(.*)'\\)",
                "indexOf\\('(.*)'\\)",
                "length",
                "size\\(\\)",
                "join\\('(.*)'\\)"
        };

        Object currentObj = element;
        String cssQuery = "";
        int index = -1;
        for (String ss : ruleList) {
            for(int i = 0 ; i < regexps.length ; i++ ){
                cssQuery = "";
                try {
                    if(StringUtil.isExits(regexps[i] , ss)) {
                        boolean error = true;
                        switch (i){
                            case 12://find()
                            case 13://children()
                            case 0://$()
                                cssQuery = StringUtil.getPattern(regexps[i], ss, 1);
                                if(currentObj instanceof Document){
                                    currentObj =  ((Document) currentObj).select(cssQuery);
                                    error = false;
                                }else if (currentObj instanceof Element){
                                    currentObj =  ((Element) currentObj).select(cssQuery);
                                    error = false;
                                }
                                break;
                            case 1://attr()
                                cssQuery = StringUtil.getPattern(regexps[i], ss, 1);
                                if (currentObj instanceof Element){
                                    currentObj = ((Element) currentObj).attr(cssQuery);
                                    error = false;
                                }else if(currentObj instanceof Elements){
                                    String temp  = ((Elements) currentObj).attr(cssQuery);
                                    if(temp.equals("") && cssQuery.equals("src")){
                                        cssQuery = "data-src";
                                    }
                                    currentObj = ((Elements) currentObj).attr(cssQuery);
                                    error = false;
                                }
                                break;
                            case 2://match()
                                cssQuery = StringUtil.getPattern(regexps[i], ss, 1);
                                index = Integer.valueOf(StringUtil.getPattern(regexps[i], ss, 2));
                                if (currentObj instanceof String){
                                    //[-1]为获取全部以"空格"隔开
                                    if (index!=-1){
                                        currentObj = StringUtil.getPattern(cssQuery, (String) currentObj,index);
                                    }else {
                                        currentObj = StringUtil.getPatternAll(cssQuery, (String) currentObj);
                                    }
                                    error = false;
                                }
                                break;
                            case 3://text()
                                if(currentObj instanceof Element){
                                    currentObj = ((Element) currentObj).text();
                                    error = false;
                                }else if (currentObj instanceof Elements) {
                                    currentObj = ((Elements) currentObj).text();
                                    error = false;
                                }
                                break;
                            case 4://html()
                                if(currentObj instanceof Element){
                                    currentObj = ((Element) currentObj).html();
                                    error = false;
                                }else if (currentObj instanceof Elements) {
                                    currentObj = ((Elements) currentObj).html();
                                    error = false;
                                }
                                break;
                            case 5://val()
                                if(currentObj instanceof Element){
                                    currentObj = ((Element) currentObj).val();
                                    error = false;
                                }else if (currentObj instanceof Elements) {
                                    currentObj = ((Elements) currentObj).val();
                                    error = false;
                                }
                                break;
                            case 6://last()
                                if(currentObj instanceof Elements){
                                    currentObj = ((Elements) currentObj).last();
                                    error = false;
                                }
                                break;
                            case 7://first()
                                if(currentObj instanceof Elements){
                                    currentObj = ((Elements) currentObj).first();
                                    error = false;
                                }
                                break;
                            case 8://get()
                            case 9://eq()
                                index = Integer.valueOf(StringUtil.getPattern(regexps[i], ss, 1));
                                if(currentObj instanceof Elements){
                                    currentObj = ((Elements) currentObj).get(index);
                                    error = false;
                                }
                                break;
                            case 10://==
                                cssQuery = StringUtil.getPattern(regexps[i], ss, 1);
                                if(currentObj instanceof String) {
                                    cssQuery = StringUtil.getPattern("'(.*)'",cssQuery,1);
                                    currentObj = currentObj.equals(cssQuery);
                                    error = false;
                                }else if(currentObj instanceof Integer){
                                    currentObj = (currentObj == Integer.valueOf(cssQuery));
                                    error = false;
                                }
                                break;
                            case 11://!=
                                cssQuery = StringUtil.getPattern(regexps[i], ss, 1);
                                if(currentObj instanceof String) {
                                    cssQuery = StringUtil.getPattern("'(.*)'",cssQuery,1);
                                    currentObj = !currentObj.equals(cssQuery);
                                    error = false;
                                }else if(currentObj instanceof Integer){
                                    currentObj = (currentObj != Integer.valueOf(cssQuery));
                                    error = false;
                                }
                                break;
                            case 14://replace()
                                cssQuery = StringUtil.getPattern(regexps[i], ss, 1);
                                String query2 = StringUtil.getPattern(regexps[i], ss ,2);
                                if (currentObj instanceof String){
                                    currentObj = ((String) currentObj).replaceAll(cssQuery,query2);
                                    error = false;
                                }
                                break;
                            case 15://indexOf()
                                cssQuery = StringUtil.getPattern(regexps[i], ss, 1);
                                if (currentObj instanceof String){
                                    currentObj = ((String) currentObj).indexOf(cssQuery);
                                    error = false;
                                }
                                break;
                            case 16://length
                            case 17://size()
                                if(currentObj instanceof Elements){
                                    currentObj = ((Elements) currentObj).size();
                                    error = false;
                                }
                                break;
                            case 18:
                                cssQuery = StringUtil.getPattern(regexps[i], ss, 1);
                                if(currentObj instanceof String[]){
                                    currentObj = StringUtil.join((String[]) currentObj,cssQuery);
                                    error = false;
                                }
                                break;
                        }
                        if(DEBUG_MODE)
                            Log.d(TAG, "parser: " + ss + "\n" + currentObj);
                        if (error){
                            Log.e(TAG, "case "+ i +" error: 当前规则\""+rule+"\"存在问题！可能是语法不支持或者对象不存在。");
                            return null;
                        }
                    }
                } catch (Exception e) {
                    return null;
                }
            }

        }
        return currentObj;
    }
}
