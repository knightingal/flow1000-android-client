package com.example.jianming.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Jianming on 2015/9/16.
 */
public class NetworkUtil {

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    private static OkHttpClient okHttpClient = new OkHttpClient();
}
