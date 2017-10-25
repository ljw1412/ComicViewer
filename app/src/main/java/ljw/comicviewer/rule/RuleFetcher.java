package ljw.comicviewer.rule;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.store.RuleStore;

/**
 * Created by ljw on 2017-10-22 022.
 */

public class RuleFetcher {
    private final String TAG = this.getClass().getSimpleName()+"----";
    private final boolean DEBUG_MODE = false;
    private static RuleFetcher ruleFetcher = null;
    private static RuleStore ruleStore;
    private static RuleParser ruleParser;

    private RuleFetcher() {
    }

    public static RuleFetcher get(){
        if(ruleFetcher == null){
            ruleFetcher = new RuleFetcher();
        }
        ruleStore = RuleStore.get();
        ruleParser = RuleParser.get();
        return ruleFetcher;
    }

    /**
     * 正则表达式获得内容
     * @param reg 正则表达式
     * @param str 需要匹配的文字
     * @param i   结果下标
     * @return
     */
    private String getPattern(String reg,String str,int i){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            return matcher.group(i);
        }
        return null;
    }

    private boolean isExits(String reg,String str){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    //拆分js代码
    private List<String> splitJS(String js){
        //优化js代码更好的管道化
        js = js.replaceAll("==",".==").replaceAll("!=",".!=");
        if(DEBUG_MODE) Log.d(TAG, "splitJS: "+js);
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
                "=='(.+)'",
                "!='(.+)'",
                "find\\('(.+)'\\)",
                "children\\('(.+)'\\)",
                "replace\\('(.+)','(.*)'\\)"
        };

        Object currentObj = element;
        String cssQuery = "";
        int index = -1;
        for (String ss : ruleList ) {
            //Log.d(TAG, "parser: "+ss);
            for(int i = 0 ; i < regexps.length ; i++ ){
                if(isExits(regexps[i] , ss)) {
                    boolean error = true;
                    switch (i){
                        case 12://find()
                        case 13://children()
                        case 0://$()
                            cssQuery = getPattern(regexps[i], ss, 1);
                            if(currentObj instanceof Document){
                                currentObj =  ((Document) currentObj).select(cssQuery);
                                error = false;
                            }else if (currentObj instanceof Element){
                                currentObj =  ((Element) currentObj).select(cssQuery);
                                error = false;
                            }
                            break;
                        case 1://attr()
                            cssQuery = getPattern(regexps[i], ss, 1);
                            if (currentObj instanceof Element){
                                currentObj = ((Element) currentObj).attr(cssQuery);
                                error = false;
                            }else if(currentObj instanceof Elements){
                                currentObj = ((Elements) currentObj).attr(cssQuery);
                                error = false;
                            }
                            break;
                        case 2://match()
                            cssQuery = getPattern(regexps[i], ss, 1);
                            index = Integer.valueOf(getPattern(regexps[i], ss, 2));
                            if (currentObj instanceof String){
                                currentObj = getPattern(cssQuery, element.toString(),index);
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
                            index = Integer.valueOf(getPattern(regexps[i], ss, 1));
                            if(currentObj instanceof Elements){
                                currentObj = ((Elements) currentObj).get(index);
                                error = false;
                            }
                            break;
                        case 10://==''
                            cssQuery = getPattern(regexps[i], ss, 1);
                            if(currentObj instanceof String) {
                                currentObj = currentObj.equals(cssQuery);
                                error = false;
                            }
                            break;
                        case 11://!=''
                            cssQuery = getPattern(regexps[i], ss, 1);
                            if(currentObj instanceof String) {
                                currentObj = !currentObj.equals(cssQuery);
                                error = false;
                            }
                            break;
                        case 14:
                            cssQuery =getPattern(regexps[i], ss, 1);
                            String query2 = getPattern(regexps[i], ss ,2);
                            Log.e(TAG, "parser: "+cssQuery +" "+query2 );
                            if (currentObj instanceof String){
                                currentObj = ((String) currentObj).replaceAll(cssQuery,query2);
                                error = false;
                            }
                            break;
                    }
                    if (error)
                        Log.e(TAG, "case "+ i +" error: 当前规则\""+rule+"\"存在问题！可能是规则错误。");
                    //if(DEBUG_MODE) Log.d(TAG, "parser: " + cssQuery);
                }
            }

        }
        return currentObj;
    }
}
