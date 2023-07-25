package com.example.jianming.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        CircularProgressIndicator progressIndicator = findViewById(R.id.download_process1);

        ImageView imageView = findViewById(R.id.image_view_logo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressIndicator.setProgressCompat(50, true);
                progressIndicator.setMax(200);
            }
        });

    }
}
