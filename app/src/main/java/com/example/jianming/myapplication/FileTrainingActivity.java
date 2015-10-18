package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.jianming.Tasks.DownloadWebpageTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

import butterknife.OnClick;
import com.example.jianming.Utils.NetworkUtil;


public class FileTrainingActivity extends AppCompatActivity {

    Context self = this;

    @Bind(R.id.file_training_toolbar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_training);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.network)
    public void network() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            startDownloadWebPage();
        } else {
            Intent intent = new Intent(self, PicIndexListActivity.class);
            //intent.putExtra("jsonArg", s);
            self.startActivity(intent);
            Log.i("network", "No network connection available.");
        }
    }

    private void startDownloadWebPage() {
        String stringUrl = "http://%serverIP:%serverPort/local1000/picIndexAjax"
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
