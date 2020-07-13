package com.example.jianming.Tasks;


import android.app.Activity;
import android.util.Log;

import androidx.room.Room;

import com.example.jianming.Utils.AppDataBase;
import com.example.jianming.beans.AlbumInfoBean;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.dao.PicAlbumDao;
import com.example.jianming.dao.PicInfoDao;
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
    AppDataBase db;
    PicAlbumDao picAlbumDao;
    PicInfoDao picInfoDao;
    public DownloadPicsTask(Activity activity, int position, int index, DownloadService downloadService) {
        this.activity = new SoftReference<>(activity);
        this.position = position;
        this.index = index;
        this.downloadService = new SoftReference<>(downloadService);
        AppDataBase db = Room.databaseBuilder(activity.getApplicationContext(),
                AppDataBase.class, "database-name").allowMainThreadQueries().build();
        this.db = db;
        picAlbumDao = db.picAlbumDao();
        picInfoDao = db.picInfoDao();
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        final PicAlbumBean picAlbumBean = picAlbumDao.getByInnerIndex(index);
        Log.i(TAG, s);


        final ObjectMapper mapper = new ObjectMapper();
        try {
            AlbumInfoBean albumInfoBean = mapper.readValue(s, AlbumInfoBean.class);
            db.beginTransaction();
            for (String pic : albumInfoBean.pics) {
                PicInfoBean picInfoBean = new PicInfoBean();

                picInfoBean.setAlbumIndex(picAlbumBean.getInnerIndex());
                picInfoBean.setName(pic);
                picInfoDao.insert(picInfoBean);
            }
            db.setTransactionSuccessful();

            DLAlbumTask dlAlbumTask = new DLAlbumTask(activity.get(), position);
            dlAlbumTask.setTaskNotifier(downloadService.get());
            downloadService.get().asyncStartDownload(dlAlbumTask, index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


}
