package com.example.jianming.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.example.jianming.Tasks.DownloadWebpageTask;
import com.example.jianming.Utils.EnvArgs;

import butterknife.Bind;
import butterknife.ButterKnife;

import butterknife.OnClick;
import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.Utils.TimeUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.UpdateStamp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Local1KActivity extends AppCompatActivity {

    Context self = this;

    @Bind(R.id.file_training_toolbar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_1k);
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

    @OnClick(R.id.local1000)
    public void goToLocal1000() {
//        if (NetworkUtil.isNetworkAvailable(this)) {
//            startDownloadWebPage();
//        } else {
//            Intent intent = new Intent(self, PicAlbumListActivityMD.class);
//            self.startActivity(intent);
//            Log.i("network", "No network connection available.");
//        }
        Intent intent = new Intent(self, PicAlbumListActivityMD.class);
        startActivity(intent);
    }

}
