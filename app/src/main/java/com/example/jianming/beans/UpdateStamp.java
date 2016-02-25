package com.example.jianming.beans;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by Jianming on 2015/10/28.
 */
@Table(name = "T_UPDATE_STAMP")
public class UpdateStamp extends Model {

    public static UpdateStamp getUpdateStampByTableName(String tableName) {
        UpdateStamp updateStamp =
                new Select().from(UpdateStamp.class).
                        where("table_name = ?", tableName).
                        executeSingle();
        Log.i("UpdataStamp", "updateStamp: " + updateStamp);
        return updateStamp;
    }

    @Column(name="table_name")
    private String tableName;

    @Column(name="update_stamp")
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
