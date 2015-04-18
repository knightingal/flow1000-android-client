package com.example.jianming.Tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jianming on 2015/4/9.
 */
public class DownloadPicTask extends AsyncTask<String, Void, byte[]> {

    @Override
    protected byte[] doInBackground(String... urls) {
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }



    private byte[] downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        int len = 500;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d("network", "The response is: " + response);
            is = conn.getInputStream();
            return readIt(is, len);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private byte[] readIt(InputStream is, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(is, "UTF-8");
        char[] buffer = new char[len];
        String content = "";
        int readLen;
        do {
            readLen = reader.read(buffer);

            content += new String(buffer).substring(0, readLen);
        } while (readLen == len);
        byte[] bs = content.getBytes();
        return bs;
    }
}
