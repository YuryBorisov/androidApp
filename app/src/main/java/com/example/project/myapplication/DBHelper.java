package com.example.project.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table items ("
                        + "id integer primary key autoincrement,"
                        + "name text,"
                        + "description text,"
                        + "base text,"
                        + "address text,"
                        + "latitude text,"
                        + "longitude text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
