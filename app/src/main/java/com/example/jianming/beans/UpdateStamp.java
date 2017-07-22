package com.example.jianming.beans;

import android.util.Log;

import com.example.jianming.Utils.Daos;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;


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

    private String tableName;

    private String updateStamp;

    @Generated(hash = 1133476786)
    public UpdateStamp(String tableName, String updateStamp) {
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
