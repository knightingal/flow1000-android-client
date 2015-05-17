package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;



public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xrxBtn:
                this.startActivity(new Intent(this, XrxActivity.class));
                break;
            case R.id.forListBtn:
                this.startActivity(new Intent(this, PicListActivity.class));
                break;
            case R.id.fileTrainingBtn:
                this.startActivity(new Intent(this, FileTrainingActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        setContentView(R.layout.activity_main);

        String mType = android.os.Build.MODEL;
        Log.d(TAG, "mType = " + mType);

        findViewById(R.id.xrxBtn).setOnClickListener(this);
        findViewById(R.id.forListBtn).setOnClickListener(this);
        findViewById(R.id.fileTrainingBtn).setOnClickListener(this);
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
