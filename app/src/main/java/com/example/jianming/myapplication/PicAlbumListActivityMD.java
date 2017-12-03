package com.example.jianming.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jianming.Tasks.DownloadAlbumsTask;
import com.example.jianming.Tasks.DownloadPicsTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicAlbumBeanDao;
import com.example.jianming.beans.PicAlbumData;
import com.example.jianming.listAdapters.PicAlbumListAdapter;

import org.greenrobot.greendao.database.Database;
import org.nanjing.knightingal.processerlib.RefreshListener;
import org.nanjing.knightingal.processerlib.Services.DownloadService;
import org.nanjing.knightingal.processerlib.beans.CounterBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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

    private static class RefreshHandler extends Handler {
        PicAlbumListActivityMD picAlbumListActivityMD;

        RefreshHandler(PicAlbumListActivityMD picAlbumListActivityMD) {
            this.picAlbumListActivityMD = picAlbumListActivityMD;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CounterBean counterBean = (CounterBean) msg.getData().getSerializable("data");
            picAlbumListActivityMD.refreshListItem(counterBean);
        }
    }
    Handler refreshHandler = new RefreshHandler(this);

    private void refreshListItem(CounterBean counterBean) {
        PicAlbumListAdapter.ViewHolder viewHolder = ((PicAlbumListAdapter.ViewHolder)listView.findViewHolderForAdapterPosition(counterBean.getIndex()));
        if (counterBean.getCurr() == counterBean.getMax()) {
            picAlbumDataList.get(counterBean.getIndex()).getPicAlbumData().setExist(1);
            picAlbumBeanDao.update(picAlbumDataList.get(counterBean.getIndex()).getPicAlbumData());
            picAlbumListAdapter.notifyDataSetChanged();
        }


        if (viewHolder != null) {
            viewHolder.downloadProcessBar.setPercent(counterBean.getCurr() * 100 / counterBean.getMax());
            viewHolder.downloadProcessBar.postInvalidate();
        }
        Log.d(TAG, "current = " + counterBean.getCurr() + " max = " + counterBean.getMax());
    }


    public void asyncStartDownload(final int index, final int position) {

        final PicAlbumBean picAlbumBean = PicAlbumBean.getByInnerIndex(index);
        int serverIndex = picAlbumBean.getServerIndex();
        String url = ("http://%serverIP:%serverPort/local1000/picContentAjax?id=" + serverIndex)
                .replace("%serverIP", EnvArgs.serverIP)
                .replace("%serverPort", EnvArgs.serverPort);

        new DownloadPicsTask(this, position, index, downLoadService).execute(url);
    }

    PicAlbumListAdapter picAlbumListAdapter;
    @BindView(R.id.list_view11)
    public RecyclerView listView;

    public DownloadService downLoadService = null;

    Boolean isBound = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            isBound = true;
            downLoadService = (DownloadService) ((DownloadService.LocalBinder) service).getService();
            downLoadService.setRefreshListener(TYPE_LIST, PicAlbumListActivityMD.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            downLoadService = null;
            isBound = false;
        }
    };

    List<PicAlbumData> picAlbumDataList = new ArrayList<>();

    PicAlbumBeanDao picAlbumBeanDao;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = ((App)getApplication()).getDb();
        picAlbumBeanDao = ((App)getApplication()).getDaoSession().getPicAlbumBeanDao();
        setContentView(R.layout.activity_pic_album_list_activity_md);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);



//        List<PicAlbumBean> picAlbumBeanList = getDataSourceFromJsonFile();
//        for (PicAlbumBean picAlbumBean : picAlbumBeanList) {
//            PicAlbumData picAlbumData = new PicAlbumData();
//            picAlbumData.setPicAlbumData(picAlbumBean);
//            picAlbumDataList.add(picAlbumData);
//        }
        listView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        picAlbumListAdapter = new PicAlbumListAdapter(this);
        picAlbumListAdapter.setDataArray(picAlbumDataList);
        listView.setAdapter(picAlbumListAdapter);
        if (NetworkUtil.isNetworkAvailable(this)) {
            startDownloadWebPage();
        }
        else {
            refreshFrontPage();
        }

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



    private void startDownloadWebPage() {
        final com.example.jianming.beans.UpdateStamp albumStamp = com.example.jianming.beans.UpdateStamp.getUpdateStampByTableName("PIC_ALBUM_BEAN");

        String stringUrl = String.format(
                "http://%s:%s/local1000/picIndexAjax?time_stamp=%s",
                EnvArgs.serverIP,
                EnvArgs.serverPort,
                albumStamp.getUpdateStamp()
        );
        Log.d("startDownloadWebPage", stringUrl);
        new DownloadAlbumsTask(picAlbumBeanDao, this).execute(stringUrl);

    }

    public void refreshFrontPage() {
        List<PicAlbumBean> picAlbumBeanList = getDataSourceFromJsonFile();
        for (PicAlbumBean picAlbumBean : picAlbumBeanList) {
            PicAlbumData picAlbumData = new PicAlbumData();
            picAlbumData.setPicAlbumData(picAlbumBean);
            picAlbumDataList.add(picAlbumData);
        }
        picAlbumListAdapter.notifyDataSetChanged();

    }
}
