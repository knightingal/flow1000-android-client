package com.example.jianming.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Semaphore;


public class DownloadPicTask extends AsyncTask<String, Void, byte[]> {
    private static final String TAG = "DownloadPicTask";
    private static final Semaphore semp = new Semaphore(5);
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
            semp.acquire();
            Log.d("DownloadPicTask", "start download " + myurl);
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            semp.release();
            Log.d("DownloadPicTask", "end download " + myurl);
            //Log.d("network", "The response is: " + response);
            is = conn.getInputStream();
            return readIt(is, len);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private byte[] readIt(InputStream is, int len) throws IOException {
        byte[] buffer = new byte[len];
        ByteArrayOutputStream out = new ByteArrayOutputStream(len);
        int readLen;
        do {
            readLen = is.read(buffer);
            if (readLen > 0) {
                out.write(buffer, 0, readLen);
            }
        } while (readLen != -1);
        return out.toByteArray();

    }
}
