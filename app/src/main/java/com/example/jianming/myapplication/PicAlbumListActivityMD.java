package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.listAdapters.PicAlbumListAdapter;
import com.example.jianming.services.DownloadService;
import com.example.jianming.views.DownloadProcessBar;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class PicAlbumListActivityMD extends AppCompatActivity implements PicCompletedListener {

    private final static String TAG = "PicAlbumListActivityMD";
    Activity self = this;

    PicAlbumListAdapter picAlbumListAdapter;
    @Bind(R.id.list_view1)
    public ListView listView;

    DownloadService downLoadService = null;

    Boolean isBound = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            isBound = true;
            downLoadService = ((DownloadService.LocalBinder) service).getService();
            downLoadService.setPicCompletedListener(PicAlbumListActivityMD.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            downLoadService = null;
            isBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_album_list_activity_md);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        List<PicAlbumBean> dataArray = getDataSourceFromJsonFile();
        picAlbumListAdapter = new PicAlbumListAdapter(this);
        picAlbumListAdapter.setDataArray(dataArray);
        listView.setAdapter(picAlbumListAdapter);

        bindService(new Intent(this, DownloadService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        downLoadService.setPicCompletedListener(null);
        downLoadService = null;
        unbindService(conn);
        isBound = false;
    }

    @OnItemClick(R.id.list_view1)
    public void doItemClick(AdapterView<?> parent, View view, int position, long id) {
        PicAlbumListAdapter.ViewHolder holder = (PicAlbumListAdapter.ViewHolder) view.getTag();
        final String name = ((TextView) view.findViewById(R.id.pic_text_view))
                .getText()
                .toString();
        int serverIndex = holder.serverIndex;
        if (holder.exist) {
            Log.i(TAG, "you click " + serverIndex + "th item, name = " + name);
            Intent intent = new Intent(self, PicAlbumActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("serverIndex", serverIndex);
            self.startActivity(intent);
        } else {
            File file = FileUtil.getAlbumStorageDir(PicAlbumListActivityMD.this, name);
            if (file.mkdirs()) {
                Log.i(TAG, file.getAbsolutePath() + " made");
            }
            String url = ("http://%serverIP:%serverPort/local1000/picContentAjax?id=" + serverIndex)
                    .replace("%serverIP", EnvArgs.serverIP)
                    .replace("%serverPort", EnvArgs.serverPort);
            downLoadService.startDownload(serverIndex, position, name, holder.downloadProcessView, url);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (NetworkUtil.isNetworkAvailable(this)) {
            int id = item.getItemId();

            if (id == R.id.hide_not_exist) {
                if (isNotExistItemShown) {
                    item.setTitle(R.string.show_not_exist_item);
                    isNotExistItemShown = false;
                } else {
                    item.setTitle(R.string.hide_not_exist_item);
                    isNotExistItemShown = true;
                }
                List<PicAlbumBean> dataArray = getDataSourceFromJsonFile();
                picAlbumListAdapter.setDataArray(dataArray);
                picAlbumListAdapter.notifyDataSetChanged();
            } else if (id == R.id.call_download) {
                View firstView = listView.getChildAt(0);
                View lastView = listView.getChildAt(listView.getChildCount() - 1);
                int firstIndex = ((PicAlbumListAdapter.ViewHolder)firstView.getTag()).serverIndex;
                int lastIndex = ((PicAlbumListAdapter.ViewHolder)lastView.getTag()).serverIndex;
                Log.d(TAG, firstIndex + " " + PicAlbumBean.getByServerIndex(firstIndex).getName());
                Log.d(TAG, lastIndex + " " + PicAlbumBean.getByServerIndex(lastIndex).getName());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private List<PicAlbumBean> getDataSourceFromJsonFile() {
        if (isNotExistItemShown && NetworkUtil.isNetworkAvailable(this)) {
            return PicAlbumBean.getAll();
        }
        else {
            return PicAlbumBean.getAllExist();
        }
    }


    private boolean isNotExistItemShown = true;

    @Override
    public void doPicListDownloadComplete(String dirName, int index, int localPosition) {
        picAlbumListAdapter.notifyDataSetChanged();
    }

    @Override
    public DownloadProcessBar getDownloadProcessBarByIndex(int index, int localPosition) {
        View firstView = listView.getChildAt(0);
        View lastView = listView.getChildAt(listView.getChildCount() - 1);
        int minIndex = ((PicAlbumListAdapter.ViewHolder)firstView.getTag()).localPosition;
        int maxIndex = ((PicAlbumListAdapter.ViewHolder)lastView.getTag()).localPosition;

        if (localPosition < minIndex || localPosition > maxIndex) {
            return null;
        }

        View currView = listView.getChildAt(localPosition - minIndex);
        return ((PicAlbumListAdapter.ViewHolder)currView.getTag()).downloadProcessView;
    }
}
