package com.example.jianming.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;

import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.services.DownloadService;
import com.example.jianming.views.DownloadProcessBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jianming on 2015/5/4.
 */
public class DownloadPicListTask extends DownloadWebpageTask{
    private static final String TAG = "DownloadPicListTask";
    private final int localPosition;
    private String dirName;
    private int index;
    private Context context;

    public static void executeDownloadAlbumInfo(Context context, int serverIndex, int localPosition, String dirName, DownloadProcessBar downloadProcessView, String url) {
        DownloadPicListTask task = new DownloadPicListTask(context, serverIndex, localPosition, dirName, downloadProcessView);
        DownloadProcessBar downloadProcessBar = ((DownloadService) context).getDownloadProcessBarByIndex(serverIndex, localPosition);
        if (downloadProcessBar != null) {
            downloadProcessBar.setVisibility(View.VISIBLE);
        }

        task.execute(url);

    }

    /**
     * Constructor of DownloadPicListTask
     *
     * @param context the context
     * @param index index from server
     * @param localPosition position of the local listView item
     * @param dirName the dirName
     * @param downloadProcessView the downloadProcessView
     */
    public DownloadPicListTask(Context context, int index, int localPosition, String dirName, DownloadProcessBar downloadProcessView) {
        this.context = context;
        this.index = index;
        this.dirName = dirName;
        this.localPosition = localPosition;
    }

    List<PicInfoBean> picInfoBeanList = new ArrayList<>();

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
            DownloadProcessBar downloadProcessBar = ((DownloadService) context).getDownloadProcessBarByIndex(index, localPosition);
            if (downloadProcessBar != null) {
                downloadProcessBar.setStepCount(picCountAll);
            }
            PicAlbumBean picAlbumBean = PicAlbumBean.getByServerIndex(index);
            for (int i = 0; i < pics.length(); i++) {
                String imgUrl = generateImgUrl(dirName, pics.getString(i));
                PicInfoBean picInfoBean = downloadImg(imgUrl, dirName, pics.getString(i));

                picInfoBean.setIndex(i);
                picInfoBean.setName(pics.getString(i));
                picInfoBean.setAlbumInfo(picAlbumBean);
                picInfoBeanList.add(picInfoBean);
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

    private PicInfoBean downloadImg(String imgUrl, final String dirName, final String picName) {
        Log.d("DownloadPicListTask", "create task for " + imgUrl);
        File directory = FileUtil.getAlbumStorageDir(context, dirName);
        File file = new File(directory, picName);
        DownloadPicTask task = new DownloadPicTask(file, this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgUrl);

        return task.getPicInfoBean();
    }

    public void notifyDownloadingProcess() {
        currPicCount++;
        DownloadProcessBar downloadProcessBar = ((DownloadService) context).getDownloadProcessBarByIndex(index, localPosition);
        if (downloadProcessBar != null) {
            downloadProcessBar.longer();
        }

        if (currPicCount == picCountAll) {
            ActiveAndroid.beginTransaction();
            try {
                for (PicInfoBean picInfoBean : picInfoBeanList) {
                    picInfoBean.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
            if (downloadProcessBar != null) {
                downloadProcessBar.clear();
                downloadProcessBar.setVisibility(View.GONE);
            }

            ((DownloadService) context).doPicListDownloadComplete(dirName, index, localPosition);
        }
    }



}
