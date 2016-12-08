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
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.example.jianming.beans.AlbumInfoBean;
import com.example.jianming.beans.DLFilePathBean;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;

import org.nanjing.knightingal.processerlib.TaskNotifier;
import org.nanjing.knightingal.processerlib.tasks.AbsTask;
import org.nanjing.knightingal.processerlib.tools.StGson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Knightingal
 * @since v1.0
 */

public class DLAlbumTask extends AbsTask<Integer, Void, Integer> {

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

    private List<PicInfoBean> picInfoBeanList = null;
    public DLAlbumTask(Activity context) {
        this.context = context;
    }
    public void asyncStartDownload(int index) {
//        AlbumInfoBean albumInfoBean = StGson.gson.fromJson(ALBUM_INFOS[index], AlbumInfoBean.class);

        PicAlbumBean picAlbumBean = PicAlbumBean.getByInnerIndex(index);
        picInfoBeanList = PicInfoBean.queryByAlbum(picAlbumBean);

        AlbumInfoBean albumInfoBean = new AlbumInfoBean();
        albumInfoBean.dirName = picAlbumBean.getName();
        albumInfoBean.picpage = "" + (picAlbumBean.getServerIndex());
        albumInfoBean.pics = new ArrayList<String>();
        for (PicInfoBean picInfoBean : picInfoBeanList) {
            albumInfoBean.pics.add(picInfoBean.getName());
            String picName = picInfoBean.getName();
            String url = "http://192.168.0.102/static/" + albumInfoBean.dirName + "/" + picName;
            File directory = getAlbumStorageDir(this.context, albumInfoBean.dirName);
            File file = new File(directory, picName);

            DLFilePathBean dlFilePathBean = new DLFilePathBean();
            dlFilePathBean.dest = file;
            dlFilePathBean.src = url;
            dlFilePathBean.index = index;
            dlFilePathBean.picIndex = picInfoBeanList.indexOf(picInfoBean);
            DLImageTask dlImageTask = new DLImageTask(this, this.taskNotifier);
            dlImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dlFilePathBean);
        }


//        for (String picName : albumInfoBean.pics) {
//            String url = "http://192.168.0.102/static/" + albumInfoBean.dirName + "/" + picName;
//            File directory = getAlbumStorageDir(this.context, albumInfoBean.dirName);
//            File file = new File(directory, picName);
//
//            DLFilePathBean dlFilePathBean = new DLFilePathBean();
//            dlFilePathBean.dest = file;
//            dlFilePathBean.src = url;
//            dlFilePathBean.index = index;
//            DLImageTask dlImageTask = new DLImageTask(this, this.taskNotifier);
//            dlImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dlFilePathBean);
//        }
    }

    private static File getAlbumStorageDir(Context context, String albumName) {

        //File fileRoot = new File("/storage/sdcard1/Android/data/com.example.jianming.myapplication/files/Download/");
        //File file = new File(fileRoot, albumName);
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
                ActiveAndroid.beginTransaction();
                for (PicInfoBean picInfoBean : picInfoBeanList) {
                    picInfoBean.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
    }

    @Override
    public int getTaskSize(int index) {
        if (picInfoBeanList == null) {
            PicAlbumBean picAlbumBean = PicAlbumBean.getByInnerIndex(index);
            picInfoBeanList = PicInfoBean.queryByAlbum(picAlbumBean);
        }
        return picInfoBeanList.size();
    }

}
