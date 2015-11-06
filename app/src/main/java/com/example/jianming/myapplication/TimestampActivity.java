package com.example.jianming.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jianming.Utils.TimeUtil;
import com.example.jianming.beans.UpdateStamp;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TimestampActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar toolbar;

    @Bind(R.id.curr_timestamp)
    public TextView currTimestamp;

    @Bind(R.id.new_timestapm)
    public EditText newTimestamp;

    @Bind(R.id.button_done)
    public Button doneBtn;

    UpdateStamp albumStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timestamp);
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

        albumStamp = UpdateStamp.getUpdateStampByTableName("T_ALBUM_INFO");
        currTimestamp.setText(albumStamp.getUpdateStamp());
        newTimestamp.setText(albumStamp.getUpdateStamp());


    }

    @OnClick(R.id.button_done)
    public void doneClick() {
        albumStamp.setUpdateStamp(newTimestamp.getText().toString());
        albumStamp.save();
    }

}
