package scar;

import java.io.*;

/// For Testing: 
///  Stores our chunks locally to harddrive
///
public class LocalStore implements IServer {
  String folder;
  final int id;
  public LocalStore(String folder, int id) {
    this.folder = folder;
    this.id = id;
  }

  public boolean getStatus() { return true; }
  public int id() { return id; }

  public void storeData(String fn, byte[] data) {
    try {
      File f = new File(folder+"/"+fn);
      f.createNewFile();
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(data);
      fos.flush();
      fos.close();
    }
    catch(Exception e) { e.printStackTrace(); }
  }

  public void close() {} 
  
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
