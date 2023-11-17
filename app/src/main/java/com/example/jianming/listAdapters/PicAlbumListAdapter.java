package com.example.jianming.listAdapters;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jianming.util.AppDataBase;
import com.example.jianming.util.FileUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicAlbumData;
import com.example.jianming.dao.PicAlbumDao;
import com.example.jianming.dao.PicInfoDao;
import com.example.jianming.myapplication.PicAlbumListActivity;
import com.example.jianming.myapplication.R;
import com.example.jianming.myapplication.SectionImageListActivity;
import com.example.jianming.services.Counter;
import com.google.android.material.progressindicator.CircularProgressIndicator;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PicAlbumListAdapter extends RecyclerView.Adapter<PicAlbumListAdapter.ViewHolder> {
    private final static String TAG = "PicAlbumListAdapter";
    private List<PicAlbumData> dataArray;

    private final Context context;

    private final PicAlbumDao picAlbumDao;

    private final PicInfoDao picInfoDao;

    public PicAlbumListAdapter(Context context) {
        this.context = context;
        AppDataBase db = Room.databaseBuilder(context,
                AppDataBase.class, "database-flow1000").allowMainThreadQueries().build();
        picAlbumDao = db.picAlbumDao();
        picInfoDao = db.picInfoDao();
    }

    public void setDataArray(List<PicAlbumData> dataArray) {
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

    private void renderExistItem(final ViewHolder viewHolder) {
        viewHolder.textView.setTextColor(context.getColor(R.color.md_theme_light_onPrimaryContainer));
        viewHolder.itemView.setBackgroundColor(context.getColor(R.color.md_theme_light_primaryContainer));
        viewHolder.downloadProcessBar.setVisibility(View.GONE);
        viewHolder.deleteBtn.setVisibility(View.VISIBLE);
        viewHolder.exist = true;
    }

    private void renderNonExistItem(final ViewHolder viewHolder) {
        viewHolder.textView.setTextColor(context.getColor(R.color.md_theme_light_onSurfaceVariant));
        viewHolder.itemView.setBackgroundColor(context.getColor(R.color.md_theme_light_surfaceVariant));
        viewHolder.deleteBtn.setVisibility(View.GONE);
        viewHolder.downloadProcessBar.setVisibility(View.VISIBLE);
        viewHolder.exist = false;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        dataArray.get(position).setPosition(position);

        viewHolder.textView.setText(formatTitle(dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumBean().getName()));
        if (dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumBean().getExist() == 1) {
            renderExistItem(viewHolder);
        } else {
            renderNonExistItem(viewHolder);
        }
        if (((PicAlbumListActivity) context).getDownLoadService() != null) {
            long albumId = dataArray.get(position).getPicAlbumBean().getId();
            if (Objects.requireNonNull(((PicAlbumListActivity) context).getDownLoadService())
                    .getProcessCounter().containsKey(albumId)) {
                Counter counter = ((PicAlbumListActivity) context).getDownLoadService()
                        .getProcessCounter().get(albumId);
                if (counter != null) {
                    viewHolder.downloadProcessBar.setVisibility(View.VISIBLE);
                    if (counter.getProcess() == 0 ) {
                        viewHolder.downloadProcessBar.setIndeterminate(true);
                    } else {
                        viewHolder.downloadProcessBar.setIndeterminate(false);
                        viewHolder.downloadProcessBar.setProgress(counter.getProcess(), true);
                        viewHolder.downloadProcessBar.setMax(counter.getMax());
                    }
                } else {
                    viewHolder.downloadProcessBar.setVisibility(View.GONE);
                }
            } else {
                viewHolder.downloadProcessBar.setVisibility(View.GONE);
            }
        }
        viewHolder.serverIndex = dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumBean().getId();
        viewHolder.position = viewHolder.getAdapterPosition();
        viewHolder.deleteBtn.setOnClickListener(v -> {
            Log.d(TAG, "you clicked " + PicAlbumListAdapter.this.dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumBean().getName() + " delete_btn");
            AlertDialog.Builder builder = new AlertDialog.Builder(PicAlbumListAdapter.this.context);
            builder.setMessage("delete this dir?");
            builder.setTitle("");
            builder.setPositiveButton("yes", (dialog, which) -> {
                FileUtil.removeDir(PicAlbumListAdapter.this.context, PicAlbumListAdapter.this.dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumBean().getName());
                PicAlbumBean picAlbumData = dataArray.get(position).getPicAlbumBean();

                picInfoDao.deleteByAlbumInnerIndex(viewHolder.serverIndex);
                picAlbumData.setExist(0);
                picAlbumDao.update(picAlbumData);

                DeleteAlbumKt.postDeleteAlbum(viewHolder.serverIndex);
                dialog.dismiss();
                notifyDataSetChanged();
//                renderNonExistItem(viewHolder);
            });
            builder.setNegativeButton("no", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }



    @Override
    public int getItemCount() {
        return this.dataArray.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView deleteBtn;
        private final TextView textView;

        private final View itemView;

        public long serverIndex;

        public int position;

        private boolean exist = false;

        public CircularProgressIndicator downloadProcessBar;

        private ViewHolder(View itemView) {

            super(itemView);
            this.textView = itemView.findViewById(R.id.pic_text_view);
            this.deleteBtn = itemView.findViewById(R.id.btn_delete);
            this.downloadProcessBar = itemView.findViewById(R.id.download_process);
            this.itemView = itemView;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final String name = this.textView.getText().toString();
            Long serverIndex = this.serverIndex;

            if (this.exist) {
                Log.i(TAG, "you click " + serverIndex + "th item, name = " + name);

                Intent intent = new Intent()
                        .setClassName(context, SectionImageListActivity.class.getName());
                intent.putExtra("name", name);
                intent.putExtra("serverIndex", serverIndex);
                context.startActivity(intent);
            } else {
                this.downloadProcessBar.setVisibility(View.VISIBLE);
                this.downloadProcessBar.setIndeterminate(true);
//                 ((PicAlbumListActivity)context).getDownLoadService().getProcessingIndex().add(position);

                File file = FileUtil.getAlbumStorageDir(context, name);
                if (file.mkdirs()) {
                    Log.i(TAG, file.getAbsolutePath() + " made");
                }
                long innerIndex = dataArray.get(position)
                        .getPicAlbumBean()
                        .getId();
                ((PicAlbumListActivity) context).asyncStartDownload((int) innerIndex, position);
            }
        }

    }

    private static String formatTitle(String sourceTitle) {
        if (sourceTitle.length() > 14) {
            String timeStamp = sourceTitle.substring(0, 14);
            boolean isTimeStamp = true;
            try {
                new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE).parse(timeStamp);
            } catch (ParseException e) {
                isTimeStamp = false;
            }
            if (isTimeStamp) {
                return sourceTitle.substring(14);
            }
            return sourceTitle;

        }
        return sourceTitle;
    }
}
