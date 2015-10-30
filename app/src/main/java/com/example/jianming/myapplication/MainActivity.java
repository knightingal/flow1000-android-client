package com.example.jianming.myapplication;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.OnItemClick;

import com.activeandroid.query.Delete;
import com.example.jianming.beans.PicIndexBean;
import com.example.jianming.beans.UpdateStamp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {


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

    private void clearDB() {
//        UpdateStamp.delete(UpdateStamp.class, 1);
        new Delete().from(UpdateStamp.class).execute();
        new Delete().from(PicIndexBean.class).execute();
        initDB();
    }

    @OnItemClick(R.id.lv_left_menu)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (position == 1) {
            clearDB();
            Toast.makeText(this, "DB cleared", Toast.LENGTH_SHORT).show();
        }
        //mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Bind(R.id.tl_custom)
    Toolbar toolbar;

    @Bind(R.id.dl_left)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.lv_left_menu)
    ListView lvLeftMenu;

    private String[] lvs = {"Settings", "Clear Database"};

    private ArrayAdapter arrayAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        initDB();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);
    }

    private void initDB() {
        UpdateStamp albumStamp = UpdateStamp.getUpdateStampByTableName("T_ALBUM_INFO");
        if (albumStamp == null) {
            albumStamp = new UpdateStamp();
            albumStamp.setTableName("T_ALBUM_INFO");
            albumStamp.setUpdateStamp("20000101000000");
            albumStamp.save();
        }
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
        } else if (id == R.id.action_about) {
            this.startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
