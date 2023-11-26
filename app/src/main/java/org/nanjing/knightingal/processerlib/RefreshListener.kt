package org.nanjing.knightingal.processerlib

import com.example.jianming.beans.PicSectionBean


interface RefreshListener {
    fun doRefreshProcess(position: Int, currCount: Int, max: Int)

    fun doRefreshList(picSectionBeanList: List<PicSectionBean>)
}