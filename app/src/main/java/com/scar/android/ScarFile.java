package com.scar.android;

/* Class for holding information about a specific file stored/retrieved via SCAR
 *
 */
public class ScarFile {
    final int id;
    final String filename;
    final String localpath;

    public ScarFile(int i,String fn, String lp) {
        id = i;
        filename = fn;
        localpath = lp;
    }
}
