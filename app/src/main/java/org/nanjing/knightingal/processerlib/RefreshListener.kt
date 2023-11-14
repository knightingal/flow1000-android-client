package org.nanjing.knightingal.processerlib


interface RefreshListener {
    fun doRefreshView(position: Int, currCount: Int, max: Int)
}