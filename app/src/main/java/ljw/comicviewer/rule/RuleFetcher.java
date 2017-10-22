package ljw.comicviewer.rule;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.store.RuleStore;

/**
 * Created by ljw on 2017-10-22 022.
 */

public class RuleFetcher {
    private final String TAG = this.getClass().getSimpleName()+"----";
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

    private String text(Element element){
        return element.text();
    }

    private String text(Elements elements){
        return elements.text();
    }

    private String html(Element element){
        return element.html();
    }

    private String html(Elements elements){
        return elements.html();
    }

    //解析
    private Object parser(String rule){
        List<String> ruleList = splitJS(rule);
        for (String ss : ruleList ) {
            Log.d(TAG, "parser: "+ss);
        }

        return null;
    }

    //拆分js代码
    private List<String> splitJS(String js){
        js = js.replaceAll("==",".==");
        Log.d(TAG, "splitJS: "+js);
        List<String> ruleList = new ArrayList<>();
        char[] chars = js.toCharArray();
        String temp = "";
        boolean inBrackets = false; //在单引号中
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


    //获取列表页信息
    public List<Comic> getComicList(String html){
        ruleParser.parseListPage();
        String firstKey = "list";
        List<Comic> comics = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Map<String,String> map = ruleStore.getListRule();
        if(map.size()>0){
            parser(map.get("items"));
            parser(map.get("comic-id"));
            parser(map.get("comic-name"));
            parser(map.get("comic-image-url"));
            parser(map.get("comic-score"));
            parser(map.get("comic-update"));
            parser(map.get("comic-update-status"));
            parser(map.get("comic-end"));

        }


//        Elements comicItems = doc.select("#contList li");
//        for (Element element: comicItems) {
//            Comic comic = new Comic();
//            comic.setId(getPattern(REG_COMIC_ID,element.select("a.bcover").attr("href"),1));
//            comic.setName(element.select("a.bcover").attr("title"));
//            comic.setImageUrl(getPattern(REG_COVER_URL_REG,element.select("a.bcover img").toString(),0));
//            comic.setScore(element.select("span em").text());
//            comic.setUpdate("更新于"+getPattern(REG_DATE,element.select("span.updateon").html(),0));
//            comic.setUpdateStatus(element.select("a.bcover span.tt").text());
//            comic.setEnd(element.select("a.bcover span").last().className().equals("fd") ? true : false);
//            list.add(comic);
////           list.add(element.attr("href")+"\n");
//        }
        return comics;
    }


}
