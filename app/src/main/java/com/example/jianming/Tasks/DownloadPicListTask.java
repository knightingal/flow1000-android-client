package com.example.jianming.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;

import com.example.jianming.myapplication.PicAlbumListActivity;
import com.example.jianming.views.DownloadProcessView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jianming on 2015/5/4.
 */
public class DownloadPicListTask extends DownloadWebpageTask{
    private static final String TAG = "DownloadPicListTask";
    private DownloadProcessView downloadProcessView;
    private String dirName;
    private int index;
    private Context context;

    public DownloadPicListTask(Context context, int index, String dirName, DownloadProcessView downloadProcessView) {
        this.context = context;
        this.index = index;
        this.dirName = dirName;
        this.downloadProcessView = downloadProcessView;
    }

    int picCountAll = 0;
    int currPicCount = 0;
    @Override
    protected void onPostExecute(String s) {
        Log.i(TAG, s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            String dirName = jsonObject.getString("dirName");
            JSONArray pics = jsonObject.getJSONArray("pics");
            picCountAll = pics.length();
            downloadProcessView.setStepCount(picCountAll);
            for (int i = 0; i < pics.length(); i++) {
                String imgUrl = generateImgUrl(dirName, pics.getString(i));
                downloadImg(imgUrl, dirName, pics.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String generateImgUrl(String dirName, String imgName) {
        return ("http://%serverIP:%serverPort/static/%dirName/" + imgName)
                .replace("%serverIP", EnvArgs.serverIP)
                .replace("%serverPort", EnvArgs.serverPort)
                .replace("%dirName", dirName);
    }

    private void downloadImg(String imgUrl, final String dirName, final String picName) {
        Log.d("DownloadPicListTask", "create task for " + imgUrl);
        File directory = FileUtil.getAlbumStorageDir(context, dirName);
        File file = new File(directory, picName);
        new DownloadPicTask(file, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgUrl);
    }

    public void notifyDownloadingProcess() {
        currPicCount++;
        downloadProcessView.longer();
        if (currPicCount == picCountAll) {
            downloadProcessView.clear();
            ((PicAlbumListActivity) context).doPicListDownloadComplete(dirName, index);
        }
    }



}
