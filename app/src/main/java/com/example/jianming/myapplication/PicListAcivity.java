package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PicListAcivity extends Activity {

    Activity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_list_acivity);
        List<String> dataArray = new ArrayList<>();
        String jsonArg = this.getIntent().getStringExtra("jsonArg");
        try {
            JSONArray jsonArray = new JSONArray(jsonArg);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                dataArray.add(name);
                //Log.i("network", jsonObject.getString("name") + " " + jsonObject.getString("mtime"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.list_view1);

        PicAdapter picAdapter = new PicAdapter(this);
        picAdapter.setDataArray(dataArray);
        listView.setAdapter(picAdapter);


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
        private List<String> dataArray;

        public PicAdapter(Context context) {

            this.mInflater = LayoutInflater.from(context);
        }

        public void setDataArray(List<String>  dataArray) {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.pic_list_content, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.pic_text_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(dataArray.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }
}
