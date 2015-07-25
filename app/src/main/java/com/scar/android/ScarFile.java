package com.scar.android;

import java.util.ArrayList;

/* Class for holding information about a specific file stored/retrieved via SCAR
 *
 */
public class ScarFile {
    final int id;
    final String filename;
    final ArrayList<String> localpaths;

    public ScarFile(int i,String fn) {
        id = i;
        filename = fn;
        localpaths = new ArrayList<String>();
    }

    public void addLocal(String lp) {
        localpaths.add(lp);
    }
}
