package com.example.jianming.Utils;

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


        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), albumName);
        if (file.mkdirs()) {
            Log.i(TAG, "Directory of " + file.getAbsolutePath() + " created");
        }
        return file;
    }

    public static boolean checkDirExist(Context context, String albumName) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), albumName);

        return file.exists();
    }
}
