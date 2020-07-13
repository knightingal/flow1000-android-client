package com.example.jianming.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

import butterknife.OnClick;


public class Local1KActivity extends AppCompatActivity {

    Context self = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_1k);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.local1000)
    public void goToLocal1000() {
        Intent intent = new Intent(self, PicAlbumListActivityMD.class);
        startActivity(intent);
    }

}
