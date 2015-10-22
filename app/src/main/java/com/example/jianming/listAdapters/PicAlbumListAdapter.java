package com.example.jianming.listAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jianming.Utils.FileUtil;
import com.example.jianming.beans.PicIndexBean;
import com.example.jianming.myapplication.R;
import com.example.jianming.views.DownloadProcessView;

import java.util.List;

public class PicAlbumListAdapter extends BaseAdapter {
    private final static String TAG = "PicAlbumListAdapter";
    private final LayoutInflater mInflater;
    private List<PicIndexBean> dataArray;

    private Context context;

    public PicAlbumListAdapter(Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.pic_list_content, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.pic_text_view);
            viewHolder.deleteBtn = (ImageView) convertView.findViewById(R.id.delete_btn);
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
            viewHolder.textView.setTextColor(Color.rgb(0, 128, 0));
            viewHolder.exist = false;
        }
        viewHolder.index = dataArray.get(position).getIndex();
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "you clicked " + PicAlbumListAdapter.this.dataArray.get(position).getName() + " delete_btn");
                AlertDialog.Builder builder = new AlertDialog.Builder(PicAlbumListAdapter.this.context);
                builder.setMessage("delete this dir?");
                builder.setTitle("");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileUtil.removeDir(PicAlbumListAdapter.this.context, PicAlbumListAdapter.this.dataArray.get(position).getName());
                        viewHolder.textView.setTextColor(Color.rgb(0, 128, 0));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        return convertView;
    }

    public class ViewHolder {
        public TextView textView;

        public ImageView deleteBtn;

        public int index;

        public boolean exist = false;

        public DownloadProcessView downloadProcessView;
    }
}
