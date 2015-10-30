package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.jianming.Tasks.DownloadPicListTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.beans.PicIndexBean;
import com.example.jianming.listAdapters.PicAlbumListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


public class PicAlbumListActivity extends AppCompatActivity {

    public void doPicListDownloadComplete(String dirName, int index) {
        PicIndexBean.setExistByIndex(index, 1);
        Intent intent = new Intent(this, PicAlbumActivity.class);
        intent.putExtra("name", dirName);
        picAlbumListAdapter.notifyDataSetChanged();
        startActivity(intent);
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
        List<PicIndexBean> dataArray = getDataSourceFromJsonFile();
        picAlbumListAdapter = new PicAlbumListAdapter(this);
        picAlbumListAdapter.setDataArray(dataArray);
        listView.setAdapter(picAlbumListAdapter);

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
            self.startActivity(intent);
        } else {
            File file = FileUtil.getAlbumStorageDir(PicAlbumListActivity.this, name);
            if (file.mkdirs()) {
                Log.i(TAG, file.getAbsolutePath() + " made");
            }
            DownloadPicListTask task = new DownloadPicListTask(
                    self,
                    index,
                    name,
                    holder.downloadProcessView
            );
            task.execute(("http://%serverIP:%serverPort/local1000/picContentAjax?id=" + index)
                    .replace("%serverIP", EnvArgs.serverIP)
                    .replace("%serverPort", EnvArgs.serverPort));
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

    private List<PicIndexBean> getDataSourceFromJsonFile() {
        if (isNotExistItemShown) {
            return PicIndexBean.getAll();
        }
        else {
            return PicIndexBean.getAllExist();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.hide_not_exist) {
            if (isNotExistItemShown) {
                item.setTitle(R.string.show_not_exist_item);
                isNotExistItemShown = false;
            } else {
                item.setTitle(R.string.hide_not_exist_item);
                isNotExistItemShown = true;
            }
            List<PicIndexBean> dataArray = getDataSourceFromJsonFile();
            picAlbumListAdapter.setDataArray(dataArray);
            picAlbumListAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNotExistItemShown = true;
}
