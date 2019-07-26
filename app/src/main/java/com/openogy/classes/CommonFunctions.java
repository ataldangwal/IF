package com.openogy.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

public final class CommonFunctions extends AppCompatActivity {

    public static String API_URL = "http://184.95.53.181/plesk-site-preview/api.ifadvertisings.com/api/";
  // public static String API_URL = "http://api.ifadvertisings.com/api/";
   //public static String API_URL = "http://192.168.225.60/IFAPI/api/";

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            isConnected = true;

        } else {
            isConnected = false;
        }

        return isConnected;
    }
}
