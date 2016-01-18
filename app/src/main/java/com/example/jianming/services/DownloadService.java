package com.example.jianming.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.jianming.Tasks.DownloadPicListTask;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.myapplication.PicCompletedListener;
import com.example.jianming.views.DownloadProcessBar;

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";


    IBinder mBinder = new LocalBinder();
    public DownloadService() {
    }

    public void startDownload(int index, String name, DownloadProcessBar downloadProcessView, String url) {
        DownloadPicListTask.executeDownloadAlbumInfo(
                this,
                index,
                name,
                downloadProcessView,
                url
        );
    }

    public void setPicCompletedListener(PicCompletedListener picCompletedListener) {
        this.picCompletedListener = picCompletedListener;
    }

    private PicCompletedListener picCompletedListener = null;

    public DownloadProcessBar getDownloadProcessBarByIndex(int index) {
        if (this.picCompletedListener == null) {
            return null;
        }
        return this.picCompletedListener.getDownloadProcessBarByIndex(index);
    }

    public void doPicListDownloadComplete(String dirName, int index) {
        PicAlbumBean.setExistByServerIndex(index, 1);
        if (picCompletedListener != null) {
            picCompletedListener.doPicListDownloadComplete(dirName, index);
        }
    }

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    public void callFromActivity() {
        Log.d(TAG, "callFromActivity");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "onBind");
//        throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
//        startCounter();

    }

    Thread th;

    private void startCounter() {
        th = new Thread(r);
        running = true;
        th.start();
    }

    public void startDownload() {
        Log.d(TAG, "startDownload");
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            while(running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "thread report!");
            }
        }
    };

    boolean running;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        running = false;
//        try {
//            th.join(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }
}
