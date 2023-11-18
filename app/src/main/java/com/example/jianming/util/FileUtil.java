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

    public static File getSectionStorageDir(Context context, String sectionName) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), sectionName);
        if (file.mkdirs()) {
            Log.i(TAG, "Directory of " + file.getAbsolutePath() + " created");
        }
        return file;
    }

    public static void removeDir(Context context, String sectionName) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), sectionName
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

    public static boolean checkDirExist(Context context, String sectionName) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), sectionName);

        return file.exists();
    }
}
