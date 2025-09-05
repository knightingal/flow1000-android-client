package org.knightingal.flow1000.client.beans

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Jianming on 2015/10/31.
 */
@Entity
data class PicInfoBean (
    @PrimaryKey
    var index: Long? = null,
    var name: String,
    var sectionIndex: Long,
    var absolutePath: String? = null,
    var height: Int = 0,
    var width: Int = 0
)
