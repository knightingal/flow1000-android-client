package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jianming.Tasks.DownloadPicListTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.Utils.JsonUtil;
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


public class PicAlbumListActivity extends Activity {

    public void doPicListDownloadComplete(String dirName, int index) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_list_acivity);
        ButterKnife.bind(this);

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
            Log.i("readFile", fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    private List<PicIndexBean> getDataSourceFromJsonFile() {
        List<PicIndexBean> dataArray = new ArrayList<>();
        String fileContent = readIndexFile();
        try {
            JSONArray jsonArray = new JSONArray(fileContent);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PicIndexBean picIndexBean = new PicIndexBean();
                picIndexBean.setIndex(Integer.parseInt(jsonObject.getString("index")));
                picIndexBean.setName(jsonObject.getString("name"));
//                picIndexBean.setMtime(jsonObject.getString("mtime"));
                dataArray.add(picIndexBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataArray;
    }
}
