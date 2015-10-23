package com.example.jianming.myapplication;

import android.content.Context;
import android.content.Intent;
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
import com.example.jianming.beans.PicIndexBean;
import com.example.jianming.db.DbContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @OnClick(R.id.network)
    public void network() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            startDownloadWebPage();
        } else {
            Intent intent = new Intent(self, PicAlbumListActivity.class);
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
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PicIndexBean picIndexBean = new PicIndexBean();
                        picIndexBean.setIndex(Integer.parseInt(jsonObject.getString("index")));
                        picIndexBean.setName(jsonObject.getString("name"));
                        DbContract.writeAblum(picIndexBean.getIndex(), picIndexBean.getName(), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DbContract.query();
                File directory = FileUtil.getAlbumStorageDir(FileTrainingActivity.this, "file");
                File file = new File(directory, "index.json");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                    fileOutputStream.write(s.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(self, PicAlbumListActivity.class);
                self.startActivity(intent);
            }
        }.execute(stringUrl);
    }
}
