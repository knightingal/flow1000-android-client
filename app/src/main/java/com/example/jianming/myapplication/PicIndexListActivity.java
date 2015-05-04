package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jianming.Tasks.DownloadPicListTask;
import com.example.jianming.Tasks.DownloadPicTask;
import com.example.jianming.Tasks.DownloadWebpageTask;
import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.beans.PicIndexBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PicIndexListActivity extends Activity {



    Activity self = this;
    private final static String TAG = "PicListAcivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_list_acivity);
        List<PicIndexBean> dataArray = new ArrayList<>();
        File directory = FileUtil.getAlbumStorageDir(PicIndexListActivity.this, "file");
        File file = new File(directory, "index.json");
        String fileContent = "";
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);

            /*********** another way of reading file here  ****************
            * byte[] buff = new byte[fileInputStream.available()];
            *
            * fileInputStream.read(buff);
            * fileContent = new String(buff);
            ***************************************************************/

            byte[] buff = new byte[30];
            ByteArrayOutputStream out = new ByteArrayOutputStream(30);
            int readLen;
            do {
                readLen = fileInputStream.read(buff);
                if (readLen > 0) {
                    out.write(buff, 0, readLen);
                }
            } while(readLen != -1);
            fileContent = new String(out.toByteArray());
            Log.i("readFile", fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, fileContent);
        try {
            JSONArray jsonArray = new JSONArray(fileContent);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PicIndexBean picIndexBean = new PicIndexBean();
                picIndexBean.setIndex(Integer.parseInt(jsonObject.getString("index")));
                picIndexBean.setName(jsonObject.getString("name"));
                picIndexBean.setMtime(jsonObject.getString("mtime"));
                dataArray.add(picIndexBean);
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
                final String name = ((TextView) view.findViewById(R.id.pic_text_view)).getText().toString();
                final int index = holder.index;
                if (holder.exist) {

                    Log.i(TAG, "you click " + index + "th item, name = " + name);
                    Intent intent = new Intent(self, PicListActivity.class);
                    intent.putExtra("name", name);
                    self.startActivity(intent);
                } else {
                    File file = FileUtil.getAlbumStorageDir(PicIndexListActivity.this, name);
                    if (file.mkdirs()) {
                        Log.i(TAG, file.getAbsolutePath() + " made");
                    }
                    DownloadPicListTask task = new DownloadPicListTask(self, index, name);
                    task.execute(("http://%serverIP:%serverPort/picDirs/picContentAjax?picpage=" + index)
                            .replace("%serverIP", EnvArgs.serverIP)
                            .replace("%serverPort", EnvArgs.serverPort));
                }

            }
        });
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
            if (FileUtil.checkDirExist(PicIndexListActivity.this, dataArray.get(position).getName())) {
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
