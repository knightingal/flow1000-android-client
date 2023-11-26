package org.nanjing.knightingal.processerlib


interface RefreshListener {
    fun doRefreshProcess(position: Int, currCount: Int, max: Int)

    fun doRefreshList()
}