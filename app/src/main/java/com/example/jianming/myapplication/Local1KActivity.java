package com.example.jianming.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class Local1KActivity extends AppCompatActivity {

    Context self = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_1k);
        findViewById(R.id.local1000).setOnClickListener(v -> goToLocal1000());


    }

    public void goToLocal1000() {
        Intent intent = new Intent(self, PicAlbumListActivity.class);
        startActivity(intent);
    }

}
