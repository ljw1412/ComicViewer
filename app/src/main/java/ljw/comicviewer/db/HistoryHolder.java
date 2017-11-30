package ljw.comicviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.bean.Chapter;
import ljw.comicviewer.bean.Comic;
import ljw.comicviewer.bean.History;

/**
 * Created by ljw on 2017-11-29 029.
 */

public class HistoryHolder {
    private final static String tableName = "history";
    private DBHelper dbHelper;

    public HistoryHolder(Context context) {
        dbHelper = new DBHelper();
        dbHelper.open(context);
    }

    public synchronized long updateOrAddHistory(History history){
        if (history == null) return -1;
        ContentValues cv = new ContentValues();
        cv.put("comicId",history.getComicId());
        cv.put("chapterId",history.getChapterId());
        cv.put("chapterName",history.getChapterName());
        cv.put("name",history.getComicName());
        cv.put("imageUrl",history.getImgUrl());
        cv.put("isEnd",history.isEnd()?1:0);
        cv.put("page",history.getPage());
        cv.put("readTime",history.getReadTime());
        if (hasData(history.getComicId())){
            long res = dbHelper.update(tableName,cv,"comicId = ?",new String[]{history.getComicId()});
            Log.d("----", "updateOrAddHistory: "+(res== -1 ? "更新失败" : "更新成功"));
            return res;
        }else{
            return addHistory(cv);
        }
    }

    public synchronized long addHistory(ContentValues cv){
        if (cv == null) return -1;
        try {
            long res = dbHelper.insert(tableName,cv);
            Log.d("----", "addHistory: "+(res==-1?"插入失败":"插入成功"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized long addHistory(History history){
        if (history == null) return -1;
        ContentValues cv = new ContentValues();
        cv.put("comicId",history.getComicId());
        cv.put("chapterId",history.getChapterId());
        cv.put("chapterName",history.getChapterName());
        cv.put("name",history.getComicName());
        cv.put("imageUrl",history.getImgUrl());
        cv.put("isEnd",history.isEnd()?1:0);
        cv.put("page",history.getPage());
        cv.put("readTime",history.getReadTime());
        try {
            long res = dbHelper.insert(tableName,cv);
            Log.d("----", "addHistory: "+(res==-1?"插入失败":"插入成功"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized List<History> getHistories(){
        List<History> list = new ArrayList<>();
        Cursor cursor = dbHelper.query("select * from "+tableName);
        Log.d("----", "getHistories: "+cursor.getCount());
        while (cursor.moveToNext()){
            History history = new History();
            history.setComicId(cursor.getString(cursor.getColumnIndex("comicId")));
            history.setChapterId(cursor.getString(cursor.getColumnIndex("chapterId")));
            history.setChapterName(cursor.getString(cursor.getColumnIndex("chapterName")));
            history.setComicName(cursor.getString(cursor.getColumnIndex("name")));
            history.setImgUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
            history.setEnd(cursor.getInt(cursor.getColumnIndex("isEnd"))==0?false:true);
            history.setReadTime(cursor.getLong(cursor.getColumnIndex("readTime")));
            history.setPage(cursor.getInt(cursor.getColumnIndex("page")));
            list.add(history);
        }
        return list;
    }

    public synchronized long delHistory(List<String> comicIdList){
        int delRows = 0;
        for (String comicId : comicIdList) {
            delRows += dbHelper.delete(tableName,"comicId = ?",new String[]{comicId});
        }
        return delRows;
    }

    public synchronized boolean hasData(String comicId){
        String findQuery = "select * from " + tableName + " where comicId = ?";
        Cursor cursor = dbHelper.query(findQuery,new String[]{comicId});
        long size = cursor.getCount();
        Log.d("----", "hasData: "+size);
        return dbHelper.query(findQuery,new String[]{comicId}).getCount()>0;
    }

    public synchronized History getHistory(String comicId){
        History history = new History();
        String findQuery = "select * from " + tableName + " where comicId = ?";
        Cursor cursor = dbHelper.query(findQuery,new String[]{comicId});
        if(cursor.getCount()>0){
            cursor.moveToNext();
            history.setComicId(cursor.getString(cursor.getColumnIndex("comicId")));
            history.setChapterId(cursor.getString(cursor.getColumnIndex("chapterId")));
            history.setChapterName(cursor.getString(cursor.getColumnIndex("chapterName")));
            history.setComicName(cursor.getString(cursor.getColumnIndex("name")));
            history.setImgUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
            history.setEnd(cursor.getInt(cursor.getColumnIndex("isEnd"))==0?false:true);
            history.setReadTime(cursor.getLong(cursor.getColumnIndex("readTime")));
            history.setPage(cursor.getInt(cursor.getColumnIndex("page")));
            return history;
        }
        return null;
    }
}
