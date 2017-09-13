package ljw.comicviewer.util;

import android.content.Context;

import java.io.File;

/**
 * Created by ljw on 2017-09-06 006.
 */

public class FileUtil {
    /**
     * 获得文件名
     * @param path 网络路径
     * @return
     */
    public static String cleanName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    public static void cleanGlideCache(Context context){
        File glideCacheDir = new File(context.getExternalCacheDir(),"GlideCache");
        if (glideCacheDir.exists()){
            for (File file : glideCacheDir.listFiles()){
                file.delete();
            }
        }
    }
}
