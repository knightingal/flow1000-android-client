package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

//import com.example.jianming.Tasks.DownloadPicListTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.Utils.NetworkUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.listAdapters.PicAlbumListAdapter;
import com.example.jianming.services.DownloadService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


public class PicAlbumListActivity extends AppCompatActivity {


    DownloadService downLoadService = null;

    Boolean isBound = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            isBound = true;
            downLoadService = ((DownloadService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            downLoadService = null;
            isBound = false;
        }
    };

    public void doPicListDownloadComplete(String dirName, int index) {
        PicAlbumBean.setExistByIndex(index, 1);
        Intent intent = new Intent(this, PicAlbumActivity.class);
        intent.putExtra("name", dirName);
        intent.putExtra("index", index);
        picAlbumListAdapter.notifyDataSetChanged();
//        startActivity(intent);
    }

    PicAlbumListAdapter picAlbumListAdapter;

    Activity self = this;
    private final static String TAG = "PicListActivity";

    @Bind(R.id.list_view1)
    public ListView listView;

    @Bind(R.id.tl_custom)
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_pic_album_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



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
        unbindService(conn);
        isBound = false;
    }

    @OnItemClick(R.id.list_view1)
    public void doItemClick(AdapterView<?> parent, View view, int position, long id) {
        PicAlbumListAdapter.ViewHolder holder = (PicAlbumListAdapter.ViewHolder) view.getTag();
        final String name = ((TextView) view.findViewById(R.id.pic_text_view))
                .getText()
                .toString();
        final int index = holder.index;
        if (holder.exist) {
            Log.i(TAG, "you click " + index + "th item, name = " + name);
            Intent intent = new Intent(self, PicAlbumActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("index", index);
            self.startActivity(intent);
        } else {
            File file = FileUtil.getAlbumStorageDir(PicAlbumListActivity.this, name);
            if (file.mkdirs()) {
                Log.i(TAG, file.getAbsolutePath() + " made");
            }
            String url = ("http://%serverIP:%serverPort/local1000/picContentAjax?id=" + index)
                    .replace("%serverIP", EnvArgs.serverIP)
                    .replace("%serverPort", EnvArgs.serverPort);
//            DownloadPicListTask.executeDownloadAlbumInfo(
//                    self,
//                    index,
//                    name,
//                    holder.downloadProcessView,
//                    url
//
//            );
            downLoadService.startDownload(index, name, holder.downloadProcessView, url);
        }
    }


    private String readIndexFile() {
        File directory = FileUtil.getAlbumStorageDir(PicAlbumListActivity.this, "file");
        File file = new File(directory, "index.json");
        String fileContent = "";
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);

            /*********** another way of reading file here  ****************
             * byte[] buff = new byte[fileInputStream.available()];
             *
             * fileInputStream.read(buff);
             * fileContent = new String(buff);
             ***************************************************************/

            byte[] buff = new byte[30];
            ByteArrayOutputStream out = new ByteArrayOutputStream(30);
            int readLen;
            do {
                readLen = fileInputStream.read(buff);
                if (readLen > 0) {
                    out.write(buff, 0, readLen);
                }
            } while(readLen != -1);
            fileContent = new String(out.toByteArray());
//            Log.i("readFile", fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    private List<PicAlbumBean> getDataSourceFromJsonFile() {
        if (isNotExistItemShown && NetworkUtil.isNetworkAvailable(this)) {
            return PicAlbumBean.getAll();
        }
        else {
            return PicAlbumBean.getAllExist();
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
                if (isBound && downLoadService != null) {
                    downLoadService.startDownload();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNotExistItemShown = true;
}
