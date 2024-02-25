package com.example.jianming.listAdapters

import SERVER_IP
import SERVER_PORT
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
import com.example.jianming.SectionDetail
import com.example.jianming.myapplication.R
import com.example.jianming.myapplication.SectionConfig
import com.example.jianming.myapplication.SectionImageListActivity
import com.example.jianming.util.Decryptor
import com.example.jianming.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request

class OnlineRecImgListAdapter(private val context: SectionImageListActivity, public var sectionDetail: SectionDetail?) : RecyclerView.Adapter<OnlineRecImgListAdapter.ImgViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.R)
    private val screamWidth : Int = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .currentWindowMetrics.bounds.width()

    public lateinit var sectionConfig:SectionConfig;


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {

        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.vlist, parent, false)

        return ImgViewHolder(v)
    }

    override fun getItemCount(): Int {
        return sectionDetail?.pics?.size ?: 0
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {
        val width = sectionDetail!!.pics[position].width
        val height = sectionDetail!!.pics[position].height
        val imgName = sectionDetail!!.pics[position].name
        holder.imgName = imgName

        val div = height.toFloat() / width.toFloat()
        val lp = holder.imgView.layoutParams

        lp.height = (div * screamWidth.toFloat()).toInt()
        lp.width = screamWidth
        holder.imgView.layoutParams = lp

        val imgUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
                "/linux1000/${sectionConfig.baseUrl}/${sectionDetail!!.dirName}/${sectionDetail!!.pics[position].name}"

        MainScope().launch {
            withContext(Dispatchers.IO) {
                val request = Request.Builder().url(imgUrl).build()
                var content = NetworkUtil.okHttpClient.newCall(request).execute().body.bytes()
                if (sectionConfig.encryped) {
                    content = Decryptor.decrypt(content)
                }
                if (holder.imgName.equals(imgName)) {
                    withContext(Dispatchers.Main) {

                        holder.imgView.setImageBitmap(
                            BitmapFactory.decodeByteArray(
                                content,
                                0,
                                content.size
                            )
                        )
                    }
                }
            }
        }

    }
    class ImgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView: ImageView = itemView.findViewById(R.id.img)

        var imgName: String?
        init {
            imgName = null
        }

    }
}
