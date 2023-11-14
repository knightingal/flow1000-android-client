package com.example.jianming.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Jianming on 2015/4/28.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    public static File getAlbumStorageDir(Context context, String albumName) {

        //File fileRoot = new File("/storage/sdcard1/Android/data/com.example.jianming.myapplication/files/Download/");
        //File file = new File(fileRoot, albumName);
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), albumName);


        if (file.mkdirs()) {
            Log.i(TAG, "Directory of " + file.getAbsolutePath() + " created");
        }
        return file;
    }

    public static void removeDir(Context context, String albumName) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), albumName
        );
        try {
            File[] files = file.listFiles();
            for (File imgFile : files) {
                imgFile.delete();
            }
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkDirExist(Context context, String albumName) {
        //File fileRoot = new File("/storage/sdcard1/Android/data/com.example.jianming.myapplication/files/Download/");
        //File file = new File(fileRoot, albumName);
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), albumName);

        return file.exists();
    }
}
