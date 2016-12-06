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

import com.example.jianming.beans.AlbumInfoBean;
import com.example.jianming.beans.DLFilePathBean;

import org.nanjing.knightingal.processerlib.TaskNotifier;
import org.nanjing.knightingal.processerlib.tasks.AbsTask;
import org.nanjing.knightingal.processerlib.tools.StGson;

import java.io.File;


/**
 * @author Knightingal
 * @since v1.0
 */

public class DLAlbumTask extends AbsTask<Integer, Void, Integer> {

    public static final String[] ALBUM_INFOS = {
            "{ \"picpage\": \"1\", \"pics\": [ \"1.jpg\", \"2.jpg\", \"3.jpg\", \"4.jpg\", \"5.jpg\", \"6.jpg\", \"7.jpg\", \"8.jpg\", \"9.jpg\", \"10.jpg\", \"11.jpg\", \"12.jpg\", \"13.jpg\", \"14.jpg\", \"15.jpg\", \"16.jpg\", \"17.jpg\", \"18.jpg\", \"19.jpg\", \"20.jpg\", \"21.jpg\", \"22.jpg\", \"23.jpg\", \"24.jpg\", \"25.jpg\", \"26.jpg\", \"27.jpg\", \"28.jpg\", \"29.jpg\", \"30.jpg\" ], \"dirName\": \"20130615152036宫廷床上的玉女Elina\" }",
            "{ \"pics\": [ \"1.jpg\", \"2.jpg\", \"3.jpg\", \"4.jpg\", \"5.jpg\", \"6.jpg\", \"7.jpg\", \"8.jpg\", \"9.jpg\", \"10.jpg\", \"11.jpg\", \"12.jpg\", \"13.jpg\", \"14.jpg\", \"15.jpg\", \"16.jpg\", \"17.jpg\", \"18.jpg\", \"19.jpg\", \"20.jpg\", \"21.jpg\", \"22.jpg\", \"23.jpg\", \"24.jpg\", \"25.jpg\", \"26.jpg\", \"27.jpg\", \"28.jpg\", \"29.jpg\", \"30.jpg\", \"31.jpg\", \"32.jpg\", \"33.jpg\", \"34.jpg\", \"35.jpg\", \"36.jpg\", \"37.jpg\", \"38.jpg\", \"39.jpg\", \"40.jpg\", \"41.jpg\", \"42.jpg\", \"43.jpg\", \"44.jpg\", \"45.jpg\", \"46.jpg\", \"47.jpg\", \"48.jpg\", \"49.jpg\", \"50.jpg\" ], \"dirName\": \"20130615162348十分洋气的小辣妹小雅\", \"picpage\": \"2\" }"
    };
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

    public DLAlbumTask(Activity context) {
        this.context = context;
    }
    public void asyncStartDownload(int index) {
        AlbumInfoBean albumInfoBean = StGson.gson.fromJson(ALBUM_INFOS[index], AlbumInfoBean.class);
        for (String picName : albumInfoBean.pics) {
            String url = "http://192.168.0.102/static/" + albumInfoBean.dirName + "/" + picName;
            File directory = getAlbumStorageDir(this.context, albumInfoBean.dirName);
            File file = new File(directory, picName);

            DLFilePathBean dlFilePathBean = new DLFilePathBean();
            dlFilePathBean.dest = file;
            dlFilePathBean.src = url;
            dlFilePathBean.index = index;
            DLImageTask dlImageTask = new DLImageTask(this, this.taskNotifier);
            dlImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dlFilePathBean);
        }
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

    @Override
    public int getTaskSize(int index) {
        return StGson.gson.fromJson(ALBUM_INFOS[index], AlbumInfoBean.class).pics.size();
    }

}
