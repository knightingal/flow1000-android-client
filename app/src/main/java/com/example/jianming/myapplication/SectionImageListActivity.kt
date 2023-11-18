package com.example.jianming.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.jianming.util.AppDataBase
import com.example.jianming.listAdapters.RecImgListAdapter

class SectionImageListActivity : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView

    private lateinit var launcher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pic_section_list_activity_md)
        recyclerView = findViewById(R.id.list_view11)
        recyclerView.setHasFixedSize(true)


        launcher = registerForActivityResult(
            object: ActivityResultContract<Intent, Int>() {
                override fun createIntent(context: Context, input: Intent): Intent {
                    return input
                }

                override fun parseResult(resultCode: Int, intent: Intent?): Int {
                    return intent?.getIntExtra("position", -1) as Int
                }
            }
        ) {
            recyclerView.scrollToPosition(it as Int)
        }

        initSectionImageList()


    }

    private fun initSectionImageList() {
        val sectionIndex = this.intent.getLongExtra("serverIndex", 0)

        val db = Room.databaseBuilder(
            this,
            AppDataBase::class.java, "database-flow1000"
        ).allowMainThreadQueries().build()
        val picInfoBeanList = db.picInfoDao().queryByAlbumInnerIndex(
            db.picSectionDao().getByServerIndex(
                sectionIndex
            ).id
        )
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = RecImgListAdapter(this, picInfoBeanList)
    }

    fun startPicContentActivity(imgs: Array<String?>?, position: Int) {
        val intent = Intent(this, PicContentActivity::class.java)
        intent.putExtra("imgArray", imgs)
        intent.putExtra("position", position)
        launcher.launch(intent)
    }


}
