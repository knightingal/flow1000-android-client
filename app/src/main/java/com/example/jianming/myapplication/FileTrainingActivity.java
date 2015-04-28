package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jianming.Tasks.DownloadWebpageTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileTrainingActivity extends Activity implements View.OnClickListener{

    Context self = this;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.network:
                network();
                break;
            default:
                break;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_training);

        findViewById(R.id.network).setOnClickListener(this);
    }

    private void network() {
        String stringUrl = "http://%serverIP:%serverPort/picDirs/picIndexAjax"
                .replace("%serverIP", EnvArgs.serverIP)
                .replace("%serverPort", EnvArgs.serverPort);
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
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
                    Intent intent = new Intent(self, PicListActivity.class);
                    self.startActivity(intent);
                }
            }.execute(stringUrl);
        } else {
            Intent intent = new Intent(self, PicListActivity.class);
            //intent.putExtra("jsonArg", s);
            self.startActivity(intent);
            Log.i("network", "No network connection available.");
        }
    }
}
