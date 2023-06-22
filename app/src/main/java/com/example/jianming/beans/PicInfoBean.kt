package com.example.jianming.beans


/**
 * Created by Jianming on 2015/10/31.
 */
data class PicInfoBean (
    var index: Long? = null,
    var name: String,
    var albumIndex: Long,
    var absolutePath: String? = null,
    var height: Int = 0,
    var width: Int = 0
)
