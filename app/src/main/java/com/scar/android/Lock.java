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

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS lock ("
                +"status INTEGER, "
                +"timeLocked INTEGER)");
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
        SQLiteStatement stmt = db.compileStatement("delete from lock");
        stmt.execute();
        stmt.close();

        stmt = db.compileStatement("INSERT INTO lock(status, timeLocked) VALUES (1, ?)");
        stmt.bindLong(1, new Date().getTime());
        stmt.executeInsert();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public boolean isLocked()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(db == null)
        {
            Log.d("fuck", "isLocked: DB IS NULL");
            return false;
        }
        Cursor cur = db.rawQuery("select status from lock", null);
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
            if(timeElapsed < 300000) //5 minutes
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
        Cursor cur = db.rawQuery("select timeLocked from lock", null);
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
        SQLiteStatement stmt = db.compileStatement("delete from lock");
        stmt.execute();
        stmt.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}