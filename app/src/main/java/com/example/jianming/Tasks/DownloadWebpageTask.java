package com.example.jianming.Tasks;

import android.os.AsyncTask;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(strUrl).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String readIt(InputStream is, int len) throws IOException {
        Reader reader = new InputStreamReader(is, "UTF-8");
        char[] buffer = new char[len];
        String content = "";
        int readLen;
        do {
            readLen = reader.read(buffer);
            if (readLen > 0) {
                content += new String(buffer).substring(0, readLen);
            }
        } while (readLen > 0);
        return content;
    }
}

