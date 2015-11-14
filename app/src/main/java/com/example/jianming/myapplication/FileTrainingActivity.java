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

    @OnClick(R.id.local1000)
    public void goToLocal1000() {
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
        UpdateStamp albumStamp = UpdateStamp.getUpdateStampByTableName("T_ALBUM_INFO");

        String stringUrl = "http://%serverIP:%serverPort/local1000/picIndexAjax?time_stamp=%timeStamp"
                .replace("%serverIP", EnvArgs.serverIP)
                .replace("%serverPort", EnvArgs.serverPort)
                .replace("%timeStamp", albumStamp.getUpdateStamp())
                ;
        Log.d("startDownloadWebPage", stringUrl);
        albumStamp.setUpdateStamp(TimeUtil.getGmtInFormatyyyyMMddHHmmss());
        albumStamp.save();
        new DownloadWebpageTask() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    ActiveAndroid.beginTransaction();
                    JSONArray jsonArray = new JSONArray(s);
//                    Log.d("", s);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PicAlbumBean picIndexBean = new PicAlbumBean();
                        picIndexBean.setIndex(Integer.parseInt(jsonObject.getString("index")));
                        picIndexBean.setName(jsonObject.getString("name"));
                        picIndexBean.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActiveAndroid.endTransaction();
                }

                Intent intent = new Intent(self, PicAlbumListActivity.class);
                self.startActivity(intent);
            }
        }.execute(stringUrl);
    }
}
