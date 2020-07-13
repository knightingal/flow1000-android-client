package com.example.jianming.beans

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Jianming on 2015/10/28.
 */
@Entity
data class UpdateStamp (
    @PrimaryKey
    var id: Long? = null,
    var tableName: String? = null,
    var updateStamp: String? = null
)
