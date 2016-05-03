package com.scar.android;

import java.util.ArrayList;

/**
 * Class for holding information about a specific file stored/retrieved via SCAR
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

  /**
   * Add a local filepath for this scar file
   * @param lp filename for local path
   */
    public void addLocal(String lp) {
        localpaths.add(lp);
    }

  /**
   * @return the filename associated with this scar file
   */
    public String getFilename()
    {
        return filename;
    }


  /**
   * @return all local pathes known to the app
   */
    public ArrayList<String> getLocalpaths()
    {
        return localpaths;
    }
}
