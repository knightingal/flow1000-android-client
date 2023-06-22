/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jianming.Tasks;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


import androidx.room.Room;

import com.example.jianming.Utils.AppDataBase;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.beans.AlbumInfoBean;
import com.example.jianming.beans.DLFilePathBean;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.dao.PicAlbumDao;
import com.example.jianming.dao.PicInfoDao;
import com.example.jianming.myapplication.AlbumConfigKt;

import org.nanjing.knightingal.processerlib.TaskNotifier;
import org.nanjing.knightingal.processerlib.tasks.AbsTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Knightingal
 * @since v1.0
 */

public class DLAlbumTask extends AbsTask<Integer, Void, Integer> {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(256);
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };
    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }
    private final static String TAG = "DLAlbumTask";

    @Override
    protected Integer doInBackground(Integer... params) {
        int index = params[0];
        asyncStartDownload(index);


        return null;
    }


    public void setTaskNotifier(TaskNotifier taskNotifier) {
        this.type = "PicAlbumListActivityMD";
        this.taskNotifier = taskNotifier;
    }

    private TaskNotifier taskNotifier;

    private Activity context;

    private int position;
    PicAlbumDao picAlbumDao;
    PicInfoDao picInfoDao;
    AppDataBase db;

    private List<PicInfoBean> picInfoBeanList = null;
    public DLAlbumTask(Activity context, int position) {
        this.position = position;
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDataBase.class, "database-flow1000").allowMainThreadQueries().build();
        picAlbumDao = db.picAlbumDao();
        picInfoDao = db.picInfoDao();
    }
    public void asyncStartDownload(int index) {

        PicAlbumBean picAlbumBean = picAlbumDao.getByInnerIndex(index);
        picInfoBeanList = picInfoDao.queryByAlbumInnerIndex(picAlbumBean.getInnerIndex());

        AlbumInfoBean albumInfoBean = new AlbumInfoBean(
                "" + (picAlbumBean.getServerIndex()),
                picAlbumBean.getName(),
                new ArrayList<String>()
        );
        for (PicInfoBean picInfoBean : picInfoBeanList) {
            albumInfoBean.getPics().add(picInfoBean.getName());
            String picName = picInfoBean.getName();
            String url;
            if (AlbumConfigKt.getAlbumConfig(picAlbumBean.getAlbum()).getEncryped()) {
                url = "http://" + EnvArgs.serverIP + ":" + EnvArgs.serverPort + "/linux1000/" + AlbumConfigKt.getAlbumConfig(picAlbumBean.getAlbum()).getBaseUrl() + "/" + albumInfoBean.getDirName() + "/" + picName + ".bin";
            } else {
                url = "http://" + EnvArgs.serverIP + ":" + EnvArgs.serverPort + "/linux1000/" + AlbumConfigKt.getAlbumConfig(picAlbumBean.getAlbum()).getBaseUrl() + "/" + albumInfoBean.getDirName() + "/" + picName + "";
            }
            File directory = getAlbumStorageDir(this.context, albumInfoBean.getDirName());
            File file = new File(directory, picName);

            DLFilePathBean dlFilePathBean = new DLFilePathBean(
                    index,
                    url,
                    file,
                    picInfoBeanList.indexOf(picInfoBean),
                    position,
                    AlbumConfigKt.getAlbumConfig(picAlbumBean.getAlbum()).getEncryped()
            );
            DLImageTask dlImageTask = new DLImageTask(this, this.taskNotifier);
            dlImageTask.executeOnExecutor(THREAD_POOL_EXECUTOR, dlFilePathBean);
        }
    }

    private static File getAlbumStorageDir(Context context, String albumName) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), albumName);

        if (file.mkdirs()) {
            Log.i(TAG, "Directory of " + file.getAbsolutePath() + " created");
        }
        return file;
    }

    private int processCount = 0;

    void updatePicInfoBean(int index, int width, int height, String path) {
        picInfoBeanList.get(index).setWidth(width);
        picInfoBeanList.get(index).setHeight(height);
        picInfoBeanList.get(index).setAbsolutePath(path);
        processCount++;
        if (processCount == picInfoBeanList.size()) {
            try {
                db.beginTransaction();
                for (PicInfoBean picInfoBean : picInfoBeanList) {
                    picInfoDao.update(picInfoBean);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    @Override
    public int getTaskSize(int index) {
        if (picInfoBeanList == null) {
            PicAlbumBean picAlbumBean = picAlbumDao.getByInnerIndex(index);
            picInfoBeanList = picInfoDao.queryByAlbumInnerIndex(picAlbumBean.getInnerIndex());
        }
        return picInfoBeanList.size();
    }

}
