package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.jianming.Tasks.DownloadWebpageTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class FileTrainingActivity extends Activity{

    Context self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_training);
        ButterKnife.inject(this);

    }

    @OnClick(R.id.network)
    public void network() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            startDownloadWebPage();
        } else {
            Intent intent = new Intent(self, PicIndexListActivity.class);
            //intent.putExtra("jsonArg", s);
            self.startActivity(intent);
            Log.i("network", "No network connection available.");
        }
    }

    private void startDownloadWebPage() {
        String stringUrl = "http://%serverIP:%serverPort/picDirs/picIndexAjax"
                .replace("%serverIP", EnvArgs.serverIP)
                .replace("%serverPort", EnvArgs.serverPort);
        new DownloadWebpageTask() {
            @Override
            protected void onPostExecute(String s) {
                File directory = FileUtil.getAlbumStorageDir(FileTrainingActivity.this, "file");
                File file = new File(directory, "index.json");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                    fileOutputStream.write(s.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(self, PicIndexListActivity.class);
                self.startActivity(intent);
            }
        }.execute(stringUrl);
    }
}
