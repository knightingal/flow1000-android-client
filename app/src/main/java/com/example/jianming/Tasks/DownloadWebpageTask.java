package com.example.jianming.Tasks;

import android.os.AsyncTask;
import com.example.jianming.Utils.NetworkUtil;

import java.io.IOException;

import okhttp3.Request;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        
    }



    private String downloadUrl(String strUrl) throws IOException {
        Request request = new Request.Builder().url(strUrl).build();
        return  NetworkUtil.getOkHttpClient().newCall(request).execute().body().string();
    }


}

