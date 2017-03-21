package info.leiguo.healthmonitoring;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Lei on 3/6/2017.
 */

public class Utils {
    /**
     *  Check whether the network is available or not.
     * @param context
     * @return
     */
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

    // This code is used to trust the https server for the assignment 2. I think it's ok to do things
    // in such a way for an assignment. I will not use the same code for a production application, because
    // it's not secure
    // The code is borrowed from StackOverflow
    public static void trustAllServer() throws NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }});
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager(){
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }}}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(
                context.getSocketFactory());
    }
}
