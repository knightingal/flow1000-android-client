package com.example.jianming.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jianming.Utils.DIOptionsExactly;
import com.example.jianming.myapplication.R;
import com.example.jianming.myapplication.XrxActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.List;
import java.util.Map;


public class PicListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    private List<Map<String, Object>> dataArray;

    Context context;

    public PicListAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setDataArray(List<Map<String, Object>> dataArray) {
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
            //holder.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imgUrl = ImageDownloader.Scheme.FILE.wrap((String) dataArray.get(position).get("img"));

        ImageLoader.getInstance().displayImage(imgUrl, holder.img, DIOptionsExactly.getInstance().getOptions());
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Activity4List", (String) dataArray.get(position).get("img"));
                String imgs[] = new String[dataArray.size()];
                for (int i = 0; i < dataArray.size(); i++) {
                    imgs[i] = (String) dataArray.get(i).get("img");
                }
                Intent intent = new Intent(context, XrxActivity.class);
//                intent.putExtra("imgUrls", ImageDownloader.Scheme.FILE.wrap((String) dataArray.get(position).get("img")));
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
