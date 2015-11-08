package com.example.jianming.myapplication;

import android.app.ListActivity;
import android.os.Bundle;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.listAdapters.PicAlbumAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PicAlbumActivity extends ListActivity {
    private static final String TAG = "Activity4List";

    private List<String> picList = new ArrayList<>();

    private List<PicInfoBean> picInfoBeanList;

    String dirName;

    int albumIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dirName = this.getIntent().getStringExtra("name");
        albumIndex = this.getIntent().getIntExtra("index", 0);
//        File file = FileUtil.getAlbumStorageDir(this, dirName);
//        File[] pics = file.listFiles();
//        for (File pic : pics) {
//            picList.add(pic.getAbsolutePath());
//        }

        picInfoBeanList = PicInfoBean.queryByAlbum(PicAlbumBean.getByIndex(albumIndex));
        doShowListView();
    }

    private void doShowListView() {
        PicAlbumAdapter adapter = new PicAlbumAdapter(this);
        adapter.setDataArray(picInfoBeanList);
        setListAdapter(adapter);
    }

}
