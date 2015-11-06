package com.example.jianming.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;

import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
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
            ActiveAndroid.beginTransaction();
            JSONObject jsonObject = new JSONObject(s);
            String dirName = jsonObject.getString("dirName");
            JSONArray pics = jsonObject.getJSONArray("pics");
            picCountAll = pics.length();
            downloadProcessView.setStepCount(picCountAll);

            for (int i = 0; i < pics.length(); i++) {
                String imgUrl = generateImgUrl(dirName, pics.getString(i));
                File file = downloadImg(imgUrl, dirName, pics.getString(i));
                PicAlbumBean picAlbumBean = PicAlbumBean.getByIndex(index);
                PicInfoBean picInfoBean = new PicInfoBean();
                picInfoBean.setIndex(i);
                picInfoBean.setName(pics.getString(i));
                picInfoBean.setAlbumInfo(picAlbumBean);
                picInfoBean.setAbsolutePath(file.getAbsolutePath());
                picInfoBean.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private String generateImgUrl(String dirName, String imgName) {
        return ("http://%serverIP:%serverPort/static/%dirName/" + imgName)
                .replace("%serverIP", EnvArgs.serverIP)
                .replace("%serverPort", EnvArgs.serverPort)
                .replace("%dirName", dirName);
    }

    private File downloadImg(String imgUrl, final String dirName, final String picName) {
        Log.d("DownloadPicListTask", "create task for " + imgUrl);
        File directory = FileUtil.getAlbumStorageDir(context, dirName);
        File file = new File(directory, picName);
        new DownloadPicTask(file, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgUrl);
        return file;
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
