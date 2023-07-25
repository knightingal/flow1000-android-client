package org.nanjing.knightingal.processerlib

import org.nanjing.knightingal.processerlib.beans.CounterBean

interface RefreshListener {
    fun doRefreshView(position: Int, currCount: Int, max: Int)
}