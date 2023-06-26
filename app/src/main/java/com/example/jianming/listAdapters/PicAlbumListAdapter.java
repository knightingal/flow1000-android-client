package com.example.jianming.listAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jianming.Utils.AppDataBase;
import com.example.jianming.Utils.FileUtil;
import com.example.jianming.beans.PicAlbumData;
import com.example.jianming.dao.PicAlbumDao;
import com.example.jianming.myapplication.PicAlbumActivity;
import com.example.jianming.myapplication.PicAlbumListActivity;
import com.example.jianming.myapplication.R;

import org.nanjing.knightingal.processerlib.beans.Counter;
import org.nanjing.knightingal.processerlib.view.ProcessBar;

import java.io.File;
import java.util.List;

public class PicAlbumListAdapter extends RecyclerView.Adapter<PicAlbumListAdapter.ViewHolder> {
    private final static String TAG = "PicAlbumListAdapter";
    private List<PicAlbumData> dataArray;

    private Context context;

    private PicAlbumDao picAlbumDao;

    public PicAlbumListAdapter(Context context) {
        this.context = context;
//        AppDataBase db = Room.databaseBuilder(context,
//                AppDataBase.class, "database-name").allowMainThreadQueries().build();
//        picAlbumDao = db.picAlbumDao();
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

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.textView.setText(dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumData().getName());
        if (dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumData().getExist() == 1) {
            viewHolder.textView.setTextColor(Color.rgb(0, 255, 0));
            viewHolder.downloadProcessBar.setVisibility(View.INVISIBLE);
            viewHolder.exist = true;
        } else {
            viewHolder.textView.setTextColor(Color.rgb(0, 128, 0));
            viewHolder.downloadProcessBar.setVisibility(View.INVISIBLE);
            viewHolder.exist = false;
        }
        if (((PicAlbumListActivity)context).getDownLoadService().getProcessingIndex().contains(viewHolder.getAdapterPosition())) {
            Counter counter = ((PicAlbumListActivity)context).getDownLoadService().counterSparseArray.get(viewHolder.getAdapterPosition());
            if (counter == null) {
                viewHolder.downloadProcessBar.setPercent(0);
            } else {
                viewHolder.downloadProcessBar.setPercent(counter.getCurr() * 100 / counter.getMax());
            }
            viewHolder.downloadProcessBar.setVisibility(View.VISIBLE);
        }
        viewHolder.serverIndex = dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumData().getServerIndex();
        viewHolder.position = viewHolder.getAdapterPosition();
        viewHolder.deleteBtn.setOnClickListener(v -> {
            Log.d(TAG, "you clicked " + PicAlbumListAdapter.this.dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumData().getName() + " delete_btn");
            AlertDialog.Builder builder = new AlertDialog.Builder(PicAlbumListAdapter.this.context);
            builder.setMessage("delete this dir?");
            builder.setTitle("");
            builder.setPositiveButton("yes", (dialog, which) -> {
                FileUtil.removeDir(PicAlbumListAdapter.this.context, PicAlbumListAdapter.this.dataArray.get(viewHolder.getAdapterPosition()).getPicAlbumData().getName());
                viewHolder.textView.setTextColor(Color.rgb(0, 128, 0));

                picAlbumDao.delete(picAlbumDao.getByServerIndex(viewHolder.serverIndex));
                dialog.dismiss();
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
        private TextView textView;

        private ImageView deleteBtn;

        public int serverIndex;

        public int position;

        private boolean exist = false;

        public ProcessBar downloadProcessBar;

        private ViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.pic_text_view);
            this.deleteBtn = itemView.findViewById(R.id.delete_btn);
            this.downloadProcessBar = itemView.findViewById(R.id.customer_view1);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final String name = this.textView.getText().toString();
            int serverIndex = this.serverIndex;
            if (this.exist) {
                Log.i(TAG, "you click " + serverIndex + "th item, name = " + name);
                Intent intent = new Intent(context, PicAlbumActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("serverIndex", serverIndex);
                context.startActivity(intent);
            } else {
                this.downloadProcessBar.setVisibility(View.VISIBLE);
                 ((PicAlbumListActivity)context).getDownLoadService().getProcessingIndex().add(position);

                File file = FileUtil.getAlbumStorageDir(context, name);
                if (file.mkdirs()) {
                    Log.i(TAG, file.getAbsolutePath() + " made");
                }
                Long innerIndex = dataArray.get(position)
                        .getPicAlbumData()
                        .getInnerIndex();
                if (innerIndex != null) {
                    ((PicAlbumListActivity) context).asyncStartDownload(innerIndex.intValue(), position);
                }
            }
        }
    }
}
