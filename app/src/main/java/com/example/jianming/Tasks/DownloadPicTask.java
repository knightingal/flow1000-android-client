package com.example.jianming.Tasks;

import android.os.AsyncTask;
import com.example.jianming.Utils.NetworkUtil;
import com.squareup.okhttp.Request;

import java.io.IOException;


public class DownloadPicTask extends AsyncTask<String, Void, byte[]> {
    private static final String TAG = "DownloadPicTask";

    @Override
    protected byte[] doInBackground(String... urls) {
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] downloadUrl(String strUrl) throws IOException {
        Request request = new Request.Builder().url(strUrl).build();
        return NetworkUtil.getOkHttpClient().newCall(request).execute().body().bytes();
    }

}
