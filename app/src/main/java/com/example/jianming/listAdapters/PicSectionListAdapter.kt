package com.example.jianming.listAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.example.jianming.beans.PicSectionBean.ClientStatus
import com.example.jianming.beans.PicSectionData
import com.example.jianming.dao.PicInfoDao
import com.example.jianming.dao.PicSectionDao
import com.example.jianming.myapplication.R
import com.example.jianming.services.ProcessCounter.getCounter
import com.example.jianming.util.AppDataBase
import com.example.jianming.util.FileUtil.removeDir
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class PicSectionListAdapter(private val context: Context) :
    RecyclerView.Adapter<PicSectionListAdapter.ViewHolder>() {
    interface ItemClickListener {
        fun onItemClick(picSectionData: PicSectionData?)
    }

    private var dataArray: List<PicSectionData>? = null

    private val picSectionDao: PicSectionDao

    private val picInfoDao: PicInfoDao

    private var displayProcessCount = false

    fun setDisplayProcessCount(displayProcessCount: Boolean) {
        this.displayProcessCount = displayProcessCount
    }

    private var itemClickListener: ItemClickListener? = null

    fun setItemClickListener(itemClickListener: ItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    init {
        val db = databaseBuilder(
            context,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        picSectionDao = db.picSectionDao()
        picInfoDao = db.picInfoDao()
    }

    fun setDataArray(dataArray: List<PicSectionData>?) {
        this.dataArray = dataArray
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.pic_list_content, parent, false)

        val vh: ViewHolder = ViewHolder(v)
        v.tag = vh
        return vh
    }

    private fun renderExistItem(viewHolder: ViewHolder) {
        viewHolder.textView.setTextColor(context.getColor(R.color.md_theme_light_onPrimaryContainer))
        viewHolder.itemView.setBackgroundColor(context.getColor(R.color.md_theme_light_primaryContainer))
        viewHolder.deleteBtn.visibility = View.VISIBLE
    }

    private fun renderNonExistItem(viewHolder: ViewHolder) {
        viewHolder.textView.setTextColor(context.getColor(R.color.md_theme_light_onSurfaceVariant))
        viewHolder.itemView.setBackgroundColor(context.getColor(R.color.md_theme_light_surfaceVariant))
        viewHolder.deleteBtn.visibility = View.GONE
    }

    fun renderProcessCounter(viewHolder: ViewHolder, position: Int) {
        val counter = getCounter(
            dataArray!![position].picSectionBean.id
        )
        val clientStatus = dataArray!![viewHolder.adapterPosition].picSectionBean.clientStatus
        if (counter != null) {
            if (true) {
                viewHolder.process.text = "" + counter.getProcess() + "/" + counter.max
                viewHolder.process.visibility = View.VISIBLE
            } else {
                viewHolder.process.visibility = View.GONE
            }
        } else {
            viewHolder.process.visibility = View.GONE
        }
    }

    fun renderProcessFinish(viewHolder: ViewHolder, position: Int) {
        val counter = getCounter(
            dataArray!![position].picSectionBean.id
        )
        val clientStatus = dataArray!![viewHolder.adapterPosition].picSectionBean.clientStatus
        if (counter != null) {
            if (true) {
                Log.d(TAG, "set " + dataArray!![position].picSectionBean.name + " finish")
                viewHolder.process.text = "" + counter.max + "/" + counter.max
                viewHolder.process.visibility = View.VISIBLE
            } else {
                viewHolder.process.visibility = View.GONE
            }
        } else {
            viewHolder.process.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        dataArray!![position].position = position

        viewHolder.textView.text =
            formatTitle(dataArray!![viewHolder.adapterPosition].picSectionBean.name)
        val clientStatus = dataArray!![viewHolder.adapterPosition].picSectionBean.clientStatus
        if (clientStatus == ClientStatus.LOCAL
            && !displayProcessCount
        ) {
            renderExistItem(viewHolder)
        } else {
            renderNonExistItem(viewHolder)
        }
        renderProcessCounter(viewHolder, position)

        viewHolder.serverIndex = dataArray!![viewHolder.adapterPosition].picSectionBean.id
        viewHolder.position = viewHolder.adapterPosition
        viewHolder.deleteBtn.setOnClickListener { v: View? ->
            Log.d(
                TAG,
                "you clicked " + dataArray!![viewHolder.adapterPosition].picSectionBean.name + " delete_btn"
            )
            val builder =
                AlertDialog.Builder(this@PicSectionListAdapter.context)
            builder.setMessage("delete this dir?")
            builder.setTitle("")
            builder.setPositiveButton("yes") { dialog: DialogInterface, which: Int ->
                removeDir(
                    this@PicSectionListAdapter.context,
                    dataArray!![viewHolder.adapterPosition].picSectionBean.name
                )
                val picSectionData = dataArray!![position].picSectionBean

                picInfoDao.deleteBySectionInnerIndex(viewHolder.serverIndex)
                picSectionData.exist = 0
                picSectionDao.update(picSectionData)

                postDeleteSection(viewHolder.serverIndex)
                dialog.dismiss()
                notifyDataSetChanged()
            }
            builder.setNegativeButton(
                "no"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            builder.create().show()
        }
    }


    override fun getItemCount(): Int {
        return dataArray!!.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val deleteBtn: ImageView =
            itemView.findViewById(R.id.btn_delete)

        val textView: TextView =
            itemView.findViewById(R.id.pic_text_view)

        val process: TextView =
            itemView.findViewById(R.id.process)

        private val itemView: View = itemView

        var serverIndex: Long = 0

        var position: Int = 0

        init {

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (itemClickListener != null) {
                itemClickListener!!.onItemClick(dataArray!![position])
            }
        }
    }

    companion object {
        private const val TAG = "PicSectionListAdapter"
        private fun formatTitle(sourceTitle: String): String {
            if (sourceTitle.length > 14) {
                val timeStamp = sourceTitle.substring(0, 14)
                var isTimeStamp = true
                try {
                    SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE).parse(timeStamp)
                } catch (e: ParseException) {
                    isTimeStamp = false
                }
                if (isTimeStamp) {
                    return sourceTitle.substring(14)
                }
                return sourceTitle
            }
            return sourceTitle
        }
    }
}
