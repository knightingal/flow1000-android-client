package com.example.jianming.Tasks;


import android.app.Activity;
import android.util.Log;

import com.example.jianming.Utils.Daos;
import com.example.jianming.beans.AlbumInfoBean;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.myapplication.App;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.nanjing.knightingal.processerlib.Services.DownloadService;

import java.io.IOException;
import java.lang.ref.SoftReference;

public class DownloadPicsTask extends DownloadWebpageTask {

    private static final String TAG = "DownloadPicsTask";
    private SoftReference<Activity> activity;
    private int position;
    private int index;
    private SoftReference<DownloadService> downloadService;

    public DownloadPicsTask(Activity activity, int position, int index, DownloadService downloadService) {
        this.activity = new SoftReference<>(activity);
        this.position = position;
        this.index = index;
        this.downloadService = new SoftReference<>(downloadService);
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        final PicAlbumBean picAlbumBean = PicAlbumBean.getByInnerIndex(index);
        Log.i(TAG, s);


        final ObjectMapper mapper = new ObjectMapper();
        try {
            AlbumInfoBean albumInfoBean = mapper.readValue(s, AlbumInfoBean.class);
            Daos.db.beginTransaction();
            for (String pic : albumInfoBean.pics.subList(0, albumInfoBean.pics.size() > 128? 128:albumInfoBean.pics.size())) {
                PicInfoBean picInfoBean = new PicInfoBean();

                picInfoBean.setAlbumIndex(picAlbumBean.getInnerIndex());
                picInfoBean.setName(pic);
                ((App)activity.get().getApplication()).getDaoSession().getPicInfoBeanDao().insert(picInfoBean);
            }
            Daos.db.setTransactionSuccessful();

            DLAlbumTask dlAlbumTask = new DLAlbumTask(activity.get(), position);
            dlAlbumTask.setTaskNotifier(downloadService.get());
            downloadService.get().asyncStartDownload(dlAlbumTask, index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Daos.db.endTransaction();
        }
    }


}
