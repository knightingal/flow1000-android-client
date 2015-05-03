package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;



public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    Context self = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        setContentView(R.layout.activity_main);

        String mtype = android.os.Build.MODEL;
        Log.d(TAG, "mtype = " + mtype);



        final Button xrxBtn = (Button) findViewById(R.id.xrxBtn);
        xrxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, XrxActivity.class));
            }
        });

        final Button forListBtn = (Button) findViewById(R.id.forListBtn);
        forListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, PicListActivity.class));
            }
        });
        final Button fileTrainingBtn = (Button) findViewById(R.id.fileTrainingBtn);
        fileTrainingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, FileTrainingActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            this.startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
