package ljw.comicviewer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ljw on 2018-01-30 030.
 */

public class NetworkUtil {
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 2;

    public static int getNetworkType(Context context){
        ConnectivityManager mConnectivity = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info != null && info.isConnected()){
            int netType = info.getType();
            if (netType == ConnectivityManager.TYPE_WIFI ) {  //WIFI
                return NETWORK_WIFI;
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {   //MOBILE
                return NETWORK_MOBILE;
            }
        }
        return NETWORK_NONE;
    }


}
