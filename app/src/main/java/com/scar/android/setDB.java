package com.scar.android;

import android.app.Activity;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

/**
 * Created by Luke on 4/13/2016.
 */

public class setDB {
    private File dbName;
    private Activity activity;
    private String dbKey;
    private SQLiteDatabase db;
    public setDB(Activity act,File DBName,String key)
    {
        dbName=DBName;
        activity=act;
        dbKey=key;
    }
    public SQLiteDatabase getDb()
    {
        return SQLiteDatabase.openDatabase(dbName.getPath(), dbKey, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
