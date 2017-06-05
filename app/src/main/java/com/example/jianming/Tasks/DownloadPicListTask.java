package com.example.jianming.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;

import com.example.jianming.beans.AlbumInfoBean;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.services.DownloadService;
import com.example.jianming.views.DownloadProcessBar;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jianming on 2015/5/4.
 */
public class DownloadPicListTask extends DownloadWebpageTask{
    private static final String TAG = "DownloadPicListTask";
    private String dirName;
    private int index;
    private Context context;

    public static void executeDownloadAlbumInfo(Context context, int serverIndex,  String dirName, String url) {
        DownloadPicListTask task = new DownloadPicListTask(context, serverIndex,  dirName);

        task.execute(url);
    }

    /**
     * Constructor of DownloadPicListTask
     *
     * @param context the context
     * @param index index from server
     * @param dirName the dirName
     */
    public DownloadPicListTask(Context context, int index, String dirName) {
        this.context = context;
        this.index = index;
        this.dirName = dirName;
    }

    List<PicInfoBean> picInfoBeanList = new ArrayList<>();

    int picCountAll = 0;
    int currPicCount = 0;

    List<String> pics;
    int currentIndex = 0;
    @Override
    protected void onPostExecute(String s) {
        Log.i(TAG, s);


        final ObjectMapper mapper = new ObjectMapper();
        try {
            AlbumInfoBean albumInfoBean = mapper.readValue(s, AlbumInfoBean.class);
            pics = albumInfoBean.pics;
            picCountAll = pics.size();

            int count = pics.size() < 128 ? pics.size(): 128;
            currentIndex = count;
            startMost128Task(0, count);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startMost128Task(int start, int end) throws JSONException{
        PicAlbumBean picAlbumBean = PicAlbumBean.getByServerIndex(index);
        for (int i = start; i < end; i++) {
            String imgUrl = generateImgUrl(dirName, pics.get(i));
            PicInfoBean picInfoBean = downloadImg(imgUrl, dirName, pics.get(i));

            picInfoBean.setIndex(i);
            picInfoBean.setName(pics.get(i));
            picInfoBean.setAlbumInfo(picAlbumBean);
            picInfoBeanList.add(picInfoBean);
        }
    }

    private String generateImgUrl(String dirName, String imgName) {
        if (EnvArgs.isEncrypt) {
            return ("http://%serverIP:%serverPort/static/encrypted/%dirName/" + imgName + ".bin")
                    .replace("%serverIP", EnvArgs.serverIP)
                    .replace("%serverPort", EnvArgs.serverPort)
                    .replace("%dirName", dirName);
        } else {
            return ("http://%serverIP:%serverPort/static/source/%dirName/" + imgName + "")
                    .replace("%serverIP", EnvArgs.serverIP)
                    .replace("%serverPort", EnvArgs.serverPort)
                    .replace("%dirName", dirName);
        }

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

        } else if (currPicCount == currentIndex) {
            int next128Count = currentIndex + 128;
            currentIndex = pics.size() < next128Count ? pics.size(): next128Count;
            try {
                startMost128Task(currPicCount, currentIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



}
