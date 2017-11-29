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
    private final static String tableName = "collection";
    private DBHelper dbHelper;

    public CollectionHolder(Context context) {
        dbHelper = new DBHelper();
        dbHelper.open(context);
    }

    public synchronized long addCollection(Comic comic){
        if (comic == null) return -1;
        if (hasComic(comic.getComicId())) return -1;
        ContentValues contentValues = new ContentValues();
        contentValues.put("comicId",comic.getComicId());
        contentValues.put("name",comic.getName());
        contentValues.put("imageUrl",comic.getImageUrl());
        contentValues.put("score",comic.getScore());
        contentValues.put("updateDate",comic.getUpdate());
        contentValues.put("updateStatus",comic.getUpdateStatus());
        contentValues.put("isEnd",comic.isEnd()?1:0);
        contentValues.put("comeFrom",comic.getComeFrom());
        contentValues.put("tag",comic.getTag());
        try {
            long res = dbHelper.insert(tableName,contentValues);
            Log.d("----", "addCollection: "+(res==-1?"插入失败":"插入成功"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized List<Comic> getComics(){
        List<Comic> comics = new ArrayList<>();

        Cursor cursor = dbHelper.query("select * from "+tableName);
        Log.d("----", "getComics: "+cursor.getCount());
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
        Log.d("----", "addCollection: 成功删除"+res+"条记录");
    }
}
