package com.example.jianming.listAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jianming.Utils.EnvArgs;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.myapplication.PicAlbumActivity;
import com.example.jianming.myapplication.PicAlbumListActivityMD;
import com.example.jianming.myapplication.R;
import com.example.jianming.views.DownloadProcessBar;

import java.io.File;
import java.util.List;

public class PicAlbumListAdapter extends RecyclerView.Adapter<PicAlbumListAdapter.ViewHolder> {
    private final static String TAG = "PicAlbumListAdapter";
    private final LayoutInflater mInflater;
    private List<PicAlbumBean> dataArray;

    private Context context;

    public PicAlbumListAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setDataArray(List<PicAlbumBean> dataArray) {
        this.dataArray = dataArray;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pic_list_content, parent, false);

        ViewHolder vh = new ViewHolder(v);
        v.setTag(vh);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.textView.setText(dataArray.get(position).getName());
//        if (FileUtil.checkDirExist(context, dataArray.get(position).getName())) {
        if (PicAlbumBean.getExistByServerIndex(dataArray.get(position).getServerIndex()) == 1) {
            viewHolder.textView.setTextColor(Color.rgb(0, 255, 0));
            viewHolder.exist = true;
        } else {
            viewHolder.textView.setTextColor(Color.rgb(0, 128, 0));
            viewHolder.exist = false;
        }
        viewHolder.serverIndex = dataArray.get(position).getServerIndex();
        viewHolder.localPosition = position;
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
//                        PicAlbumBean.setExistByServerIndex(viewHolder.serverIndex, 0);
//                        PicAlbumBean picAlbum = PicAlbumBean.getByServerIndex(viewHolder.serverIndex);
                        PicAlbumBean.deletePicAlbumFromDb(viewHolder.serverIndex);
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
    }



    @Override
    public int getItemCount() {
        return this.dataArray.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;

        public ImageView deleteBtn;

        public int serverIndex;

        public int localPosition;

        public boolean exist = false;

        public DownloadProcessBar downloadProcessView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.pic_text_view);
            this.deleteBtn = (ImageView) itemView.findViewById(R.id.delete_btn);
            this.downloadProcessView = (DownloadProcessBar) itemView.findViewById(R.id.customer_view1);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //PicAlbumListAdapter.ViewHolder holder = (PicAlbumListAdapter.ViewHolder) view.getTag();
            final String name = this.textView.getText().toString();
            int serverIndex = this.serverIndex;
            if (this.exist) {
                Log.i(TAG, "you click " + serverIndex + "th item, name = " + name);
                Intent intent = new Intent((PicAlbumListActivityMD)context, PicAlbumActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("serverIndex", serverIndex);
                context.startActivity(intent);
            } else {
                File file = FileUtil.getAlbumStorageDir((PicAlbumListActivityMD)context, name);
                if (file.mkdirs()) {
                    Log.i(TAG, file.getAbsolutePath() + " made");
                }
                String url = ("http://%serverIP:%serverPort/local1000/picContentAjax?id=" + serverIndex)
                        .replace("%serverIP", EnvArgs.serverIP)
                        .replace("%serverPort", EnvArgs.serverPort);
                ((PicAlbumListActivityMD)context).downLoadService.startDownload(serverIndex, getPosition(), name, this.downloadProcessView, url);
            }
        }
    }
}
