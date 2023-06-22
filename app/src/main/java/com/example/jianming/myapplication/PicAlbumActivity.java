package com.example.jianming.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;


import com.example.jianming.Utils.AppDataBase;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.listAdapters.PicAlbumContentAdapter;

import java.util.ArrayList;
import java.util.List;


public class PicAlbumActivity extends ListActivity {
    private static final int PicContentRequestCode = 1;

    private static final String TAG = "PicAlbumActivity";

    private List<String> picList = new ArrayList<>();

    private List<PicInfoBean> picInfoBeanList;

    String dirName;

    int albumIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dirName = this.getIntent().getStringExtra("name");
        albumIndex = this.getIntent().getIntExtra("serverIndex", 0);

//        AppDataBase db = Room.databaseBuilder(this,
//                AppDataBase.class, "database-name").allowMainThreadQueries().build();
//        picInfoBeanList = db.picInfoDao().queryByAlbumInnerIndex(db.picAlbumDao().getByServerIndex(albumIndex).getInnerIndex());
        doShowListView();
    }

    private void doShowListView() {
        PicAlbumContentAdapter adapter = new PicAlbumContentAdapter(this);
        adapter.setDataArray(picInfoBeanList);
        setListAdapter(adapter);
    }

    public void startPicContentActivity(String[] imgs, int position) {
        Intent intent = new Intent(this, PicContentActivity.class);
        intent.putExtra("imgArray", imgs);
        intent.putExtra("position", position);
        startActivityForResult(intent, PicContentRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PicContentRequestCode:
                    int position = data.getIntExtra("position", -1);
                    getListView().setSelection(position);
                    break;
                default:
                    break;
            }
        }

    }
}
