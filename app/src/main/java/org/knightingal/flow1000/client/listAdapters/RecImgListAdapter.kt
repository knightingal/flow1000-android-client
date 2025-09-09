package org.knightingal.flow1000.client.listAdapters

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import org.knightingal.flow1000.client.beans.PicInfoBean
import org.knightingal.flow1000.client.myapplication.SectionImageListActivity
import org.knightingal.flow1000.client.R
import com.google.common.io.Files
import java.io.File
import java.io.IOException

class RecImgListAdapter(private val context: SectionImageListActivity, private val dataArray: List<PicInfoBean>) : RecyclerView.Adapter<RecImgListAdapter.ImgViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.R)
    private val screamWidth : Int = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .currentWindowMetrics.bounds.width()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {

        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.vlist, parent, false)

        return ImgViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataArray.size
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {
        val width = dataArray[position].width
        val height = dataArray[position].height

        val div = height.toFloat() / width.toFloat()
        val lp = holder.img.layoutParams

        lp.height = (div * screamWidth.toFloat()).toInt()
        lp.width = screamWidth
        holder.img.layoutParams = lp

        val file = File(dataArray[position].absolutePath as String)

        try {
            val enCryptedContent = Files.toByteArray(file)
            holder.img.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    enCryptedContent,
                    0,
                    enCryptedContent.size
                )
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        holder.img.setOnClickListener {
            Log.d("Activity4List", dataArray[position].absolutePath!!)
            val imgList = arrayOfNulls<String>(dataArray.size)
            for (i in dataArray.indices) {
                imgList[i] = dataArray[i].absolutePath
            }
            context.startPicContentActivity(imgList, position)
        }

    }
    class ImgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.img)
    }
}