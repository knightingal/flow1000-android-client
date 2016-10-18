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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.example.jianming.Tasks.DownloadWebpageTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.Utils.TimeUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.UpdateStamp;
import com.example.jianming.listAdapters.PicAlbumListAdapter;
import com.example.jianming.services.DownloadService;
import com.example.jianming.views.DownloadProcessBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PicAlbumListActivityMD extends AppCompatActivity implements PicCompletedListener {

    private final static String TAG = "PicAlbumListActivityMD";

    PicAlbumListAdapter picAlbumListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Bind(R.id.list_view11)
    public RecyclerView listView;

    public DownloadService downLoadService = null;

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


        List<PicAlbumBean> dataArray = getDataSourceFromJsonFile();

        listView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        picAlbumListAdapter = new PicAlbumListAdapter(this);
        picAlbumListAdapter.setDataArray(dataArray);
        listView.setAdapter(picAlbumListAdapter);
        startDownloadWebPage();

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

    private void startDownloadWebPage() {
        final UpdateStamp albumStamp = UpdateStamp.getUpdateStampByTableName("T_ALBUM_INFO");

        String stringUrl = String.format(
                "http://%s:%s/local1000/picIndexAjax?time_stamp=%s",
                EnvArgs.serverIP,
                EnvArgs.serverPort,
                albumStamp.getUpdateStamp()
        );
        Log.d("startDownloadWebPage", stringUrl);
        new DownloadWebpageTask() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    ActiveAndroid.beginTransaction();
                    JSONArray jsonArray = new JSONArray(s);
                    Log.d("resp body", s);

                    albumStamp.setUpdateStamp(TimeUtil.getGmtInFormatyyyyMMddHHmmss());
                    albumStamp.save();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PicAlbumBean picIndexBean = new PicAlbumBean();
                        picIndexBean.setServerIndex(Integer.parseInt(jsonObject.getString("index")));
                        picIndexBean.setName(jsonObject.getString("name"));
                        picIndexBean.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    ActiveAndroid.endTransaction();
                }
                picAlbumListAdapter.setDataArray(getDataSourceFromJsonFile());
                picAlbumListAdapter.notifyDataSetChanged();
            }
        }.execute(stringUrl);
    }
}
