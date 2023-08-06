package com.example.jianming.myapplication

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import androidx.room.Room.databaseBuilder
import com.example.jianming.Utils.AppDataBase
import com.example.jianming.beans.PicInfoBean
import com.example.jianming.listAdapters.ImgListAdapter

class AlbumContentActivity : ListActivity() {

    private var dirName: String? = null

    private var albumIndex: Long? = null

    private lateinit var picInfoBeanList: List<PicInfoBean>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dirName = this.intent.getStringExtra("name")
        albumIndex = this.intent.getLongExtra("serverIndex", 0)

        val db = databaseBuilder(
            this,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        picInfoBeanList = db.picInfoDao().queryByAlbumInnerIndex(
            db.picAlbumDao().getByServerIndex(
                albumIndex!!
            ).id
        )
        doShowListView()
    }


    private fun doShowListView() {
        val adapter = ImgListAdapter(this)
        adapter.dataArray = picInfoBeanList
        listAdapter = adapter
    }

    private val PicContentRequestCode = 1

    fun startPicContentActivity(imgs: Array<String?>?, position: Int) {
        val intent = Intent(this, PicContentActivity::class.java)
        intent.putExtra("imgArray", imgs)
        intent.putExtra("position", position)
        startActivityForResult(intent, PicContentRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PicContentRequestCode -> {
                    val position = data.getIntExtra("position", -1)
                    listView.setSelection(position)
                }

                else -> {}
            }
        }
    }
}