package com.example.jianming.listAdapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jianming.services.ProcessCounter;
import com.example.jianming.util.AppDataBase;
import com.example.jianming.util.FileUtil;
import com.example.jianming.beans.PicSectionBean;
import com.example.jianming.beans.PicSectionData;
import com.example.jianming.dao.PicSectionDao;
import com.example.jianming.dao.PicInfoDao;
import com.example.jianming.myapplication.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PicSectionListAdapter extends RecyclerView.Adapter<PicSectionListAdapter.ViewHolder> {


    public interface ItemClickListener {
        void onItemClick(PicSectionData picSectionData);
    }

    private final static String TAG = "PicSectionListAdapter";
    private List<PicSectionData> dataArray;

    private final Context context;

    private final PicSectionDao picSectionDao;

    private final PicInfoDao picInfoDao;

    private boolean displayProcessCount = false;

    public void setDisplayProcessCount(boolean displayProcessCount) {
        this.displayProcessCount = displayProcessCount;
    }

    private ItemClickListener itemClickListener = null;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public PicSectionListAdapter(Context context) {
        this.context = context;
        AppDataBase db = Room.databaseBuilder(context,
                AppDataBase.class, "database-flow1000").allowMainThreadQueries().build();
        picSectionDao = db.picSectionDao();
        picInfoDao = db.picInfoDao();
    }

    public void setDataArray(List<PicSectionData> dataArray) {
        this.dataArray = dataArray;
    }


    @NonNull
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
        viewHolder.deleteBtn.setVisibility(View.VISIBLE);
    }

    private void renderNonExistItem(final ViewHolder viewHolder) {
        viewHolder.textView.setTextColor(context.getColor(R.color.md_theme_light_onSurfaceVariant));
        viewHolder.itemView.setBackgroundColor(context.getColor(R.color.md_theme_light_surfaceVariant));
        viewHolder.deleteBtn.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        dataArray.get(position).setPosition(position);

        viewHolder.textView.setText(formatTitle(dataArray.get(viewHolder.getAdapterPosition()).getPicSectionBean().getName()));
        PicSectionBean.ClientStatus clientStatus = dataArray.get(viewHolder.getAdapterPosition()).getPicSectionBean().getClientStatus();
        if (clientStatus == PicSectionBean.ClientStatus.LOCAL
                && !displayProcessCount) {
            renderExistItem(viewHolder);
        } else {
            renderNonExistItem(viewHolder);
        }
        ProcessCounter.Counter counter = ProcessCounter.INSTANCE.getCounter(dataArray.get(position).getPicSectionBean().getId());
        if (counter != null) {
            if (displayProcessCount || clientStatus != PicSectionBean.ClientStatus.LOCAL) {
                viewHolder.process.setText("" + counter.getProcess() + "/" + counter.getMax());
                viewHolder.process.setVisibility(View.VISIBLE);
            } else {
                viewHolder.process.setVisibility(View.GONE);
            }
        } else {
            viewHolder.process.setVisibility(View.GONE);
        }

        viewHolder.serverIndex = dataArray.get(viewHolder.getAdapterPosition()).getPicSectionBean().getId();
        viewHolder.position = viewHolder.getAdapterPosition();
        viewHolder.deleteBtn.setOnClickListener(v -> {
            Log.d(TAG, "you clicked " + PicSectionListAdapter.this.dataArray.get(viewHolder.getAdapterPosition()).getPicSectionBean().getName() + " delete_btn");
            AlertDialog.Builder builder = new AlertDialog.Builder(PicSectionListAdapter.this.context);
            builder.setMessage("delete this dir?");
            builder.setTitle("");
            builder.setPositiveButton("yes", (dialog, which) -> {
                FileUtil.removeDir(PicSectionListAdapter.this.context, PicSectionListAdapter.this.dataArray.get(viewHolder.getAdapterPosition()).getPicSectionBean().getName());
                PicSectionBean picSectionData = dataArray.get(position).getPicSectionBean();

                picInfoDao.deleteBySectionInnerIndex(viewHolder.serverIndex);
                picSectionData.setExist(0);
                picSectionDao.update(picSectionData);

                DeleteSectionKt.postDeleteSection(viewHolder.serverIndex);
                dialog.dismiss();
                notifyDataSetChanged();
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

        public final TextView process;

        private final View itemView;

        public long serverIndex;

        public int position;

        private ViewHolder(View itemView) {

            super(itemView);
            this.textView = itemView.findViewById(R.id.pic_text_view);
            this.deleteBtn = itemView.findViewById(R.id.btn_delete);
            this.process = itemView.findViewById(R.id.process);
            this.itemView = itemView;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(dataArray.get(position));
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
