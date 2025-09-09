package org.knightingal.flow1000.client.listAdapters

import SERVER_IP
import SERVER_PORT
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import org.knightingal.flow1000.client.SectionDetail
import org.knightingal.flow1000.client.myapplication.SectionConfig
import org.knightingal.flow1000.client.myapplication.SectionImageListActivity
import org.knightingal.flow1000.client.util.Decrypt
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import org.knightingal.flow1000.client.R


class OnlineRecImgListAdapter(context: SectionImageListActivity, var sectionDetail: SectionDetail?) : RecyclerView.Adapter<OnlineRecImgListAdapter.ImgViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.R)
    private val screamWidth : Int = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .currentWindowMetrics.bounds.width()

    lateinit var sectionConfig:SectionConfig;


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
                val client = HttpClient(CIO)
                val response: HttpResponse = client.get(imgUrl)
                var content: ByteArray = response.body()

                if (sectionConfig.encrypted) {
                    content = Decrypt.decrypt(content)
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
        var imgName: String? = null
    }
}
