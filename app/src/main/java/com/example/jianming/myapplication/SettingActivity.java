package com.example.jianming.myapplication;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jianming.Utils.EnvArgs;


public class SettingActivity extends Activity implements View.OnClickListener{


    private EditText ipEditText;
    private EditText portEditText;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_done:
                String ip = ipEditText.getText().toString();
                String port = portEditText.getText().toString();
                EnvArgs.serverIP = ip;
                EnvArgs.serverPort = port;
                finish();
                //this.finishActivity(0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ipEditText = (EditText) findViewById(R.id.ip_edit);
        portEditText = (EditText) findViewById(R.id.port_edit);
        findViewById(R.id.button_done).setOnClickListener(this);
    }





}
