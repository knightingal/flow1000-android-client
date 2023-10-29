package com.example.jianming.myapplication;

import static android.content.pm.PackageManager.GET_META_DATA;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        CircularProgressIndicator progressIndicator = findViewById(R.id.download_process1);
        TextView versionCodeText = findViewById(R.id.version_code);
        TextView versionNameText = findViewById(R.id.version_name);

        ImageView imageView = findViewById(R.id.image_view_logo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressIndicator.setProgressCompat(50, true);
                progressIndicator.setMax(200);
            }
        });

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            versionNameText.setText(versionName);
            long versionCode = packageInfo.getLongVersionCode();
            versionCodeText.setText(String.valueOf(versionCode));

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
