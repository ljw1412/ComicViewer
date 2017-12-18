package ljw.comicviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.bean.Comic;

/**
 * Created by ljw on 2017-11-11 011.
 */

public class CollectionHolder {
    private String TAG = this.getClass().getSimpleName()+"----";
    private final static String tableName = "collection";
    private DBHelper dbHelper;

    public CollectionHolder(Context context) {
        dbHelper = new DBHelper();
        dbHelper.open(context);
    }

    public synchronized long addOrUpdateCollection(Comic comic){
        if (comic == null) return -1;
        ContentValues cv = new ContentValues();
        cv.put("comicId",comic.getComicId());
        cv.put("name",comic.getName());
        cv.put("imageUrl",comic.getImageUrl());
        cv.put("score",comic.getScore());
        cv.put("updateDate",comic.getUpdate());
        cv.put("updateStatus",comic.getUpdateStatus());
        cv.put("isEnd",comic.isEnd()?1:0);
        cv.put("comeFrom",comic.getComeFrom());
        cv.put("tag",comic.getTag());
        long res;
        if (hasComic(comic.getComicId())){
            res = dbHelper.update(tableName,cv,"comicId = ?",comic.getComicId());
        }else{
            res = addCollection(cv);
        }
        Log.d(TAG, "addSection: "+(res==-1?"更新插入失败":"更新插入成功"));
        return res;
    }

    public synchronized long addCollection(ContentValues cv) {
        long res = dbHelper.insert(tableName,cv);
        return res;
    }

    public synchronized long addCollection(Comic comic){
        if (comic == null) return -1;
        if (hasComic(comic.getComicId())) return -1;
        ContentValues cv = new ContentValues();
        cv.put("comicId",comic.getComicId());
        cv.put("name",comic.getName());
        cv.put("imageUrl",comic.getImageUrl());
        cv.put("score",comic.getScore());
        cv.put("updateDate",comic.getUpdate());
        cv.put("updateStatus",comic.getUpdateStatus());
        cv.put("isEnd",comic.isEnd()?1:0);
        cv.put("comeFrom",comic.getComeFrom());
        cv.put("tag",comic.getTag());
        try {
            long res = dbHelper.insert(tableName,cv);
            Log.d(TAG, "addCollection: "+(res==-1?"插入失败":"插入成功"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized List<Comic> getComics(){
        List<Comic> comics = new ArrayList<>();

        Cursor cursor = dbHelper.query("select * from "+tableName);
        Log.d(TAG, "getComics: "+cursor.getCount());
        while (cursor.moveToNext()){
            Comic comic = new Comic();
            comic.setComicId(cursor.getString(cursor.getColumnIndex("comicId")));
            comic.setName(cursor.getString(cursor.getColumnIndex("name")));
            comic.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
            comic.setScore(cursor.getString(cursor.getColumnIndex("score")));
            comic.setUpdate(cursor.getString(cursor.getColumnIndex("updateDate")));
            comic.setUpdateStatus(cursor.getString(cursor.getColumnIndex("updateStatus")));
            comic.setEnd(cursor.getInt(cursor.getColumnIndex("isEnd"))>0);
            comic.setComeFrom(cursor.getString(cursor.getColumnIndex("comeFrom")));
            comic.setTag(cursor.getString(cursor.getColumnIndex("tag")));
            comics.add(comic);
        }
        return comics;
    }

    public synchronized boolean hasComic(String comicId){
        String findQuery = "select * from " + tableName + " where comicId = ?";
        return dbHelper.query(findQuery,comicId).getCount()>0;
    }

    public synchronized void deleteComic(String comicId){
        long res = dbHelper.delete(tableName,"`comicId` = ?",new String[]{comicId});
        Log.d(TAG, "addCollection: 成功删除"+res+"条记录");
    }
}
