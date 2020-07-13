package com.example.jianming.beans;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Jianming on 2015/10/28.
 */
@Entity
public class UpdateStamp {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PrimaryKey
    private Long id;

    private String tableName;

    private String updateStamp;

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
