package com.scar.android;

import android.app.Activity;
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


    public static void init(MetaData data, byte[] pas, Activity act)
    {
        meta = data;
        password = pas;
        metaBackground = new MetaDataB(act, "PublicDatabase");
    }

  /**
   * Makes a new lockout timer
   * @param con Context of App
   */
  public static void makeLock(Context con)
    {
        lock = new Lock(con, "locker");
    }


  /**
   * Locks the lockout timer
   */ 
    public static void setLocked()
    {
        lock.setLock();
    }

  /**
   * @return gives remaining lockout time left
   */
    public static long remaining()
    {
        return 300000 - lock.elapsed();
    }

  /**
   * @return checks if app is locked or not
   */
    public static boolean isLocked()
    {
        return lock.isLocked();
    }

  /**
   * @return gives number of login attempts
   */ 
    public static int getTries()
    {
        return lock.getTries();
    }

  /**
   * @param tries sets lockout tries to tries
   */
    public static void setTries(int tries)
    {
        lock.setTries(tries);
    }

  /**
   * Unlocks the app
   */
    public static void unlock()
    {
        lock.unlock();
    }
  /**
   * Clears the current session
   */
  public static void clear() {
        if(meta!=null)
            meta.close();
        meta = null;
        password = null;
    }

  /**
   * Checks if current session is valid
   */
    public static boolean valid() {
        return meta != null && meta.valid() && password != null;
    }
}
