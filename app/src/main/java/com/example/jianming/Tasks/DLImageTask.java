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

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jianming.Utils.Decryptor;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.beans.DLFilePathBean;

import org.nanjing.knightingal.processerlib.TaskNotifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Request;

/**
 * @author Knightingal
 * @since v1.0
 */

public class DLImageTask extends AsyncTask<DLFilePathBean, Void, Integer> {

    private DLAlbumTask parentTask;

    private static final String TAG = "DLImageTask";

    private TaskNotifier taskNotifier;

    public DLImageTask(DLAlbumTask parentTask, TaskNotifier taskNotifier) {
        this.parentTask = parentTask;
        this.taskNotifier = taskNotifier;
    }

    private DLFilePathBean dlFilePathBean;

    @Override
    protected Integer doInBackground(DLFilePathBean... dlFilePathBeen) {
        this.dlFilePathBean = dlFilePathBeen[0];
        downloadUrl(dlFilePathBeen[0].src, dlFilePathBeen[0].dest);

        return 0;
    }

    private void downloadUrl(String src, File dest)  {
        Log.d(TAG, "start download " + src);
        Request request = new Request.Builder().url(src).build();
        try {
            byte[] bytes = NetworkUtil.getOkHttpClient().newCall(request).execute().body().bytes();
            FileOutputStream fileOutputStream = new FileOutputStream(dest, true);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(dest.getAbsolutePath(), options);
            if (EnvArgs.isEncrypt) {
                BitmapFactory.decodeByteArray(Decryptor.decrypt(bytes), 0, bytes.length, options);
            } else {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            }
            width = options.outWidth;
            height = options.outHeight;
            absolutePath = dest.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "download " + src + " error");
            downloadUrl(src, dest);
        }

        Log.d(TAG, "end download " + dest.getAbsolutePath());
    }

    private int width = 0;

    private int height = 0;

    private String absolutePath = "";

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        parentTask.updatePicInfoBean(dlFilePathBean.picIndex, width, height, absolutePath);
        taskNotifier.onTaskComplete(parentTask, dlFilePathBean.position);
    }
}
