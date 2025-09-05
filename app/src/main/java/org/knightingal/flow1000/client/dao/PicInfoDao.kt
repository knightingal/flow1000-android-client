package org.knightingal.flow1000.client.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import org.knightingal.flow1000.client.beans.PicInfoBean



@Dao
interface PicInfoDao {

    @Insert
    fun insert(picInfoBean: PicInfoBean)

    @Query("select * from PicInfoBean where `index` = :index")
    fun query(index: Long): PicInfoBean

    @Query("select * from PicInfoBean where sectionIndex = :innerIndex")
    fun queryBySectionInnerIndex(innerIndex: Long):List<PicInfoBean>

    @Query("delete from PicInfoBean where sectionIndex = :innerIndex")
    fun deleteBySectionInnerIndex(innerIndex: Long)

    @Update
    fun update(picInfoBean: PicInfoBean)
}
