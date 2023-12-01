package org.nanjing.knightingal.processerlib

import com.example.jianming.beans.PicSectionBean


interface RefreshListener {
    fun doRefreshProcess(sectionId: Long, position: Int, currCount: Int, max: Int)

    fun doRefreshList(picSectionBeanList: List<PicSectionBean>)

    fun notifyListReady()
}