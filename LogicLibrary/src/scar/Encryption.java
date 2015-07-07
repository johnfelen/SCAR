package scar;

import java.lang.reflect.*;

public class Encryption {
  //Use this to get an object instance of the encryption class 
  //used in the implementation
  public static Encryption getInstance() {
    return new Encryption();
  }

  public Encryption() {} 
  
  public byte[] encrypt(byte[] data, String stringOfKey) { return null; }
  public byte[] decrypt(byte[] cipherText, String stringOfKey) { return null; }
}
