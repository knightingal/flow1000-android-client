package com.example.jianming.myapplication;

import android.app.Application;

import com.example.jianming.Utils.Daos;
import com.example.jianming.beans.DaoMaster;
import com.example.jianming.beans.DaoSession;

import org.greenrobot.greendao.database.Database;


public class App extends Application {
    public DaoSession getDaoSession() {
        return daoSession;
    }

    private DaoSession daoSession;

    public Database getDb() {
        return db;
    }

    private Database db;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "Local1000.db");
        db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        Daos.picAlbumBeanDao = daoSession.getPicAlbumBeanDao();
        Daos.picInfoBeanDao = daoSession.getPicInfoBeanDao();
        Daos.updateStampDao = daoSession.getUpdateStampDao();
        Daos.db = db;
    }


}
