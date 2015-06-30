package scar.pc;

import java.io.*;

public class SimpleDemo {
  public static void get(String path, String fn, String password, int k, int n) {
    scar.IServer servers[] = { new scar.LocalStore("encoded") };
    scar.GetFile gf = new scar.GetFile(fn, password, k, n, servers);

    byte[] data = gf.get(); //data => file bytes
    //Make a new file with filename 'path'
    //Store the data bytes via an FileOutputStream.write for every byte into the file
    try {
      File file = new File(path);
      file.createNewFile();
      FileOutputStream fout = new FileOutputStream(file);
      fout.write(data);
      fout.flush();
      fout.close();
    } catch(Exception e) {
      e.printStackTrace();
      System.out.println("Failed to save file: " + path);
    }
  }

  public static void store(String path, String fn, String password, int k, int n) {
    byte[] bArr = null;
    try {
      File file = new File(path);
      FileInputStream fin = new FileInputStream(file);
      int filesize = (int)file.length();
      bArr = new byte[filesize];
      
      //Read in the file bArr[0...len] = fin.read();
      fin.read(bArr);
      fin.close();
    } catch(Exception e) {
      e.printStackTrace();
      System.out.println("Can't load file: " + fn);
    }
      
    scar.IServer servers[] = { new scar.LocalStore("encoded") };
    scar.StoreFile sf = new scar.StoreFile(bArr, fn, password, k, n, servers);
    sf.store();
  }
  
  //Usage: SimpleDemo -get STORE-PATH FILENAME PASSWORD K N
  //                  -store FILE-PATH FILENAME PASSWORD K N
  public static void main(String args[]) {

    String path = args[1];
    String fn = args[2];
    String password = args[3];
    int k = Integer.parseInt(args[4]);
    int n = Integer.parseInt(args[5]);
    
    scar.Encryption.setEncryption(PCEncrypt.class);

    if(args[0].equals("-store")) {
      store(path, fn, password, k, n);
    }
    else if(args[0].equals("-get")) {
      get(path, fn, password, k, n);
    }
    else {
      //invalid usage
      System.out.println("Usage: java -jar scar-pc.jar -get STORE-FILE FILENAME PASSWORD K N");
      System.out.println("                             -store FILE-PATH FILENAME PASSWORD K N");
    }
  }
}
