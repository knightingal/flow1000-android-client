package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jianming.beans.PicIndexBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PicListAcivity extends Activity {

    Activity self = this;
    private final static String TAG = "PicListAcivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_list_acivity);
        List<PicIndexBean> dataArray = new ArrayList<>();
        String jsonArg = this.getIntent().getStringExtra("jsonArg");
        Log.i(TAG, jsonArg);
        try {
            JSONArray jsonArray = new JSONArray(jsonArg);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PicIndexBean picIndexBean = new PicIndexBean();
                picIndexBean.setIndex(Integer.parseInt(jsonObject.getString("index")));
                picIndexBean.setName(jsonObject.getString("name"));
                picIndexBean.setMtime(jsonObject.getString("mtime"));
                dataArray.add(picIndexBean);
//                String name = jsonObject.getString("name");
//                dataArray.add(name);
                //Log.i("network", jsonObject.getString("name") + " " + jsonObject.getString("mtime"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.list_view1);

        PicAdapter picAdapter = new PicAdapter(this);
        picAdapter.setDataArray(dataArray);
        listView.setAdapter(picAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PicAdapter.ViewHolder holder = (PicAdapter.ViewHolder) view.getTag();
                String name = ((TextView) view.findViewById(R.id.pic_text_view)).getText().toString();
                if (holder.exist) {
                    int index = holder.index;
                    Log.i(TAG, "you click " + index + "th item, name = " + name);
                    Intent intent = new Intent(self, Activity4List.class);
                    intent.putExtra("index", index);
                    self.startActivity(intent);
                } else {
                    File file = new File(PicListAcivity.this.getExternalFilesDir(
                            Environment.DIRECTORY_DOWNLOADS), name);
                    file.mkdir();
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pic_list_acivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PicAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private List<PicIndexBean> dataArray;

        public PicAdapter(Context context) {

            this.mInflater = LayoutInflater.from(context);
        }

        public void setDataArray(List<PicIndexBean> dataArray) {
            this.dataArray = dataArray;
        }


        @Override
        public int getCount() {
            return this.dataArray.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private boolean checkDirExist(String dirName) {
            File file = new File(PicListAcivity.this.getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS), dirName);
            return file.exists();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.pic_list_content, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.pic_text_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(dataArray.get(position).getName());
            if (checkDirExist(dataArray.get(position).getName())) {
                viewHolder.textView.setTextColor(Color.rgb(0, 255, 0));
                viewHolder.exist = true;
            } else {
                viewHolder.textView.setTextColor(Color.rgb(255, 0, 0));
                viewHolder.exist = false;
            }
            viewHolder.index = dataArray.get(position).getIndex();
            return convertView;
        }

        class ViewHolder {
            TextView textView;

            int index;

            boolean exist = false;
        }
    }
}
