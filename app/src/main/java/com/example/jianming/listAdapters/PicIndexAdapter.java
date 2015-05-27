package com.example.jianming.listAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jianming.Utils.FileUtil;
import com.example.jianming.beans.PicIndexBean;
import com.example.jianming.myapplication.R;
import com.example.jianming.views.DownloadProcessView;

import java.util.List;

public class PicIndexAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<PicIndexBean> dataArray;

    private Context context;

    public PicIndexAdapter(Context context) {
        this.context = context;
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
            viewHolder.downloadProcessView = (DownloadProcessView) convertView.findViewById(R.id.customer_view1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(dataArray.get(position).getName());
        if (FileUtil.checkDirExist(context, dataArray.get(position).getName())) {
            viewHolder.textView.setTextColor(Color.rgb(0, 255, 0));
            viewHolder.exist = true;
        } else {
            viewHolder.textView.setTextColor(Color.rgb(255, 0, 0));
            viewHolder.exist = false;
        }
        viewHolder.index = dataArray.get(position).getIndex();
        return convertView;
    }

    public class ViewHolder {
        public TextView textView;

        public int index;

        public boolean exist = false;

        public DownloadProcessView downloadProcessView;
    }
}
