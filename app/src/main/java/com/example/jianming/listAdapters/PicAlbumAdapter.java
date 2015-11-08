package com.example.jianming.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jianming.Utils.DIOptionsExactly;
import com.example.jianming.beans.PicInfoBean;
import com.example.jianming.myapplication.R;
import com.example.jianming.myapplication.XrxActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.List;
import java.util.Map;


public class PicAlbumAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    private List<PicInfoBean> dataArray;

    Context context;

    int sreamWidth;

    public PicAlbumAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.sreamWidth = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }

    public void setDataArray(List<PicInfoBean> dataArray) {
        this.dataArray = dataArray;
    }


    @Override
    public int getCount() {
        return dataArray.size();
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.vlist, parent, false);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imgUrl = ImageDownloader.Scheme.FILE.wrap((String) dataArray.get(position).getAbsolutePath());
        int width = dataArray.get(position).getWidth();
        int height = dataArray.get(position).getHeight();

        float div = (float)height / (float)width;

        ViewGroup.LayoutParams lp = holder.img.getLayoutParams();
        // sreamHeight  = height / width * sreamWidth

        lp.height = (int)(div * (float)sreamWidth);
        lp.width = sreamWidth;

        holder.img.setLayoutParams(lp);
        ImageLoader.getInstance().displayImage(imgUrl, holder.img, DIOptionsExactly.getInstance().getOptions());
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Activity4List", (String) dataArray.get(position).getAbsolutePath());
                String imgs[] = new String[dataArray.size()];
                for (int i = 0; i < dataArray.size(); i++) {
                    imgs[i] = (String) dataArray.get(i).getAbsolutePath();
                }
                Intent intent = new Intent(context, XrxActivity.class);
                intent.putExtra("imgs", imgs);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });

        //holder.info.setText((String) dataArray.get(position).get("info"));
        return convertView;
    }

    public final class ViewHolder {
        public ImageView img;
    }
}
