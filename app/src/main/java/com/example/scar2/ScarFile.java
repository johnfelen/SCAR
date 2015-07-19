package com.example.scar2;

/* Class for holding information about a specific file stored/retrieved via SCAR
 *
 */
public class ScarFile {
    final String filename;
    final String localpath;

    public ScarFile(String fn, String lp) {
        filename = fn;
        localpath = lp;
    }
}
