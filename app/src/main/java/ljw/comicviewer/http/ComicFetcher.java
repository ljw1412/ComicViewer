package ljw.comicviewer.http;



import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljw.comicviewer.bean.Author;
import ljw.comicviewer.bean.CallBackData;
import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.bean.Section;
import ljw.comicviewer.rule.RuleFetcher;
import ljw.comicviewer.store.RuleStore;


/**
 * Created by ljw on 2017-08-23 023.
 */

public class ComicFetcher {

    private static RuleStore getRuleStore(){
        return RuleStore.get();
    }

    private static RuleFetcher getRuleFetcher(){
        return RuleFetcher.get();
    }

    //首页解析
    public static CallBackData getHome(String html){
        Map<String,String> map = getRuleStore().getHomeRule();
        List<Section> sections = new ArrayList<>();
        //防止因为真实网页分页显示而导致同名标题产生多个板块
        Map<String,List<Comic>> parseMap = new HashMap<>();
        //板块顺序
        List<String> order = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        if(map!=null && map.size()>0) {
            JSONArray titlesJ = JSON.parseArray(map.get("titles"));
            JSONArray titleJ = JSON.parseArray(map.get("title"));
            JSONArray itemsListJ = JSON.parseArray(map.get("items-list"));
            JSONArray itemsJ = JSON.parseArray(map.get("items"));
            if(titlesJ!=null && titleJ!=null && itemsListJ !=null && itemsJ != null){
//                int len = titlesJ.size();
                int len = titlesJ.size() > itemsListJ.size() ? itemsListJ.size() : titlesJ.size();
                for(int i = 0 ;i < len ; i++){
                    Elements itemsList = (Elements) getRuleFetcher().parser(doc , (String) itemsListJ.get(i));
                    Elements titleList = (Elements) getRuleFetcher().parser(doc, (String) titlesJ.get(i));
                    for(int j = 0 ; j< itemsList.size() ;j++){
                        List<Comic> comics = new ArrayList<>();
                        Elements items = (Elements) getRuleFetcher().parser(itemsList.get(j), (String) itemsJ.get(i));
                        for(Element item : items) {
                            Comic comic = new Comic();
                            comic.setComicId((String) getRuleFetcher().parser(item,map.get("comic-id")));
                            comic.setName((String) getRuleFetcher().parser(item,map.get("comic-name")));
                            comic.setImageUrl((String) getRuleFetcher().parser(item,map.get("comic-image-url")));
                            comic.setUpdateStatus((String) getRuleFetcher().parser(item,map.get("comic-update-status")));
                            comic.setScore((String) getRuleFetcher().parser(item,map.get("comic-score")));
                            comics.add(comic);
                        }

                        String title = "";
                        if(titleList.size()==0){
                            title = "其他漫画";
                        }else if(j>=titleList.size()){
                            //如果没有标题则使用最后一个标题
                            title = (String) getRuleFetcher().parser(titleList.last(), (String) titleJ.get(i));
                        }else{
                            title = (String) getRuleFetcher().parser(titleList.get(j), (String) titleJ.get(i));
                            order.add(title);
                        }


                        if(parseMap.containsKey(title)){
                            //如果包含该标题则追加
                            parseMap.get(title).addAll(comics);
                        }else{
                            //如果不包含则新建并添加
                            List<Comic> cs = new ArrayList<>();
                            cs.addAll(comics);
                            parseMap.put(title,cs);
                        }

                    }
                }
                //从分析完的map中获取数据
                for (String title : order){
                    Section section = new Section();
                    section.setTitle(title);
                    section.setComics(parseMap.get(title));
                    sections.add(section);
                }
            }else{
                throw new RuntimeException("首页解析错误");
            }

        }
        CallBackData backData = new CallBackData();
        backData.setObj(sections);
        return backData;
    }

    //获取列表页信息(规则版)
    public static CallBackData getComicList(String html){
        Map<String,String> map = getRuleStore().getListRule();

        List<Comic> comics = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        if(map!=null && map.size()>0){
            Elements items = (Elements) getRuleFetcher().parser(doc , map.get("items"));
            for(Element element : items){
                Comic comic = new Comic();
                comic.setComicId((String) getRuleFetcher().parser(element,map.get("comic-id")));
                comic.setName((String) getRuleFetcher().parser(element,map.get("comic-name")));
                comic.setImageUrl((String) getRuleFetcher().parser(element,map.get("comic-image-url")));
                comic.setScore((String) getRuleFetcher().parser(element,map.get("comic-score")));
                comic.setUpdate((String) getRuleFetcher().parser(element,map.get("comic-update")));
                comic.setUpdateStatus((String) getRuleFetcher().parser(element,map.get("comic-update-status")));
                comic.setEnd((Boolean) getRuleFetcher().parser(element,map.get("comic-end")));
                comics.add(comic);
            }
        }
        CallBackData backData = new CallBackData();
        backData.setObj(comics);
        int maxPage = 99999;
        try {
            maxPage = Integer.valueOf((String) getRuleFetcher().parser(doc,map.get("max-page")));
        }catch (Exception e){
            if(e instanceof NumberFormatException){
                maxPage = 1;
            }
        }
        backData.setArg1(maxPage);
        return backData;
    }

    //漫画搜索(规则版)
    public static CallBackData getSearchResults(String html){
        Map<String,String> map = getRuleStore().getSearchRule();

        List<Comic> comics = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        if(map!=null && map.size()>0){
            Elements items = (Elements) getRuleFetcher().parser(doc , map.get("items"));
            for(Element element : items){
                Comic comic = new Comic();
                comic.setComicId((String) getRuleFetcher().parser(element,map.get("comic-id")));
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
            if(e instanceof NumberFormatException){
                maxPage = 1;
            }
        }
        backData.setArg1(maxPage);
        return backData;
    }

    //作者相关漫画(规则版)
    public static CallBackData getAuthorResults(String html){
        Map<String,String> map = getRuleStore().getSearchRule();

        List<Comic> comics = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        if(map!=null && map.size()>0){
            Elements items = (Elements) getRuleFetcher().parser(doc , map.get("items"));
            for(Element element : items){
                Comic comic = new Comic();
                comic.setComicId((String) getRuleFetcher().parser(element,map.get("comic-id")));
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
            if(e instanceof NumberFormatException){
                maxPage = 1;
            }
        }
        backData.setArg1(maxPage);
        return backData;
    }

    //获取详细页信息(规则版)
    public static Comic getComicDetails(String html,Comic comic){
        Map<String,String> map = getRuleStore().getDetailsRule();

        Document doc = Jsoup.parse(html);
        comic.setName((String) getRuleFetcher().parser(doc,map.get("comic-name")));
        comic.setImageUrl((String) getRuleFetcher().parser(doc,map.get("comic-image-url")));
        comic.setTag((String) getRuleFetcher().parser(doc,map.get("comic-tag")));
        comic.setAuthor((String) getRuleFetcher().parser(doc,map.get("comic-author")));

        String authorStr = (String) getRuleFetcher().parser(doc,map.get("comic-author"));
        String[] authors = authorStr.split(" ");
        if(map.get("comic-author-href")!=null) {
            String hrefStr = (String) getRuleFetcher().parser(doc, map.get("comic-author-href"));
            String[] hrefs = hrefStr.split(" ");

            List<Author> authorList = new ArrayList<>();
            for(int i = 0 ; i < authors.length ; i++){
                Author author = new Author();
                try {
                    author.setName(authors[i]);
                    author.setMark(hrefs[i]);
                } catch (Exception e) {
                    Log.e("----", "getComicDetails: "+"作者相关数组越界");
                }
                authorList.add(author);
            }
            comic.setAuthors(authorList);
        }
        comic.setUpdateStatus("更新至"+(String) getRuleFetcher().parser(doc,map.get("comic-update-status")));
        comic.setUpdate((String) getRuleFetcher().parser(doc,map.get("comic-update")));
        comic.setInfo((String) getRuleFetcher().parser(doc,map.get("comic-info")));
        try {
            comic.setEnd((Boolean) getRuleFetcher().parser(doc,map.get("comic-end")));
            comic.setBan((Boolean) getRuleFetcher().parser(doc,map.get("comic-is-ban")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comic;
    }


    //获得漫画章节
    public static Comic getComicChapters(String html,Comic comic) {
        Map<String,String> map = getRuleStore().getDetailsChapterRule();

        List<Chapter> chapters = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements chapterType = null;
        if(map.get("chapter-type") != null) {
            chapterType = (Elements) getRuleFetcher().parser(doc, map.get("chapter-type"));
        }
        Elements chapterLists = (Elements) getRuleFetcher().parser(doc,map.get("chapter-lists"));
        for(int i = 0 ; i < chapterLists.size() ; i++){
            int type = 2;
            if(chapterType!=null) {
                if (chapterType.get(i).text().contains("单行本")) {
                    type = 0;
                } else if (chapterType.get(i).text().contains("单话")) {
                    type = 1;
                }
            }
            Elements chapterList = (Elements) getRuleFetcher().parser(chapterLists.get(i),map.get("chapter-list"));
            for(int j = 0 ; j < chapterList.size() ; j++){
                Elements chapterItems = (Elements) getRuleFetcher().parser(chapterList.get(j),map.get("chapter-items"));
                for (int k = chapterItems.size()-1 ; k >= 0 ; k--){
                    Chapter chapter = new Chapter();
                    chapter.setComicId(comic.getComicId());
                    chapter.setChapterId((String) getRuleFetcher().parser(chapterItems.get(k),map.get("chapter-id")));
                    chapter.setChapterName((String) getRuleFetcher().parser(chapterItems.get(k),map.get("chapter-name")));
                    chapter.setType(type);
                    chapters.add(chapter);
                }
            }
        }
//        for (Chapter chapter : chapters) {
//            Log.d("----====", "getComicChapters: "+chapter.toString());
//        }
        comic.setChapters(chapters);
        return comic;
    }

    //获得漫画章节
    public static List<Chapter> getComicChapterList(String html,Comic comic) {
        Map<String,String> map = getRuleStore().getDetailsChapterRule();
        if(map==null) return null;
        List<Chapter> chapters = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements chapterType = null;
        if(map.get("chapter-type") != null) {
            chapterType = (Elements) getRuleFetcher().parser(doc, map.get("chapter-type"));
        }
        Elements chapterLists = (Elements) getRuleFetcher().parser(doc,map.get("chapter-lists"));
        for(int i = 0 ; i < chapterLists.size() ; i++){
            int type = 2;
            if(chapterType!=null) {
                if (chapterType.get(i).text().contains("单行本")) {
                    type = 0;
                } else if (chapterType.get(i).text().contains("单话")) {
                    type = 1;
                }
            }
            Elements chapterList = (Elements) getRuleFetcher().parser(chapterLists.get(i),map.get("chapter-list"));
            for(int j = 0 ; j < chapterList.size() ; j++){
                Elements chapterItems = (Elements) getRuleFetcher().parser(chapterList.get(j),map.get("chapter-items"));
                for (int k = chapterItems.size()-1 ; k >= 0 ; k--){
                    Chapter chapter = new Chapter();
                    chapter.setComicId(comic.getComicId());
                    chapter.setChapterId((String) getRuleFetcher().parser(chapterItems.get(k),map.get("chapter-id")));
                    chapter.setChapterName((String) getRuleFetcher().parser(chapterItems.get(k),map.get("chapter-name")));
                    chapter.setType(type);
                    chapters.add(chapter);
                }
            }
        }
//        for (Chapter chapter : chapters) {
//            Log.d("----====", "getComicChapters: "+chapter.toString());
//        }
        comic.setChapters(chapters);
        return chapters;
    }


    //获取最新（规则版）
    public static List<Comic> getLatestList(String html){
        Map<String,String> map = getRuleStore().getUpdateRule();
        if (map==null) return null;
        List<Comic> comics = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements latestList = (Elements) getRuleFetcher().parser(doc,map.get("latest-list"));
        for(Element ele : latestList){
            Elements latestItems = (Elements) getRuleFetcher().parser(ele,map.get("latest-items"));
            for(Element item : latestItems){
                Comic comic = new Comic();
                comic.setComicId((String) getRuleFetcher().parser(item,map.get("comic-id")));
                comic.setName((String) getRuleFetcher().parser(item,map.get("comic-name")));
                comic.setImageUrl((String) getRuleFetcher().parser(item,map.get("comic-image-url")));
//                comic.setScore((String) getRuleFetcher().parser(item,map.get("comic-score")));
                comic.setUpdate((String) getRuleFetcher().parser(item,map.get("comic-update")));
                comic.setUpdateStatus((String) getRuleFetcher().parser(item,map.get("comic-update-status")));
                comic.setEnd((Boolean) getRuleFetcher().parser(item,map.get("comic-end")));
                comics.add(comic);
            }
        }
        return comics;
    }

}
