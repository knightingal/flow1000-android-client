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

package org.nanjing.knightingal.processerlib.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;


import org.nanjing.knightingal.processerlib.beans.Counter;
import org.nanjing.knightingal.processerlib.beans.CounterBean;
import org.nanjing.knightingal.processerlib.RefreshListener;
import org.nanjing.knightingal.processerlib.tasks.AbsTask;
import org.nanjing.knightingal.processerlib.tasks.SleepTask;
import org.nanjing.knightingal.processerlib.tools.StGson;
import org.nanjing.knightingal.processerlib.TaskNotifier;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Knightingal
 * @since v1.0
 */

public class DownloadService extends Service implements TaskNotifier {
    private static final String TAG = "DownloadService";
    private IBinder mBinder = new LocalBinder();

    private RefreshListener refreshListener = null;

    private List<String> typeList = null;

    public void setRefreshListener(List<String> typeList, RefreshListener refreshListener) {
        this.typeList = typeList;
        this.refreshListener = refreshListener;
    }

    public void removeListener() {
        this.refreshListener = null;
        this.typeList = null;

    }





    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBInd");
        return mBinder;
    }

    Thread th;
    private void startCounter() {
        th = new Thread(r);
        running = true;
        th.start();

    }

    List<Counter> counterList = new ArrayList<Counter>();
    boolean running;

    int info = 0;
    Runnable r = new Runnable() {
        @Override
        public void run() {
            while(running) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Log.d(TAG, "thread report!");
                try {
                    InetAddress address = InetAddress.getByName("192.168.0.103");
                    DatagramSocket socket = new DatagramSocket();

                    String sendData = StGson.gson.toJson(counterList);
                    byte[] bytes = sendData.getBytes();
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9600);
                    socket.send(packet);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (Counter counter : counterList) {
                    if (refreshListener != null) {
                        refreshListener.doRefreshView(new CounterBean(counter.getIndex(), counter.getCurr(), counter.getMax(), ""));
                    }
                    counter.inc();


                }
                info++;
            }
        }
    };

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }


    public void asyncStartDownload(AbsTask task, Long... params) {
//        SleepTask sleepTask = new SleepTask(this);
//        sleepTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, index);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

//        DLAlbumTask dlAlbumTask = new DLAlbumTask(this, this);
//        dlAlbumTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, index);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        running = false;
    }

    public SparseArray<Counter> counterSparseArray = new SparseArray<>();
    @Override
    public void onTaskComplete(AbsTask task, int index) {

        Counter counter = counterSparseArray.get(index);
        if (counter == null) {
            counter = new Counter(0, task.getTaskSize(index), 1, index);
//            counter = new Counter(0, 300, 1, index);
            counterSparseArray.put(index, counter);
        }
        counter.inc();

        String type = task.getType();
        if (refreshListener != null && typeList != null && typeList.contains(type)) {
            refreshListener.doRefreshView(new CounterBean(index, counter.getCurr(), counter.getMax(), type));
        }
    }
}
