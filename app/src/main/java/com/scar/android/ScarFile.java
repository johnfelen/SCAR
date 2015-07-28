package com.scar.android;

import java.util.ArrayList;

/* Class for holding information about a specific file stored/retrieved via SCAR
 *
 */
public class ScarFile {
    final public int id;
    final public String filename;
    final public ArrayList<String> localpaths;

    public ScarFile(int i,String fn) {
        id = i;
        filename = fn;
        localpaths = new ArrayList<String>();
    }

    public void addLocal(String lp) {
        localpaths.add(lp);
    }

    public String getFilename()
    {
        return filename;
    }

    public ArrayList<String> getLocalpaths()
    {
        return localpaths;
    }
}
