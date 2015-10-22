package com.example.jianming.Tasks;

import android.os.AsyncTask;

import com.example.jianming.Utils.FileUtil;
import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.myapplication.PicAlbumListActivity;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DownloadPicTask extends AsyncTask<String, Void, byte[]> {
    private static final String TAG = "DownloadPicTask";

    File file;

    DownloadPicListTask parentTask;

    public DownloadPicTask(File file, DownloadPicListTask parentTask) {
//        this.dirName = dirName;
//        this.picName = picName;
        this.file = file;
        this.parentTask = parentTask;
    }

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

    @Override
    protected void onPostExecute(byte[] bytes) {


//        File directory = FileUtil.getAlbumStorageDir(context, dirName);
//        File file = new File(directory, picName);
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            parentTask.notifyDownloadingProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
