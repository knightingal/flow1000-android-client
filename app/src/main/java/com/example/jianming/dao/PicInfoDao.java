package com.example.jianming.dao;


import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicInfoBean;

import java.util.List;


//@Dao
public interface PicInfoDao {

//    @Insert
    void insert(PicInfoBean picInfoBean);

//    @Query("select * from PicInfoBean where albumIndex = :innerIndex")
    List<PicInfoBean> queryByAlbumInnerIndex(Long innerIndex);

//    @Update
    void update(PicInfoBean picInfoBean);
}
