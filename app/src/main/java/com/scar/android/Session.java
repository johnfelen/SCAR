package com.scar.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.scar.android.Services.MetaDataB;

import java.util.Date;

/**
 * SCAR Session for holding information about the current
 * session
 */
public class Session
{
    public static MetaData meta;
    public static MetaDataB metaBackground;
    public static byte[] password;
    public static Lock lock;


    public static void init(MetaData data, byte[] pas)
    {
        meta = data;
        password = pas;
    }

    public static void makeLock(Context con)
    {
        lock = new Lock(con, "locker");
    }

    public static void setLocked(){
        lock.setLock();
    }

    public static long remaining()
    {
        return 300000 - lock.elapsed();
    }

    public static boolean isLocked()
    {
        return lock.isLocked();
    }

    public static void unlock()
    {
        lock.unlock();
    }
    public static void clear() {
        if(meta!=null)
            meta.close();
        meta = null;
        password = null;
    }

    public static boolean valid()
    {
        return meta != null && meta.valid() && password != null;
    }
}
