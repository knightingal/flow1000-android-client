package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jianming.Tasks.DownloadPicListTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.beans.PicIndexBean;
import com.example.jianming.listAdapters.PicIndexAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PicIndexListActivity extends Activity {

    public void doPicListDownloadComplete(String dirName, int index) {
        Intent intent = new Intent(this, PicContentListActivity.class);
        intent.putExtra("name", dirName);
        picIndexAdapter.notifyDataSetChanged();
        startActivity(intent);
    }

    PicIndexAdapter picIndexAdapter;

    Activity self = this;
    private final static String TAG = "PicListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_list_acivity);
        List<PicIndexBean> dataArray = new ArrayList<>();
        File directory = FileUtil.getAlbumStorageDir(PicIndexListActivity.this, "file");
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
            Log.i("readFile", fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, fileContent);
        try {
            JSONArray jsonArray = new JSONArray(fileContent);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PicIndexBean picIndexBean = new PicIndexBean();
                picIndexBean.setIndex(Integer.parseInt(jsonObject.getString("index")));
                picIndexBean.setName(jsonObject.getString("name"));
                picIndexBean.setMtime(jsonObject.getString("mtime"));
                dataArray.add(picIndexBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.list_view1);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);


        picIndexAdapter = new PicIndexAdapter(this);
        picIndexAdapter.setDataArray(dataArray);
        listView.setAdapter(picIndexAdapter);

        listView.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_list, menu);
                return true;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle("clicked");

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doItemClick(view);
            }
        });
    }

    public void doItemClick(View view) {
        PicIndexAdapter.ViewHolder holder = (PicIndexAdapter.ViewHolder) view.getTag();
        final String name = ((TextView) view.findViewById(R.id.pic_text_view))
                .getText()
                .toString();
        final int index = holder.index;
        if (holder.exist) {
            Log.i(TAG, "you click " + index + "th item, name = " + name);
            Intent intent = new Intent(self, PicContentListActivity.class);
            intent.putExtra("name", name);
            self.startActivity(intent);
        } else {
            File file = FileUtil.getAlbumStorageDir(PicIndexListActivity.this, name);
            if (file.mkdirs()) {
                Log.i(TAG, file.getAbsolutePath() + " made");
            }
            DownloadPicListTask task = new DownloadPicListTask(
                    self,
                    index,
                    name,
                    holder.downloadProcessView
            );
            task.execute(("http://%serverIP:%serverPort/picDirs/picContentAjax?picpage=" + index)
                    .replace("%serverIP", EnvArgs.serverIP)
                    .replace("%serverPort", EnvArgs.serverPort));
        }

    }
}
