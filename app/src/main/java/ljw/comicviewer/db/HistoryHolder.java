package ljw.comicviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.bean.History;

/**
 * Created by ljw on 2017-11-29 029.
 */

public class HistoryHolder {
    private String TAG = this.getClass().getSimpleName()+"----";
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
        cv.put("comeFrom",history.getComeFrom());
        Log.d(TAG, "updateOrAddHistory: "+history.toString());
        if (hasData(history.getComicId())){
            long res = dbHelper.update(tableName,cv,"comicId = ?",new String[]{history.getComicId()});
            Log.d(TAG, "updateOrAddHistory: "+(res== -1 ? "更新失败" : "更新成功"));
            return res;
        }else{
            return addHistory(cv);
        }
    }

    public synchronized long addHistory(ContentValues cv){
        if (cv == null) return -1;
        try {
            long res = dbHelper.insert(tableName,cv);
            Log.d(TAG, "addHistory: "+(res==-1?"插入失败":"插入成功"));
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
        cv.put("comeFrom",history.getComeFrom());
        try {
            long res = dbHelper.insert(tableName,cv);
            Log.d(TAG, "addHistory: "+(res==-1?"插入失败":"插入成功"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized List<History> getHistories(){
        List<History> list = new ArrayList<>();
        Cursor cursor = dbHelper.query("select * from "+tableName);
        Log.d(TAG, "getHistories: Histories数量"+cursor.getCount());
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
            history.setComeFrom(cursor.getString(cursor.getColumnIndex("comeFrom")));
            list.add(history);
        }
        return list;
    }

    public synchronized List<History> getHistories(int limit,String comeFrom){
        List<History> list = new ArrayList<>();
        String sql = "select * from "+tableName+" where comeFrom = \""+comeFrom+"\" order by readTime desc limit "+limit;
        Log.d(TAG, "getHistories: "+sql);
        Cursor cursor = dbHelper.query(sql);
        Log.d(TAG, "getHistories: Histories数量"+cursor.getCount());
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
            history.setComeFrom(cursor.getString(cursor.getColumnIndex("comeFrom")));
            list.add(history);
        }
        return list;
    }
    public synchronized long delOneHistory(String comicId){
        int delRows =dbHelper.delete(tableName,"comicId = ?",new String[]{comicId});
        Log.d(TAG, "addHistory: 删除条目："+delRows);
        return delRows;
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
        Log.d(TAG, "hasData: "+size);
        return size>0;
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
            history.setComeFrom(cursor.getString(cursor.getColumnIndex("comeFrom")));
            return history;
        }
        return null;
    }
}
