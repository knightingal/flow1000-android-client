package com.example.jianming.myapplication;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.jianming.Utils.DIOptionsExactly;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Activity4List extends ListActivity {

    //ListView mListView;

    private List<Map<String, Object>> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mData = getData();
        ListAdapter adapter = new MyAdapter(this);
        setListAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;
        for (int i = 45; i != 0; i--) {
            map = new HashMap<>();
            map.put("title", "G" + i);
            map.put("info", "google " + i);
            map.put("img", "http://192.168.0.104:8081/picDirs/picRepository/2/" + i + ".jpg");
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.vlist, null);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                //holder.info = (TextView) convertView.findViewById(R.id.info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //holder.img.setImageResource((Integer)mData.get(position).get("img"));
            ImageLoader.getInstance().displayImage((String) mData.get(position).get("img"), holder.img, DIOptionsExactly.getInstance().getOptions());
            holder.title.setText((String) mData.get(position).get("title"));

            //holder.info.setText((String) mData.get(position).get("info"));
            return convertView;
        }
    }

}
