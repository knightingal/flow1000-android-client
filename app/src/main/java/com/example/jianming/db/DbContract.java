package com.example.jianming.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.jianming.beans.PicIndexBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jianming on 2015/10/23.
 */
public class DbContract {
    public DbContract() {}

    public static abstract class DbEntry implements BaseColumns {
        public static final String TABLE_NAME = "T_ALBUM_INFO";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String IS_EXIST = "is_exist";
    }

    private static final String TEXT_TYPE = " text";
    private static final String COMMA_SEP = ",";
    private static final String INTEGER_TYPE = " integer";

    private static final String SQL_CREATE_ENTRIES =
            "create table " + DbEntry.TABLE_NAME + " (" +
            DbEntry.ID + INTEGER_TYPE + " primary key, " +
            DbEntry.NAME + TEXT_TYPE + COMMA_SEP +
            DbEntry.IS_EXIST + INTEGER_TYPE +
            ")";

    private static final String SQL_DELETE_ENTRIES =
            "drop table if exists " + DbEntry.TABLE_NAME;


    public static class DbHelper extends SQLiteOpenHelper {
        public static final int DATEBASE_VERSION = 1;
        public static final String DATEBASE_NAME = "yStart.db";

        public DbHelper(Context context) {
            super(context, DATEBASE_NAME, null, DATEBASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }


    }

    static DbHelper mDbHelper;

    public static void access(Context context) {
        mDbHelper = new DbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(DbEntry.TABLE_NAME, null, null);
    }

    public static long writeAblum(int id, String name, int isExist) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbEntry.ID, id);
        values.put(DbEntry.NAME, name);
        values.put(DbEntry.IS_EXIST, isExist);

        long newRowId;
        newRowId = db.insert(
                DbEntry.TABLE_NAME,
                null,
                values
        );
//        Log.d("DbContract", "insert " + name + " rowid = " + newRowId);
        return newRowId;
    }

    public static List<PicIndexBean> query() {
        List<PicIndexBean> picIndexBeans = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] columns = {DbEntry.ID, DbEntry.NAME};
        Cursor c = db.query(
                DbEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null
        );

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex(DbEntry.ID));
            String name = c.getString(c.getColumnIndex(DbEntry.NAME));
            picIndexBeans.add(new PicIndexBean(id, name));

        }

        return picIndexBeans;
    }

}
