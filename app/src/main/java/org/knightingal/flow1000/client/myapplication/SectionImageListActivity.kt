package org.knightingal.flow1000.client.myapplication

import org.knightingal.flow1000.client.util.SERVER_IP
import org.knightingal.flow1000.client.util.SERVER_PORT
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.gson.Gson
import org.knightingal.flow1000.client.SectionDetail
import org.knightingal.flow1000.client.listAdapters.OnlineRecImgListAdapter
import org.knightingal.flow1000.client.util.AppDataBase
import org.knightingal.flow1000.client.listAdapters.RecImgListAdapter
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.knightingal.flow1000.client.R

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

    @SuppressLint("NotifyDataSetChanged")
    private fun initSectionImageList() {
        val sectionIndex = this.intent.getLongExtra("serverIndex", 0)
        val exist = this.intent.getIntExtra("exist", 0)
        if (exist == 1) {
            val db = Room.databaseBuilder(
                this,
                AppDataBase::class.java, "database-flow1000"
            ).allowMainThreadQueries().build()
            val picInfoBeanList = db.picInfoDao().queryBySectionInnerIndex(
                db.picSectionDao().getByServerIndex(
                    sectionIndex
                ).id
            )
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = RecImgListAdapter(this, picInfoBeanList)
        } else {
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@SectionImageListActivity)
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = OnlineRecImgListAdapter(this, null)

            val url = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picDetailAjax?id=$sectionIndex"
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    val client = HttpClient(CIO)
                    val response: HttpResponse = client.get(url)
                    val body: String = response.body()
//                    val mapper = jacksonObjectMapper()
                    try {
                        val sectionDetail = Gson().fromJson(body, SectionDetail::class.java)
                        withContext(Dispatchers.Main) {

                            val sectionConfig = getSectionConfig(sectionDetail.album)
                            (recyclerView.adapter as OnlineRecImgListAdapter).sectionDetail = sectionDetail
                            (recyclerView.adapter as OnlineRecImgListAdapter).sectionConfig = sectionConfig
                            (recyclerView.adapter as OnlineRecImgListAdapter).notifyDataSetChanged()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    fun startPicContentActivity(imgList: Array<String?>?, position: Int) {
        val intent = Intent(this, PicContentActivity::class.java)
        intent.putExtra("imgArray", imgList)
        intent.putExtra("position", position)
        launcher.launch(intent)
    }


}
