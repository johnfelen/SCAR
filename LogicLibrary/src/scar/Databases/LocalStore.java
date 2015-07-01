package scar;

import java.io.*;

/// For Testing: 
///  Stores our chunks locally to harddrive
///
public class LocalStore implements IServer {
  String folder;
  public LocalStore(String folder) {
    this.folder = folder;
  }

  public void storeData(String fn, byte[] data) {
    try {
      System.out.println("Storing: " + fn);
      File f = new File(folder+"/"+fn);
      f.createNewFile();
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(data);
      fos.flush();
      fos.close();
    }
    catch(Exception e) { e.printStackTrace(); }
  }

  public byte[] getData(String fn) {
    try {
      File f = new File(folder+"/"+fn);
      FileInputStream fis = new FileInputStream(f);
      byte[] data = new byte[(int)f.length()];
      fis.read(data);
      fis.close();
      return data;
    } catch (Exception e) { 
      System.out.println("Warning: Failed to retrieve: " + fn);
    }
    return null;
  }
}
