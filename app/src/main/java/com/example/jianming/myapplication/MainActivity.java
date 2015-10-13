package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {


    private static final String TAG = "MainActivity";

    @OnClick({R.id.su27, R.id.picIndexBtn})
    public void btnClicked(View v) {
        switch (v.getId()) {
            case R.id.su27:
                this.startActivity(new Intent(this, XrxActivity.class));
                break;

            case R.id.picIndexBtn:
                this.startActivity(new Intent(this, FileTrainingActivity.class));
                break;

            default:
                break;
        }
    }

    @OnItemClick(R.id.lv_left_menu)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            startActivity(new Intent(this, SettingActivity.class));
        }
    }

    @InjectView(R.id.tl_custom)
    Toolbar toolbar;

    @InjectView(R.id.dl_left)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.lv_left_menu)
    ListView lvLeftMenu;

    private String[] lvs = {"Settings"};

    private ArrayAdapter arrayAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);

//        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
//                }
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
