package com.example.jianming.Tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.myapplication.PicListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jianming on 2015/5/4.
 */
public class DownloadPicListTask extends DownloadWebpageTask{
    private static final String TAG = "DownloadPicListTask";
    private String dirName;
    private int index;
    private Context context;

    public DownloadPicListTask(Context context, int index, String dirName) {
        this.context = context;
        this.index = index;
        this.dirName = dirName;
    }

    int picCountAll = 0;
    int currPicCount = 0;
    @Override
    protected void onPostExecute(String s) {
        Log.i(TAG, s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray pics = jsonObject.getJSONArray("pics");
            picCountAll = pics.length();
            for (int i = 0; i < pics.length(); i++) {
                final String imgUrl = ("http://%serverIP:%serverPort/picDirs/picRepository/%index/" + pics.getString(i))
                        .replace("%serverIP", EnvArgs.serverIP)
                        .replace("%serverPort", EnvArgs.serverPort)
                        .replace("%index", index + "");
                downloadImg(
                        imgUrl,
                        dirName,
                        pics.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadImg(String imgUrl, final String dirName, final String picName) {
        new DownloadPicTask() {

            @Override
            protected void onPostExecute(byte[] bytes) {
                File directory = FileUtil.getAlbumStorageDir(context, dirName);
                File file = new File(directory, picName);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    fileOutputStream.write(bytes);
                    fileOutputStream.close();
                    currPicCount++;

                    //TODO: notify downloading process
                    if (currPicCount == picCountAll) {
                        Intent intent = new Intent(context, PicListActivity.class);
                        intent.putExtra("name", dirName);
                        context.startActivity(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.execute(imgUrl);
    }


}
