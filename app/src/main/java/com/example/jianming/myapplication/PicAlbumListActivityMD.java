package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import com.example.jianming.Tasks.DLAlbumTask;
import com.example.jianming.Tasks.DownloadWebpageTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.Utils.TimeUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicAlbumData;
import com.example.jianming.beans.UpdateStamp;
import com.example.jianming.listAdapters.PicAlbumListAdapter;
import com.example.jianming.views.DownloadProcessBar;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nanjing.knightingal.processerlib.RefreshListener;
import org.nanjing.knightingal.processerlib.Services.DownloadService;
import org.nanjing.knightingal.processerlib.beans.CounterBean;
import org.nanjing.knightingal.processerlib.tools.StGson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PicAlbumListActivityMD extends AppCompatActivity implements RefreshListener {

    private static final List<String> TYPE_LIST = new ArrayList<>();
    private final static String TAG = "PicAlbumListActivityMD";
    static {
        TYPE_LIST.add(TAG);
    }

    @Override
    public void doRefreshView(CounterBean counterBean) {

        Message msg = new Message();
        Bundle data = new Bundle();
        data.putSerializable("data", counterBean);
        msg.setData(data);
        refreshHandler.sendMessage(msg);
    }

    Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CounterBean counterBean = (CounterBean) msg.getData().getSerializable("data");
            refreshListItem(counterBean);
        }
    };

    private void refreshListItem(CounterBean counterBean) {
        PicAlbumListAdapter.ViewHolder viewHolder = ((PicAlbumListAdapter.ViewHolder)listView.findViewHolderForAdapterPosition(counterBean.getIndex()));
        if (viewHolder == null) {
            return;
        }

//        picAlbumListAdapter.counterBeanList.set(counterBean.getIndex(), counterBean);

//        viewHolder.procTv.setText(String.valueOf(counterBean.getCurr()) + "/" + String.valueOf(counterBean.getMax()));
        viewHolder.downloadProcessBar.setPercent(counterBean.getCurr() * 100 / counterBean.getMax());
        viewHolder.downloadProcessBar.invalidate();



    }


    public void asyncStartDownload(int index) {


        DLAlbumTask dlAlbumTask = new DLAlbumTask(this);
        dlAlbumTask.setTaskNotifier(downLoadService);
//        SleepTask sleepTask = new SleepTask(downloadService);
        downLoadService.asyncStartDownload(dlAlbumTask, index);
    }

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
            downLoadService.setRefreshListener(TYPE_LIST, PicAlbumListActivityMD.this);
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



        List<PicAlbumBean> picAlbumBeanList = getDataSourceFromJsonFile();
        List<PicAlbumData> picAlbumDataList = new ArrayList<>();
        for (PicAlbumBean picAlbumBean : picAlbumBeanList) {
            PicAlbumData picAlbumData = new PicAlbumData();
            picAlbumData.setPicAlbumData(picAlbumBean);
            picAlbumDataList.add(picAlbumData);
        }
//        picAlbumListAdapter.setDataArray(picAlbumDataList);
        listView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        picAlbumListAdapter = new PicAlbumListAdapter(this);
        picAlbumListAdapter.setDataArray(picAlbumDataList);
        listView.setAdapter(picAlbumListAdapter);
        startDownloadWebPage();

    }

    @Override
    protected void onPause() {
        super.onPause();
        downLoadService.removeListener();
        downLoadService = null;
        unbindService(conn);
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, DownloadService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
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
                List<PicAlbumBean> picAlbumBeanList = getDataSourceFromJsonFile();
                List<PicAlbumData> picAlbumDataList = new ArrayList<>();
                for (PicAlbumBean picAlbumBean : picAlbumBeanList) {
                    PicAlbumData picAlbumData = new PicAlbumData();
                    picAlbumData.setPicAlbumData(picAlbumBean);
                    picAlbumDataList.add(picAlbumData);
                }
                picAlbumListAdapter.setDataArray(picAlbumDataList);
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

    public void doPicListDownloadComplete(String dirName, int index, int localPosition) {
        picAlbumListAdapter.notifyDataSetChanged();
    }

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
        final ObjectMapper mapper = new ObjectMapper();
        new DownloadWebpageTask() {
            @Override
            protected void onPostExecute(String s) {
                try {
                    ActiveAndroid.beginTransaction();

                    PicAlbumBean[] picAlbumBeanList = mapper.readValue(s, PicAlbumBean[].class);
                    for (PicAlbumBean picAlbumBean : picAlbumBeanList) {
                        picAlbumBean.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    ActiveAndroid.endTransaction();
                }
                List<PicAlbumBean> picAlbumBeanList = getDataSourceFromJsonFile();
                List<PicAlbumData> picAlbumDataList = new ArrayList<>();
                for (PicAlbumBean picAlbumBean : picAlbumBeanList) {
                    PicAlbumData picAlbumData = new PicAlbumData();
                    picAlbumData.setPicAlbumData(picAlbumBean);
                    picAlbumDataList.add(picAlbumData);
                }
                picAlbumListAdapter.setDataArray(picAlbumDataList);
//                picAlbumListAdapter.setDataArray(getDataSourceFromJsonFile());
                picAlbumListAdapter.notifyDataSetChanged();

            }
        }.execute(stringUrl);
    }
}
