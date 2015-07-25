package com.scar.android;

/**
 * SCAR Session for holding information about the current
 * session
 */
public class Session {
    public static MetaData meta;
    public static byte[] password;

    public static void init(MetaData data, byte[] pas) {
        meta = data;
        password = pas;
    }

    public static void clear() {
        meta = null;
        password = null;
    }

    public static boolean valid() {
        return meta != null && meta.valid() && password != null;
    }
}
