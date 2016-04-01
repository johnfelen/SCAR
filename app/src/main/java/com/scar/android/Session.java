package com.scar.android;

import android.util.Log;

import com.scar.android.Services.MetaDataB;

import java.util.Date;

/**
 * SCAR Session for holding information about the current
 * session
 */
public class Session {
    public static MetaData meta;
    public static MetaDataB metaBackground;
    public static byte[] password;
    public static boolean locked;
    public static long lockTime;

    public static void init(MetaData data, byte[] pas) {
        meta = data;
        password = pas;
    }

    public static void setLocked(){
        locked = true;
        lockTime = new Date().getTime();
    }

    public static void unlock(){
        locked = false;
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
