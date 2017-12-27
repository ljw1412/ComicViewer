package ljw.comicviewer.store;

/**
 * Created by ljw on 2017-12-27 027.
 */

public class AppStatusStore {
    private final String TAG = this.getClass().getSimpleName()+"----";
    private static AppStatusStore appStatusStore;
    private String currentSource;
    private boolean sourceReplace = false;//是否更换源

    private AppStatusStore() {
    }
    public static AppStatusStore get(){
        if (appStatusStore == null){
            appStatusStore = new AppStatusStore();
        }
        return appStatusStore;
    }

    public String getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(String currentSource) {
        this.currentSource = currentSource;
    }

    public boolean isSourceReplace() {
        return sourceReplace;
    }

    public void setSourceReplace(boolean sourceReplace) {
        this.sourceReplace = sourceReplace;
    }
}
