package scar;

import java.lang.reflect.*;

public class Encryption {
  private static Constructor<Encryption> factory; 
  
  //Set the encryption class to be used in the implementation
  public static void setEncryption(Class<? extends Encryption> eclass) {
    try {
      factory = (Constructor<Encryption>)eclass.getConstructor();
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  
  //Use this to get an object instance of the encryption class 
  //used in the implementation
  public static Encryption getInstance() {
    if(factory != null) {
      try {
        return factory.newInstance();
      } catch(Exception e) {
        e.printStackTrace();
        return null;
      }
    }
    return null;
  }

  public Encryption() {} 
  
  public byte[] encrypt(byte[] data, String stringOfKey) { return null; }
  public byte[] decrypt(byte[] cipherText, String stringOfKey) { return null; }
}
