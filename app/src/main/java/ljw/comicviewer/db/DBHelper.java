package ljw.comicviewer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ljw on 2017-11-11 011.
 */

public class DBHelper {
    private String TAG = this.getClass().getSimpleName()+"----";
    private final static String dbName = "comic_viewer.db";
    private SQLiteHelper mSQLiteHelper = null;

    public synchronized void open(Context context){
        close();
        mSQLiteHelper = new SQLiteHelper(context, dbName, null, 1);
    }

    public synchronized void execSQL(String sql) {
        if (mSQLiteHelper == null) {
            return;
        }
        mSQLiteHelper.getWritableDatabase().execSQL(sql);
    }

    public synchronized long insert(String table, ContentValues values) {
        if (mSQLiteHelper == null) {
            return -1;
        }
        return mSQLiteHelper.getWritableDatabase().insertOrThrow(table, null, values);
    }

    public synchronized int update(String table, ContentValues values,
                                   String whereClause, String... whereArgs) {
        if (mSQLiteHelper == null) {
            return -1;
        }
        return mSQLiteHelper.getWritableDatabase().update(table, values,
                whereClause, whereArgs);
    }

    public synchronized Cursor query(String table, String[] columns,
                                     String selection, String[] selectionArgs, String groupBy,
                                     String having, String orderBy) {
        if (mSQLiteHelper == null) {
            return null;
        }
        return mSQLiteHelper.getReadableDatabase().query(table, columns,
                selection, selectionArgs, groupBy, having, orderBy);
    }

    public synchronized Cursor query(String sql) {
        if (mSQLiteHelper == null) {
            return null;
        }
        return mSQLiteHelper.getReadableDatabase().rawQuery(sql, null);
    }

    public synchronized Cursor query(String sql, String... args) {
        if (mSQLiteHelper == null) {
            return null;
        }
        return mSQLiteHelper.getReadableDatabase().rawQuery(sql, args);
    }

    public synchronized void nonQuery(String sql) {
        if (mSQLiteHelper == null) {
            return;
        }
        mSQLiteHelper.getWritableDatabase().execSQL(sql);
    }

    public synchronized void nonQuery(String sql, String... args) {
        if (mSQLiteHelper == null) {
            return;
        }
        mSQLiteHelper.getWritableDatabase().execSQL(sql, args);
    }

    public synchronized int delete(String table, String whereClause,
                                   String[] whereArgs) {
        if (mSQLiteHelper == null) {
            return -1;
        }
        return mSQLiteHelper.getWritableDatabase().delete(table, whereClause,
                whereArgs);
    }



    public synchronized void close() {
        if (mSQLiteHelper != null) {
            mSQLiteHelper.close();
            mSQLiteHelper = null;
        }
    }

    public class SQLiteHelper extends SQLiteOpenHelper{

        public SQLiteHelper(Context context, String name
                , SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //字段名不能为update，否则会导致插入失败的
            db.execSQL("CREATE TABLE `collection`(`id` integer primary key autoincrement," +
                    "`comicId`,`name`,`imageUrl`,`score`,`updateDate`,`updateStatus`,`isEnd`,`tag`,`comeFrom`)");
            db.execSQL("CREATE TABLE `history`(`id` integer primary key autoincrement," +
                    "`comicId`,`name`,`imageUrl`,`isEnd`,`chapterId`,`chapterName`,`readTime`,`page`)");
            db.execSQL("CREATE TABLE `rule`(`id` integer primary key autoincrement," +
                    "`name`,`rule` text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade: oldVersion=" + oldVersion + " newVersion=" + newVersion);
        }
    }
}
