package org.nanjing.knightingal.processerlib

import org.knightingal.flow1000.client.beans.PicSectionBean


interface RefreshListener {
    fun doRefreshProcess(sectionId: Long,  finish: Boolean)

    fun doRefreshList(picSectionBeanList: List<PicSectionBean>)

    fun notifyListReady()
}