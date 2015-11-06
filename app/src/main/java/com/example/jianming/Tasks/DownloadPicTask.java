package com.example.jianming.Tasks;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.beans.PicInfoBean;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DownloadPicTask extends AsyncTask<String, Void, Integer> {
    private static final String TAG = "DownloadPicTask";

    File file;

    DownloadPicListTask parentTask;

    public PicInfoBean getPicInfoBean() {
        return picInfoBean;
    }

    PicInfoBean picInfoBean = new PicInfoBean();



    public DownloadPicTask(File file, DownloadPicListTask parentTask) {
        this.file = file;
        this.parentTask = parentTask;
    }

    @Override
    protected Integer doInBackground(String... urls) {
        try {
            downloadUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void downloadUrl(String strUrl) throws IOException {
        Request request = new Request.Builder().url(strUrl).build();
        byte[] bytes = NetworkUtil.getOkHttpClient().newCall(request).execute().body().bytes();
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int width = options.outWidth;
        int height = options.outHeight;
        picInfoBean.setWidth(width);
        picInfoBean.setHeight(height);
        picInfoBean.setAbsolutePath(file.getAbsolutePath());
    }

    @Override
    protected void onPostExecute(Integer param) {
        parentTask.notifyDownloadingProcess();
    }

}
