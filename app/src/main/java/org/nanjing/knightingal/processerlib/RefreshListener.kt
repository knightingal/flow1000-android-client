package org.nanjing.knightingal.processerlib

import com.example.jianming.beans.PicSectionBean
import com.example.jianming.beans.PicSectionData


interface RefreshListener {
    fun doRefreshProcess(sectionId: Long, position: Int, currCount: Int, max: Int)

    fun doRefreshList(picSectionBeanList: List<PicSectionData>)

    fun notifyListReady()
}