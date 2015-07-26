package com.scar.android.ServerImpl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/*
 * Server Implementation for a local sqlite db
 *  Hostname -> db file name
 *  ignore: port, uname, pass
 */
public class SQLiteStore implements scar.IServer{
    private final SQLiteDatabase db;

    public SQLiteStore(String file) {
        db = SQLiteDatabase.openOrCreateDatabase(file, null);
        //create tables if needed
        db.execSQL("CREATE TABLE IF NOT EXISTS files (name TEXT,data BLOB,PRIMARY KEY(name))");
    }

    public boolean getStatus() { return true; }

    public void storeData(String fn, byte[] data) {
        SQLiteStatement stmt = db.compileStatement("insert into files (?, ?)");
        stmt.bindString(1, fn);
        stmt.bindBlob(2, data);
        stmt.executeInsert();
    }

    public byte[] getData(String fn) {
        Cursor cur = db.rawQuery("select data from files where name = ?", new String[] { fn });
        cur.moveToFirst();
        if(cur.getCount() > 0)
            return cur.getBlob(cur.getColumnIndex("data"));
        return null;
    }
}
