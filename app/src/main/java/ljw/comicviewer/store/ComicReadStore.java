package ljw.comicviewer.store;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ljw.comicviewer.bean.Chapter;

/**
 * Created by ljw on 2017-09-19 019.
 */

public class ComicReadStore {
    private final String TAG = this.getClass().getSimpleName()+"----";
    private static ComicReadStore comicStore;
    private List<Chapter> list = new ArrayList<>();
    private int currentIndex = -1;

    private ComicReadStore(){}

    public static ComicReadStore get(){
        if (comicStore == null){
            comicStore = new ComicReadStore();
        }
        return comicStore;
    }

    public List<Chapter> getObj() {
        return list;
    }

    public void setObj(List<Chapter> obj) {
        list.clear();
        list.addAll(obj);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void clearStore(){
        list.clear();
        currentIndex = -1;
    }

    public void printList(){
        String out = "{size:"+list.size()+",currentIndex:"+currentIndex+",\nitems:[\n";
        for (int i = 0; i < list.size() ; i++) {
            out += i+list.get(i).toString()+ ((i!= list.size()-1)?",\n":"\n");
        }
        Log.d(TAG, out+"]}");
    }

    public int getSize(){
        return list.size();
    }
}
