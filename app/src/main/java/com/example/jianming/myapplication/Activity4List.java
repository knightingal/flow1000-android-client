package com.example.jianming.myapplication;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Activity4List extends ListActivity {

    //ListView mListView;
    private Context self = this;

    private static final String TAG = "Activity4List";

    private List<Map<String, Object>> mData;

    private List<String> picList = new ArrayList<>();

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        index = this.getIntent().getIntExtra("index", 2);
        new DownloadWebpageTask() {
            @Override
            protected void onPostExecute(String s) {
                Log.i(TAG, s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray pics = jsonObject.getJSONArray("pics");
                    for (int i = 0; i < pics.length(); i++) {
                        picList.add(pics.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                doShowListView();
            }

        }.execute(("http://%serverIP:%serverPort/picDirs/picContentAjax?picpage=" + index)
                .replace("%serverIP", EnvArgs.serverIP)
                .replace("%serverPort", EnvArgs.serverPort));
//        mData = getData();
//        ListAdapter adapter = new MyAdapter(this);
//        setListAdapter(adapter);
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
            map.put("img", ("http://%serverIP:%serverPort/picDirs/picRepository/%index/" + picList.get(i))
                    .replace("%serverIP", EnvArgs.serverIP)
                    .replace("%serverPort", EnvArgs.serverPort)
                    .replace("%index", index + "")
            );
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
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Activity4List", (String) mData.get(position).get("img"));
//                    new DownloadPicTask() {
//                        @Override
//                        protected void onPostExecute(byte[] bytes) {
//                            //TODO save the bitmap here
//                        }
//
//                    }.execute((String) mData.get(position).get("img"));

                    Intent intent = new Intent(self, XrxActivity.class);
                    intent.putExtra("imgUrl", (String) mData.get(position).get("img"));

                    startActivity(intent);
                }
            });

            //holder.info.setText((String) mData.get(position).get("info"));
            return convertView;
        }
    }

}
