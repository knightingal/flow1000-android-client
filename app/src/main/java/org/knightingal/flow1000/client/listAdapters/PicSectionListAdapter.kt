package org.knightingal.flow1000.client.listAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import org.knightingal.flow1000.client.services.ProcessCounter
import org.knightingal.flow1000.client.util.AppDataBase
import org.knightingal.flow1000.client.util.FileUtil
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.dao.PicInfoDao
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import org.knightingal.flow1000.client.R

fun interface ItemClickListener {
    fun onItemClick(picSectionData: PicSectionData)
}

class PicSectionListAdapter(
    private val context: Context?
) : RecyclerView.Adapter<PicSectionListAdapter.ViewHolder>() {


    companion object {
        private fun formatTitle(sourceTitle: String): String {
            if (sourceTitle.length > 14) {
                val timeStamp = sourceTitle.substring(0, 14)
                var isTimeStamp = true
                try {
                    SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE).parse(timeStamp)
                } catch (_: ParseException) {
                    isTimeStamp = false
                }
                return if (isTimeStamp) sourceTitle.substring(14) else sourceTitle
            }
            return sourceTitle
        }
        private const val TAG = "PicSectionListAdapter"
    }

    private var dataArray: List<PicSectionData> = emptyList()
    private val picSectionDao: PicSectionDao
    private val picInfoDao: PicInfoDao
    private var displayProcessCount: Boolean = false
    private lateinit var itemClickListener: ItemClickListener

    init {
        val db = Room.databaseBuilder(
            context as Context,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
    }

    fun setDisplayProcessCount(displayProcessCount: Boolean) {
        this.displayProcessCount = displayProcessCount
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setDataArray(dataArray: List<PicSectionData>) {
        this.dataArray = dataArray
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.pic_list_content, parent, false)
        val vh = ViewHolder(v)
        v.tag = vh
        return vh
    }

    private fun renderExistItem(viewHolder: ViewHolder) {
        viewHolder.textView.setTextColor(context!!.getColor(R.color.md_theme_light_onPrimaryContainer))
        viewHolder.itemView.setBackgroundColor(context.getColor(R.color.md_theme_light_primaryContainer))
        viewHolder.deleteBtn.visibility = View.VISIBLE
    }

    private fun renderNonExistItem(viewHolder: ViewHolder) {
        viewHolder.textView.setTextColor(context!!.getColor(R.color.md_theme_light_onSurfaceVariant))
        viewHolder.itemView.setBackgroundColor(context.getColor(R.color.md_theme_light_surfaceVariant))
        viewHolder.deleteBtn.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun renderProcessCounter(viewHolder: ViewHolder, position: Int) {

        val counter = ProcessCounter.getCounter(dataArray[position].picSectionBean.id)
        val clientStatus = dataArray[viewHolder.adapterPosition].picSectionBean.clientStatus
        if (counter != null) {
            viewHolder.process.text = "${counter.getProcess()}/${counter.max}"
            viewHolder.process.visibility = View.VISIBLE
        } else {
            viewHolder.process.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    fun renderProcessFinish(viewHolder: ViewHolder, position: Int) {
        val counter = ProcessCounter.getCounter(dataArray[position].picSectionBean.id)

        val clientStatus = dataArray[viewHolder.adapterPosition].picSectionBean.clientStatus
        if (counter != null) {
            Log.d(TAG, "set ${dataArray[position].picSectionBean.name} finish")
            viewHolder.process.text = "${counter.max}/${counter.max}"
            viewHolder.process.visibility = View.VISIBLE
        } else {
            viewHolder.process.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        dataArray[position].position = position

        viewHolder.textView.text = formatTitle(dataArray[viewHolder.adapterPosition].picSectionBean.name)
        val clientStatus = dataArray[viewHolder.adapterPosition].picSectionBean.clientStatus
        if (clientStatus == PicSectionBean.ClientStatus.LOCAL && !displayProcessCount) {
            renderExistItem(viewHolder)
        } else {
            renderNonExistItem(viewHolder)
        }
        renderProcessCounter(viewHolder, position)

        viewHolder.serverIndex = dataArray[viewHolder.adapterPosition].picSectionBean.id
        viewHolder.position1 = viewHolder.adapterPosition
        viewHolder.deleteBtn.setOnClickListener {
            Log.d(TAG, "you clicked ${dataArray[viewHolder.adapterPosition].picSectionBean.name} delete_btn")
            val builder = AlertDialog.Builder(context as Context)
            builder.setMessage("delete this dir?")
            builder.setTitle("")
            builder.setPositiveButton("yes") { dialog, _ ->
                FileUtil.removeDir(context, dataArray[viewHolder.adapterPosition].picSectionBean.name)
                val picSectionData = dataArray[position].picSectionBean

                picInfoDao.deleteBySectionInnerIndex(viewHolder.serverIndex)
                picSectionData.exist = 0
                picSectionDao.update(picSectionData)

                postDeleteSection(viewHolder.serverIndex)
                dialog.dismiss()
                notifyDataSetChanged()
            }
            builder.setNegativeButton("no") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }
    }

    override fun getItemCount(): Int = dataArray.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val deleteBtn: ImageView = itemView.findViewById(R.id.btn_delete)
        val textView: TextView = itemView.findViewById(R.id.pic_text_view)
        val process: TextView = itemView.findViewById(R.id.process)
        var serverIndex: Long = 0
        var position1: Int = 0

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(dataArray[position1])
        }
    }

}

