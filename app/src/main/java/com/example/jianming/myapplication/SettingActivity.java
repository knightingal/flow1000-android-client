package com.example.jianming.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;




public class SettingActivity extends AppCompatActivity {

//    @BindView(R.id.ip_edit)
    public EditText ipEditText;

//    @BindView(R.id.port_edit)
    public EditText portEditText;

//    @BindView(R.id.tl_custom)
//    Toolbar toolbar;

//    @OnClick(R.id.button_done)
    public void doDoneBtn() {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ipEditText = findViewById(R.id.ip_edit);
        portEditText = findViewById(R.id.port_edit);
        ipEditText = findViewById(R.id.ip_edit);
        findViewById(R.id.button_done).setOnClickListener(v -> doDoneBtn());

//        setSupportActionBar(toolbar);

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        ipEditText.setFocusable(true);
        ipEditText.setFocusableInTouchMode(true);
        ipEditText.requestFocus();
        ipEditText.requestFocusFromTouch();
    }





}
