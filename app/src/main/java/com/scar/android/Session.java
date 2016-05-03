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

  /**
   * Clears the current session
   */
    public static void clear() {
        //meta.close();
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
