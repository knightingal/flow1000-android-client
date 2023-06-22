package com.example.jianming.beans

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Jianming on 2015/10/28.
 */
@Entity
data class UpdateStamp (

    @PrimaryKey
    var id: Long? = null,

    @ColumnInfo(name="table_name")
    var tableName: String? = null,

    @ColumnInfo(name="update_stamp")
    var updateStamp: String? = null
)
