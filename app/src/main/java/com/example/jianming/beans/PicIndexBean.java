package com.example.jianming.beans;
import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.example.jianming.annotations.JsonName;

import java.util.List;

@Table(name = "T_ALBUM_INFO")
public class PicIndexBean extends Model{
    public PicIndexBean() {}

    public static List<PicIndexBean> getAll() {
        return new Select().
                from(PicIndexBean.class).
                orderBy(Cache.getTableInfo(PicIndexBean.class).getIdName()).
                execute();
    }

    public PicIndexBean(int index, String name) {
        this.index = index;
        this.name = name;
    }

    @JsonName("jsonName")
    @Column(name="Name")
    private String name;

    @JsonName("jsonIndex")
    @Column(name="server_index", index=true)
    private int index;

//    @JsonName("jsonMtime")
//    private String mtime;

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

//    public String getMtime() {
//        return mtime;
//    }
//
//    public void setMtime(String mtime) {
//        this.mtime = mtime;
//    }

    public void setName(String name) {
        this.name = name;
    }
}
