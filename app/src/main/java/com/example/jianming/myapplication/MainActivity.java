package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;



public class MainActivity extends Activity {

    Context self = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        setContentView(R.layout.activity_main);



        final Button mButton1 = (Button) findViewById(R.id.button1);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, ImageTraningActivity.class));
            }
        });

        final Button mButton2 = (Button) findViewById(R.id.button2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, ScreenSlidePagerActivity.class));
            }
        });
        final Button mButton3 = (Button) findViewById(R.id.button3);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, CrossActivity.class));
            }
        });
        final Button mButton4 = (Button) findViewById(R.id.button4);
        mButton4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, XrxActivity.class));
            }
        });
        final Button mButton5 = (Button) findViewById(R.id.button5);
        mButton5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, ZoomActivity.class));
            }
        });
        final Button mButton6 = (Button) findViewById(R.id.button6);
        mButton6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, CardFlipActivity.class));
            }
        });
        final Button mButton7 = (Button) findViewById(R.id.button7);
        mButton7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                self.startActivity(new Intent(self, Activity4List.class));
            }
        });
        final Button mButton8 = (Button) findViewById(R.id.button8);
        mButton8.setOnClickListener(new View.OnClickListener(){
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
