package org.knightingal.flow1000.client.listAdapters

import org.knightingal.flow1000.client.beans.PicSectionData

fun interface ItemClickListener {
    fun onItemClick(picSectionData: PicSectionData)
}