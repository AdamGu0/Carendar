package info.leiguo.healthmonitoring;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Lei on 3/6/2017.
 */

public class Utils {
    public static boolean checkNetwork(Context context){
        if(context == null){
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo info =  connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isConnected()){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
