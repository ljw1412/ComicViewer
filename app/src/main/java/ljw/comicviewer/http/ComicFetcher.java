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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.bean.ManhuaguiComicInfo;


/**
 * Created by ljw on 2017-08-23 023.
 */

public class ComicFetcher {
    private static String REG_DATE = "[0-9]{2,4}-[0-9]{1,2}-[0-9]{1,2}";
    private static String REG_COVER_URL_REG = "http[s]{0,1}:.+.(?:jpg|jpeg|png|gif|bmp)";
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

    //获取列表页信息
    public static List<Comic> getComicList(String html){
        List<Comic> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements comicItems = doc.select("#contList li");
        for (Element element: comicItems) {
            Comic comic = new Comic();
            comic.setId(getPattern(REG_COMIC_ID,element.select("a.bcover").attr("href"),1));
            comic.setName(element.select("a.bcover").attr("title"));
            comic.setImageUrl(getPattern(REG_COVER_URL_REG,element.select("a.bcover img").toString(),0));
            comic.setScore(element.select("span em").text());
            comic.setUpdate("更新于"+getPattern(REG_DATE,element.select("span.updateon").text(),0));
            comic.setUpdateStatus(element.select("a.bcover span.tt").text());
            comic.setEnd(element.select("a.bcover span").last().className().equals("fd") ? true : false);
            list.add(comic);
//           list.add(element.attr("href")+"\n");
        }
        return list;
    }

    //获取详细页信息
    public static Comic getComicDetails(String html,Comic comic){
//        Comic comic = new Comic();
        Document doc = Jsoup.parse(html);


        Element book_cont = doc.select(".book-cont").get(0);
//        comic.setId(id);
        comic.setName(book_cont.select(".book-title h1").text());
        comic.setImageUrl(getPattern(REG_COVER_URL_REG,book_cont.select(".hcover img").toString(),0));
        Elements detail_list = doc.select(".detail-list li");
        comic.setTag(detail_list.get(1).select("span").get(0).select("a").text());
        comic.setAuthor(detail_list.get(1).select("span").get(1).select("a").text());
        if(doc.select(".detail-list li.status span span").size()>0) {
            comic.setUpdateStatus("更新至"+doc.select(".detail-list li.status span a").get(0).text());
            comic.setUpdate(doc.select(".detail-list li.status span span").get(1).text());
            comic.setEnd(doc.select(".detail-list li.status span span").get(0).text().contains("完结") ? true : false);
        }else{
            comic.setUpdateStatus("暂未更新。敬请期待！！！");
            comic.setUpdate("不详");
            comic.setEnd(false);
        }
        comic.setInfo(doc.select(".book-detail #intro-all").text());


        //检查是否被屏蔽
        Element comicInfo = doc.select(".chapter").get(0);
        Elements error = comicInfo.select(".result-none");
        if (error.size()>0){
            comic.setBan(true);
        }
        return comic;
    }

    //漫画搜索
    public static CallBackData getSearchResults(String html){
        List<Comic> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements comicItems = doc.select(".book-result li");
        for (Element li : comicItems) {
            Comic comic = new Comic();
            comic.setId(getPattern(REG_COMIC_ID,li.select("a.bcover").attr("href"),1));
            comic.setName(li.select("dt a").get(0).text());
            comic.setImageUrl(getPattern(REG_COVER_URL_REG,li.select("a.bcover img").toString(),0));
            comic.setUpdateStatus(li.select("a.bcover span.tt").text());
            comic.setScore(li.select(".book-score .score-avg strong").text());
            comic.setUpdate(li.select(".book-detail .status span span").get(1).text());
            comic.setUpdateStatus("更新至"+li.select(".book-detail .status span a").get(0).text());
            comic.setEnd(li.select(".book-detail .status span span").get(0).text().contains("完结") ? true : false);
            comic.setAuthor(li.select(".tags").get(2).select("span a").text());
            comic.setTag(li.select(".tags").get(1).select("span").get(2).select("a").text());
            comic.setInfo(li.select(".intro").text().replaceAll("\\[详情\\]",""));
            list.add(comic);
            Log.d("----", "getSearchResults: "+comic.toString());
        }
        CallBackData backData = new CallBackData();
        backData.setObj(list);
        try {
            int maxPage = (Integer.valueOf(doc.select(".result-count strong").last().text())+9)/10;
            backData.setArg1(maxPage);
        }catch (Exception e){

        }
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
