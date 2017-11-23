package com.example.jianming.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jianming.Utils.Daos;
import com.example.jianming.beans.DaoSession;
import com.example.jianming.beans.PicAlbumBeanDao;
import com.example.jianming.beans.UpdateStamp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.nanjing.knightingal.processerlib.Services.DownloadService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DaoSession daoSession;
    private PicAlbumBeanDao picAlbumBeanDao;

    @OnClick({R.id.picIndexBtn})
    public void btnClicked(View v) {
        switch (v.getId()) {
            case R.id.picIndexBtn:
                this.startActivity(new Intent(this, Local1KActivity.class));
                break;

            default:
                break;
        }
    }

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.fab)
    public FloatingActionButton fab;

    @BindView(R.id.drawer_layout)
    public DrawerLayout drawer;

    @BindView(R.id.nav_view)
    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        daoSession = ((App)getApplication()).getDaoSession();
        picAlbumBeanDao = daoSession.getPicAlbumBeanDao();
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
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
        UpdateStamp albumStamp = UpdateStamp.getUpdateStampByTableName("PIC_ALBUM_BEAN");
        if (albumStamp == null) {
            albumStamp = new UpdateStamp();
            albumStamp.setTableName("PIC_ALBUM_BEAN");
            albumStamp.setUpdateStamp("20000101000000");
            albumStamp.save();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mBound) {
//            unbindService(mConnection);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent intent = new Intent(this, DownloadService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
            mService = binder.getService();
//            mService.callFromActivity();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_about) {
//            this.startActivity(new Intent(this, AboutActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
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
//        UpdateStamp.delete(UpdateStamp.class, 1);
//        new Delete().from(UpdateStamp.class).execute();
        Daos.updateStampDao.deleteAll();
//        new Delete().from(PicAlbumBean.class).execute();
        picAlbumBeanDao.deleteAll();
        initDB();
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
