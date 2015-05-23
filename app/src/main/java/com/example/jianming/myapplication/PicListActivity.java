package com.example.jianming.myapplication;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.jianming.Tasks.DownloadPicTask;
import com.example.jianming.Tasks.DownloadWebpageTask;
import com.example.jianming.Utils.DIOptionsExactly;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.listAdapters.PicListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PicListActivity extends ListActivity {

    //ListView mListView;
    private Context self = this;

    private static final String TAG = "Activity4List";

    private List<String> picList = new ArrayList<>();

    String dirName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dirName = this.getIntent().getStringExtra("name");
        File file = FileUtil.getAlbumStorageDir(this, dirName);
        File[] pics = file.listFiles();
        for (File pic : pics) {
            picList.add(pic.getAbsolutePath());
        }
        doShowListView();
    }

    private void doShowListView() {
        PicListAdapter adapter = new PicListAdapter(this);
        adapter.setDataArray(getData());
        setListAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;
        for (int i = 0; i != picList.size(); i++) {
            map = new HashMap<>();
            map.put("title", "G" + i);
            map.put("info", "google " + i);
            map.put("img", picList.get(i));

            list.add(map);
        }

        return list;
    }

}
