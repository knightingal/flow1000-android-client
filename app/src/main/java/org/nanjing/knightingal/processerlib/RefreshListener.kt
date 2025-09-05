package org.nanjing.knightingal.processerlib

import org.knightingal.flow1000.client.beans.PicSectionData


interface RefreshListener {
    fun doRefreshProcess(sectionId: Long,  finish: Boolean)

    fun doRefreshList(picSectionBeanList: List<PicSectionData>)

    fun notifyListReady()
}