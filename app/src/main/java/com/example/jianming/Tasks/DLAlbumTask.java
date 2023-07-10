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
import android.graphics.BitmapFactory;
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
import com.example.jianming.myapplication.AlbumConfig;
import com.example.jianming.myapplication.AlbumConfigKt;

import org.nanjing.knightingal.processerlib.TaskNotifier;
import org.nanjing.knightingal.processerlib.tasks.AbsTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


/**
 * @author Knightingal
 * @since v1.0
 */

public class DLAlbumTask extends AbsTask<Long, Void, Integer> {

    private final static String TAG = "DLAlbumTask";

    @Override
    protected Integer doInBackground(Long... params) {
        Long index = params[0];
        asyncStartDownload(index);
        return null;
    }


    public void setTaskNotifier(TaskNotifier taskNotifier) {
        this.type = "PicAlbumListActivityMD";
        this.taskNotifier = taskNotifier;
    }

    private TaskNotifier taskNotifier;

    private Activity context;

    private final int position;
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
    public void asyncStartDownload(Long index) {

        PicAlbumBean picAlbumBean = picAlbumDao.getByInnerIndex(index);
        picInfoBeanList = picInfoDao.queryByAlbumInnerIndex(picAlbumBean.getId());

        AlbumInfoBean albumInfoBean = new AlbumInfoBean(
                "" + (picAlbumBean.getId()),
                picAlbumBean.getName(),
                new ArrayList<String>()
        );
        for (PicInfoBean picInfoBean : picInfoBeanList) {
            albumInfoBean.getPics().add(picInfoBean.getName());
            String picName = picInfoBean.getName();
            AlbumConfig albumConfig = AlbumConfigKt.getAlbumConfig(picAlbumBean.getAlbum());

            String url = String.format("http://%s:%s/linux1000/%s/%s/%s",
                    EnvArgs.serverIP, EnvArgs.serverPort,
                    albumConfig.getBaseUrl(),
                    albumInfoBean.getDirName(), picName
            );
            if (albumConfig.getEncryped()) {
                url = url + ".bin";
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

            Function1<byte[], Unit> callback = bytes -> {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                int width = options.outWidth;
                int height = options.outHeight;
                String absolutePath = file.getAbsolutePath();
                updatePicInfoBean(picInfoBeanList.indexOf(picInfoBean), width, height, absolutePath);
                taskNotifier.onTaskComplete(DLAlbumTask.this, position);

                return Unit.INSTANCE;
            };


            ConcurrencyImageTask.INSTANCE.downloadUrl(url, file,
                    AlbumConfigKt.getAlbumConfig(picAlbumBean.getAlbum()).getEncryped(), callback);
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

    private final AtomicInteger processCount = new AtomicInteger(0);

    void updatePicInfoBean(int index, int width, int height, String path) {
        picInfoBeanList.get(index).setWidth(width);
        picInfoBeanList.get(index).setHeight(height);
        picInfoBeanList.get(index).setAbsolutePath(path);
        int currCount = processCount.incrementAndGet();
        if (currCount == picInfoBeanList.size()) {
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
            picInfoBeanList = picInfoDao.queryByAlbumInnerIndex(picAlbumBean.getId());
        }
        return picInfoBeanList.size();
    }

}
