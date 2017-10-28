package ljw.comicviewer.http;



import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;
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
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.bean.ManhuaguiComicInfo;
import ljw.comicviewer.rule.RuleFetcher;
import ljw.comicviewer.rule.RuleParser;
import ljw.comicviewer.store.RuleStore;


/**
 * Created by ljw on 2017-08-23 023.
 */

public class ComicFetcher {
    private static String REG_DATE = "\\w+-\\w+-\\w+";
    private static String REG_COVER_URL_REG = "http[s]?:.+.(?:jpg|jpeg|png|gif|bmp)";
    private static String REG_COMIC_ID = "/(\\d+)/";
    private static String REG_CHAPTER_ID = "/(\\d+).html";

    /**
     * 正则表达式获得内容
     * @param reg 正则表达式
     * @param str 需要匹配的文字
     * @param i   结果下标
     * @return
     */
    private static String getPattern(String reg,String str,int i){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            return matcher.group(i);
        }
        return null;
    }

    private static RuleParser getRuleParser(){
        return RuleParser.get();
    }

    private static RuleStore getRuleStore(){
        return RuleStore.get();
    }

    private static RuleFetcher getRuleFetcher(){
        return RuleFetcher.get();
    }

    //获取列表页信息(规则版)
    public static List<Comic> getComicList(String html){
        getRuleParser().parseListPage();
        Map<String,String> map = getRuleStore().getListRule();
        RuleFetcher ruleFetcher = getRuleFetcher();

        List<Comic> comics = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        if(map!=null && map.size()>0){
            Elements items = (Elements) ruleFetcher.parser(doc , map.get("items"));
            for(Element element : items){
                Comic comic = new Comic();
                comic.setId((String) ruleFetcher.parser(element,map.get("comic-id")));
                comic.setName((String) ruleFetcher.parser(element,map.get("comic-name")));
                comic.setImageUrl((String) ruleFetcher.parser(element,map.get("comic-image-url")));
                comic.setScore((String) ruleFetcher.parser(element,map.get("comic-score")));
                comic.setUpdate((String) ruleFetcher.parser(element,map.get("comic-update")));
                comic.setUpdateStatus((String) ruleFetcher.parser(element,map.get("comic-update-status")));
                comic.setEnd((Boolean) ruleFetcher.parser(element,map.get("comic-end")));
                comics.add(comic);
            }
        }
        return comics;
    }

    //获取详细页信息(规则版)
    public static Comic getComicDetails(String html,Comic comic){
        getRuleParser().parseDetailsPage();
        Map<String,String> map = getRuleStore().getDetailsRule();

        Document doc = Jsoup.parse(html);
        comic.setName((String) getRuleFetcher().parser(doc,map.get("comic-name")));
        comic.setImageUrl((String) getRuleFetcher().parser(doc,map.get("comic-image-url")));
        comic.setTag((String) getRuleFetcher().parser(doc,map.get("comic-tag")));
        comic.setAuthor((String) getRuleFetcher().parser(doc,map.get("comic-author")));
        comic.setUpdateStatus("更新至"+(String) getRuleFetcher().parser(doc,map.get("comic-update-status")));
        comic.setUpdate((String) getRuleFetcher().parser(doc,map.get("comic-update")));
        comic.setEnd((Boolean) getRuleFetcher().parser(doc,map.get("comic-end")));
        comic.setInfo((String) getRuleFetcher().parser(doc,map.get("comic-info")));
        comic.setBan((Boolean) getRuleFetcher().parser(doc,map.get("comic-is-ban")));
//        if(doc.select(".detail-list li.status span span").size()>0) {
//            comic.setUpdateStatus("更新至"+doc.select(".detail-list li.status span a").get(0).text());
//            comic.setUpdate(doc.select(".detail-list li.status span span").get(1).text());
//            comic.setEnd(doc.select(".detail-list li.status span span").get(0).text().contains("完结") ? true : false);
//        }else{
//            comic.setUpdateStatus("暂未更新。敬请期待！！！");
//            comic.setUpdate("不详");
//            comic.setEnd(false);
//        }
        return comic;
    }

    //漫画搜索(规则版)
    public static CallBackData getSearchResults(String html){
        getRuleParser().parseSearchPage();

        List<Comic> comics = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Map<String,String> map = getRuleStore().getSearchRule();
        if(map!=null && map.size()>0){
            Elements items = (Elements) getRuleFetcher().parser(doc , map.get("items"));
            for(Element element : items){
                Comic comic = new Comic();
                comic.setId((String) getRuleFetcher().parser(element,map.get("comic-id")));
                comic.setName((String) getRuleFetcher().parser(element,map.get("comic-name")));
                comic.setImageUrl((String) getRuleFetcher().parser(element,map.get("comic-image-url")));
                comic.setScore((String) getRuleFetcher().parser(element,map.get("comic-score")));
                comic.setUpdate((String) getRuleFetcher().parser(element,map.get("comic-update")));
                comic.setUpdateStatus("更新至"+(String) getRuleFetcher().parser(element,map.get("comic-update-status")));
                comic.setEnd((Boolean) getRuleFetcher().parser(element,map.get("comic-end")));
                comic.setAuthor((String) getRuleFetcher().parser(element,map.get("comic-author")));
                comic.setTag((String) getRuleFetcher().parser(element,map.get("comic-tag")));
                comic.setInfo((String) getRuleFetcher().parser(element,map.get("comic-info")));
                comics.add(comic);
            }
        }
        CallBackData backData = new CallBackData();
        backData.setObj(comics);
        int maxPage = 99999;
        try {
            maxPage = Integer.valueOf((String) getRuleFetcher().parser(doc,map.get("max-page")));
        }catch (Exception e){

        }
        backData.setArg1(maxPage);
        return backData;
    }


    //获得漫画章节
    public static Comic getComicChapters(String html,Comic comic) {
        List<Chapter> chapters = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements chapter_list = doc.select(".chapter-list");
        for (Element c:chapter_list) {
            //类型检测，默认类型为其他
            int type = 2;
            Element preElement = c.previousElementSibling();
            if (preElement.className().contains("chapter-page")){
                //如果前一个元素为分页元素
                preElement = preElement.previousElementSibling();
            }
            if(preElement.tagName()=="h4"){
                String typeString = preElement.text();
                if (typeString.contains("单行本")){
                    type = 0;
                }else if (typeString.contains("单话")){
                    type = 1;
                }
            }
            //具体章节处理
            Elements chapter_ul = c.select("ul");
            for(Element ul : chapter_ul) {
                Elements innerChapters = ul.select("a.status0");
                for (int i = innerChapters.size() - 1; i >= 0; i--) {//由于获取是依次所以的到的元素组是倒序的，因此要正过来
                    Element e = innerChapters.get(i);
                    Chapter chapter = new Chapter();
                    chapter.setComic_id(comic.getId());
                    chapter.setChapter_id(getPattern(REG_CHAPTER_ID, e.attr("href"), 1));
                    chapter.setChapter_name(e.attr("title"));
                    chapter.setType(type);
                    chapters.add(chapter);
                }
            }
            for (Chapter chapter : chapters) {
                Log.d("====", "getComicChapters: "+chapter.toString());
            }
        }
        comic.setChapters(chapters);
        return comic;
    }

    //获得解析当前章节
    public static ManhuaguiComicInfo parseCurrentChapter(String s){
        Gson gson = new Gson();
        ManhuaguiComicInfo info = gson.fromJson(s,ManhuaguiComicInfo.class);
        return info;
    }
}
