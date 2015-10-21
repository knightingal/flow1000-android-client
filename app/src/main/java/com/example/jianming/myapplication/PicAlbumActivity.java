package com.example.jianming.myapplication;

import android.app.ListActivity;
import android.os.Bundle;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.listAdapters.PicAlbumAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PicAlbumActivity extends ListActivity {
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
        PicAlbumAdapter adapter = new PicAlbumAdapter(this);
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
