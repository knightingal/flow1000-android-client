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

    private List<Map<String, Object>> mData;

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
        mData = getData();
        ListAdapter adapter = new MyAdapter(this);
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

    public final class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView info;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.vlist, parent, false);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                //holder.info = (TextView) convertView.findViewById(R.id.info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String imgUrl = ImageDownloader.Scheme.FILE.wrap((String) mData.get(position).get("img"));

            ImageLoader.getInstance().displayImage(imgUrl, holder.img, DIOptionsExactly.getInstance().getOptions());
            holder.title.setText((String) mData.get(position).get("title"));
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Activity4List", (String) mData.get(position).get("img"));

                    Intent intent = new Intent(self, XrxActivity.class);
                    intent.putExtra("imgUrl", ImageDownloader.Scheme.FILE.wrap((String) mData.get(position).get("img")));

                    startActivity(intent);
                }
            });

            //holder.info.setText((String) mData.get(position).get("info"));
            return convertView;
        }
    }

}
