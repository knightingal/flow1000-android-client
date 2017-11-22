package com.example.jianming.beans;

import android.util.Log;

import com.example.jianming.Utils.Daos;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


/**
 * Created by Jianming on 2015/10/28.
 */
@Entity
public class UpdateStamp {

    public static UpdateStamp getUpdateStampByTableName(String tableName) {
        return Daos.updateStampDao.queryBuilder()
                .where(UpdateStampDao.Properties.TableName.eq(tableName))
                .build().unique();
    }

    public void save() {
        Daos.updateStampDao.insert(this);
    }

    public void update() {
        Daos.updateStampDao.update(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    private Long id;

    private String tableName;

    private String updateStamp;

    @Generated(hash = 1659781237)
    public UpdateStamp(Long id, String tableName, String updateStamp) {
        this.id = id;
        this.tableName = tableName;
        this.updateStamp = updateStamp;
    }

    @Generated(hash = 426198667)
    public UpdateStamp() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUpdateStamp() {
        return updateStamp;
    }

    public void setUpdateStamp(String updateStamp) {
        this.updateStamp = updateStamp;
    }
}
