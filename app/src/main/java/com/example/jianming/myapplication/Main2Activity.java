package com.example.jianming.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.example.jianming.Utils.AppDataBase;
import com.example.jianming.dao.PicAlbumDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.Toast;


import com.example.jianming.beans.UpdateStamp;
import com.example.jianming.services.DownloadService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;



public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private PicAlbumDao picAlbumBeanDao;

//    @OnClick({R.id.picIndexBtn})
    public void btnClicked(View v) {
        if (v.getId() == R.id.picIndexBtn) {
            this.startActivity(new Intent(this, Local1KActivity.class));
        }

    }

    AppDataBase db;

//    @BindView(R.id.toolbar)
    public Toolbar toolbar;

//    @BindView(R.id.fab)
    public FloatingActionButton fab;

//    @BindView(R.id.drawer_layout)
    public DrawerLayout drawer;

//    @BindView(R.id.nav_view)
    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        db = Room.databaseBuilder(this,
//                AppDataBase.class, "database-name").allowMainThreadQueries().build();
        db = new AppDataBase();


        picAlbumBeanDao = db.picAlbumDao();
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        setContentView(R.layout.activity_main2);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        findViewById(R.id.picIndexBtn).setOnClickListener(this::btnClicked);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        initDB();

        startService(new Intent(this, DownloadService.class));
    }

    private void initDB() {
//        UpdateStamp albumStamp = this.db.updataStampDao().getUpdateStampByTableName("PIC_ALBUM_BEAN");
//        if (albumStamp == null) {
//            albumStamp = new UpdateStamp();
//            albumStamp.setTableName("PIC_ALBUM_BEAN");
//            albumStamp.setUpdateStamp("20000101000000");
//            this.db.updataStampDao().save(albumStamp);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    DownloadService mService;

    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {

        private static final String TAG = "ServiceConnection";

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.d(TAG, "onServiceConnected");
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
            mService = (DownloadService) binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceConnected");
            mService = null;
            mBound = false;
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, DownloadService.class));
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (id == R.id.clear_database) {
            clearDB();
            Toast.makeText(this, "DB cleared", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.set_timestamp) {
//            startActivity(new Intent(this, TimestampActivity.class));
        } else if (id == R.id.QR_code) {
            //QrcodeActivity
//            startActivity(new Intent(this, CapActivity.class));
//            startActivityForResult(new Intent(this, CapActivity.class), I_CAP_ACTIVITY);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static final int I_CAP_ACTIVITY = 1;

    private void clearDB() {
//        this.db.updataStampDao().deleteAll(null);
//        picAlbumBeanDao.deleteAll(null);
//        initDB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == I_CAP_ACTIVITY) {
            String url = data.getStringExtra("data");
            Toast.makeText(this, url, Toast.LENGTH_LONG).show();
        }

    }
}
