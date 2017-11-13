package ljw.comicviewer.db;

import android.content.ContentValues;
import android.content.Context;

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

    public synchronized void addCollection(Comic comic){
        if (comic == null) return;
        ContentValues contentValues = new ContentValues();
    }

}
