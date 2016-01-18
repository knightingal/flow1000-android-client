package com.example.jianming.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.listAdapters.PicAlbumAdapter;

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
//        File file = FileUtil.getAlbumStorageDir(this, dirName);
//        File[] pics = file.listFiles();
//        for (File pic : pics) {
//            picList.add(pic.getAbsolutePath());
//        }

        picInfoBeanList = PicInfoBean.queryByAlbum(PicAlbumBean.getByServerIndex(albumIndex));
        doShowListView();
    }

    private void doShowListView() {
        PicAlbumAdapter adapter = new PicAlbumAdapter(this);
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
