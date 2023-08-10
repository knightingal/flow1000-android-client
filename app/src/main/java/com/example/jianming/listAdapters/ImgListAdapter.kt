package com.example.jianming.listAdapters

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.jianming.Utils.Decryptor
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.myapplication.AlbumContentActivity
import com.example.jianming.myapplication.R
import com.google.common.io.Files
import java.io.File
import java.io.IOException

class ImgListAdapter constructor(private val context: AlbumContentActivity) : BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    @RequiresApi(Build.VERSION_CODES.R)
    private val screamWidth : Int = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .currentWindowMetrics.bounds.width()

    var dataArray: List<PicInfoBean>  = listOf()

    override fun getCount(): Int = dataArray.size


    override fun getItem(position: Int): PicInfoBean {
        return dataArray[position]
    }

    override fun getItemId(position: Int): Long {
        return dataArray[position].index as Long
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {

        val holder: ViewHolder
        val convertView: View
        if (view == null) {
            holder = ViewHolder()
            convertView = mInflater.inflate(R.layout.vlist, parent, false)
            holder.img = convertView.findViewById(R.id.img)
            convertView.tag = holder
        } else {
            convertView = view
            holder = convertView.tag as ViewHolder
        }

        val width = dataArray[position].width
        val height = dataArray[position].height

        val div = height.toFloat() / width.toFloat()

        val lp = holder.img!!.layoutParams

        lp.height = (div * screamWidth.toFloat()).toInt()
        lp.width = screamWidth

        holder.img!!.layoutParams = lp
        val file = dataArray[position].absolutePath?.let { File(it) }


        try {
            val enCryptedContent = Files.toByteArray(file)
            holder.img!!.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    enCryptedContent,
                    0,
                    enCryptedContent.size
                )
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        holder.img!!.setOnClickListener {
            Log.d("Activity4List", dataArray[position].absolutePath!!)
            val imgs = arrayOfNulls<String>(dataArray.size)
            for (i in dataArray.indices) {
                imgs[i] = dataArray[i].absolutePath
            }
            context.startPicContentActivity(imgs, position)
        }

        return convertView

    }

    public final class ViewHolder {
        var img: ImageView? = null
    }
}