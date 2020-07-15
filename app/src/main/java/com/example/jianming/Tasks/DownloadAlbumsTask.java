package com.example.jianming.Tasks;


import androidx.room.Room;

import com.example.jianming.Utils.AppDataBase;
import com.example.jianming.Utils.TimeUtil;
import com.example.jianming.beans.PicAlbumBean;

import com.example.jianming.beans.UpdateStamp;
import com.example.jianming.dao.PicAlbumDao;
import com.example.jianming.dao.UpdataStampDao;
import com.example.jianming.myapplication.PicAlbumListActivityMD;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

import java.lang.ref.SoftReference;

public class DownloadAlbumsTask extends DownloadWebpageTask {
    private PicAlbumDao picAlbumDao;
    private UpdataStampDao updataStampDao;

    private SoftReference<PicAlbumListActivityMD> activityMD;
    AppDataBase db;


    public DownloadAlbumsTask(PicAlbumListActivityMD activityMD) {

        this.activityMD = new SoftReference<>(activityMD);


        AppDataBase db = Room.databaseBuilder(activityMD.getApplicationContext(),
                AppDataBase.class, "database-name").allowMainThreadQueries().build();
        this.picAlbumDao = db.picAlbumDao();
        this.updataStampDao = db.updataStampDao();
        this.db = db;
    }

    @Override
    protected void onPostExecute(String s) {
        final ObjectMapper mapper = new ObjectMapper().registerModule(new KotlinModule());
        try {
//            Daos.db.beginTransaction();
            db.beginTransaction();
            UpdateStamp updateStamp = updataStampDao.getUpdateStampByTableName("PIC_ALBUM_BEAN");
            updateStamp.setUpdateStamp(TimeUtil.currentFormatyyyyMMddHHmmss());
            updataStampDao.update(updateStamp);
            PicAlbumBean[] picAlbumBeanList = mapper.readValue(s, PicAlbumBean[].class);
            for (PicAlbumBean picAlbumBean : picAlbumBeanList) {
                picAlbumDao.insert(picAlbumBean);
            }
//            Daos.db.setTransactionSuccessful();
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
//            Daos.db.endTransaction();
            db.endTransaction();
        }
        activityMD.get().refreshFrontPage();
    }
}
