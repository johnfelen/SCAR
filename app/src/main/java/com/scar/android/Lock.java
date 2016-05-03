package com.scar.android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.Date;

/**
 * Created by Spencer on 4/6/2016.
 */
public class Lock extends SQLiteOpenHelper {

    private final String dbname;
    public final int timer=300000;

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS locks ("
                + "status INTEGER, "
                + "tries INTEGER,"
                + "timeLocked INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Lock(Context con, String dbnm) {
        super(con, dbnm, null, 1);
        dbname = dbnm;
    }

    public void setLock()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("delete from locks");
        stmt.execute();
        stmt.close();

        stmt = db.compileStatement("INSERT INTO locks(status, timeLocked, tries) VALUES (1, ?, ?)");
        stmt.bindLong(1, new Date().getTime());
        stmt.bindLong(2, 0);
        stmt.executeInsert();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void setTries(int tries)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("delete from locks");
        stmt.execute();
        stmt.close();

        stmt = db.compileStatement("INSERT INTO locks(status, tries) VALUES (1, ?)");
        stmt.bindLong(1, tries);
        stmt.execute();
        stmt.close();

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public int getTries()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        db = this.getWritableDatabase();

        Cursor cur = db.rawQuery("select tries from locks", null);
        if(cur == null || cur.getCount() == 0)
        {
            return 0;
        }

        cur.moveToFirst();
        int tries = cur.getInt(cur.getColumnIndex("tries"));
        cur.close();
        return tries;
    }

    public boolean isLocked()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(db == null)
        {
            return false;
        }
        Cursor cur = db.rawQuery("select status from locks", null);
        if(cur == null || cur.getCount() == 0)
        {
            return false;
        }

        cur.moveToFirst();
        int status = cur.getInt(cur.getColumnIndex("status"));
        if(status == 0)
        {
            return false;
        }
        else
        {
            long timeElapsed = elapsed();
            if(timeElapsed < timer) //5 minutes
            {
                return true;
            }
            else
            {
                unlock();
            }
        }

        return false;
    }

    public long elapsed()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("select timeLocked from locks", null);
        if(cur == null)
        {
            return 0;
        }
        cur.moveToFirst();

        long current = new Date().getTime();
        return current - cur.getLong(cur.getColumnIndex("timeLocked"));
    }

    public void unlock()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement("delete from locks");
        stmt.execute();
        stmt.close();

        stmt = db.compileStatement("INSERT INTO locks(status, tries) VALUES(1, 0)");
        stmt.execute();
        stmt.close();

        db.setTransactionSuccessful();
        db.endTransaction();
    }
}