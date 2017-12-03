package ljw.comicviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ljw on 2017-12-03 003.
 */

public class RuleHolder {
    private String TAG = this.getClass().getSimpleName()+"----";
    private final static String tableName = "rule";
    private DBHelper dbHelper;

    public RuleHolder(Context context) {
        dbHelper = new DBHelper();
        dbHelper.open(context);
    }

    private synchronized boolean hasRule(String name){
        String findQuery = "select * from " + tableName + " where name = "+name;
        return dbHelper.query(findQuery).getCount()>0;
    }

    private synchronized long addRule(String name,String rule){
        if(name==null || rule ==null) return -1;
        if(hasRule(name)) return -2;
        ContentValues cv = new ContentValues();
        cv.put("name",name);
        cv.put("rule",rule);
        long res = dbHelper.insert(tableName,cv);
        Log.d(TAG, "addRule: "+(res==-1?"插入失败":"插入成功"));
        return res;
    }

    private synchronized long updateRule(String name,String rule){
        if(name==null || rule ==null) return -1;
        ContentValues cv = new ContentValues();
        cv.put("rule",rule);
        long res = dbHelper.update(tableName,cv,"name = ?",new String[]{name});
        Log.d(TAG, "updateOrAddHistory: "+(res== -1 ? "更新失败" : "更新成功"));
        return res;
    }

    private synchronized long delRule(String name){
        return dbHelper.delete(tableName,"name = ?",new String[]{name});
    }

    private synchronized Map<String, String> getRuleByName(String name){
        Map<String, String> map = new HashMap<>();
        Cursor cursor = dbHelper.query("select * from " + tableName + " where name = " + name);
        if(cursor.getCount()>0) {
            cursor.moveToNext();
            String rName = cursor.getString(cursor.getColumnIndex("name"));
            String rule = cursor.getString(cursor.getColumnIndex("rule"));
            if (rName != null && rule != null) {
                map.put("name", rName);
                map.put("rule", rule);
                return map;
            }
        }
        return null;
    }

    private synchronized Map<String, String> getRuleById(String id){
        Map<String, String> map = new HashMap<>();
        Cursor cursor = dbHelper.query("select * from " + tableName + " where id = " + id);
        if(cursor.getCount()>0) {
            cursor.moveToNext();
            String rName = cursor.getString(cursor.getColumnIndex("name"));
            String rule = cursor.getString(cursor.getColumnIndex("rule"));
            if (rName != null && rule != null) {
                map.put("name", rName);
                map.put("rule", rule);
                return map;
            }
        }
        return null;
    }
}
