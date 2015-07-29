package com.scar.android.ServerImpl;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;

/*
 * Server Implementation for a local sqlite db
 *  Hostname -> db file name
 *  ignore: port, uname, pass
 */
public class SQLiteStore implements scar.IServer{
    private final SQLiteDatabase db;

    public SQLiteStore(Activity act, String file) {
        db = SQLiteDatabase.openOrCreateDatabase(act.getDatabasePath(file), null);
        //create tables if needed
        db.execSQL("CREATE TABLE IF NOT EXISTS files (name TEXT,data BLOB,PRIMARY KEY(name))");
    }

    public void close() {
        db.close();
    }

    public boolean getStatus() { return true; }

    public void storeData(String fn, byte[] data) {
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("insert into files values (?, ?)");
        stmt.bindString(1, fn);
        stmt.bindBlob(2, data);
        stmt.executeInsert();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public byte[] getData(String fn) {
        byte[] ret = null;
        db.beginTransaction();
        Cursor cur = db.rawQuery("select data from files where name = ?", new String[] { fn });
        cur.moveToFirst();
        if(cur.getCount() > 0)
            ret = cur.getBlob(cur.getColumnIndex("data"));
        cur.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }
}
