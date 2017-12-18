package ljw.comicviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

import ljw.comicviewer.bean.Section;

/**
 * Created by ljw on 2017-12-18 018.
 */

public class SectionDBHolder {
    private String TAG = this.getClass().getSimpleName()+"----";
    private final static String tableName = "section";
    private DBHelper dbHelper;

    public SectionDBHolder(Context context){
        dbHelper = new DBHelper();
        dbHelper.open(context);
    }

    public synchronized boolean hasSection(String comeFrom){
        String findQuery = "select * from " + tableName + " where comeFrom = '"+comeFrom+"'";
        return dbHelper.query(findQuery).getCount()>0;
    }

    public synchronized long addOrUpdateSection(String comeFrom, List<Section> sections){
        if(comeFrom==null || sections ==null || sections.size()==0) return -1;
        long res = -1;
        ContentValues cv = new ContentValues();
        cv.put("comeFrom",comeFrom);
        String json = JSON.toJSONString(sections);
        cv.put("list",json);
        if(hasSection(comeFrom)){
            res = dbHelper.update(tableName,cv,"comeFrom = ?",comeFrom);
        }else{
            res = addSection(cv);
        }
        Log.d(TAG, "addSection: "+(res==-1?"更新插入失败":"更新插入成功"));
        return res;
    }

    public synchronized long addSection(ContentValues cv){
        long res = dbHelper.insert(tableName,cv);
        return res;
    }


    public synchronized long addSection(String comeFrom, List<Section> sections){
        if(comeFrom==null || sections ==null || sections.size()==0) return -1;
        if(hasSection(comeFrom)) return -2;
        ContentValues cv = new ContentValues();
        cv.put("comeFrom",comeFrom);
        String json = JSON.toJSONString(sections);
        cv.put("list",json);
        long res = dbHelper.insert(tableName,cv);
        Log.d(TAG, "addSection: "+(res==-1?"插入失败":"插入成功"));
        return res;
    }

    public synchronized List<Section> getSectionsByHost(String comeFrom){
        if(comeFrom == null) return null;
        Cursor cursor = dbHelper.query("select * from "+tableName+" where comeFrom = '"+comeFrom+"'");
        if(cursor.moveToNext()){
            String json = cursor.getString(cursor.getColumnIndex("list"));
            try {
                List<Section> sections = JSON.parseArray(json,Section.class);
                return sections;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
