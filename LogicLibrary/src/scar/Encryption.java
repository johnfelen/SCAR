package scar;

import java.lang.reflect.*;

public class Encryption {
  //Use this to get an object instance of the encryption class 
  //used in the implementation
  public static Encryption getInstance() {
    return new Encryption();
  }

  public Encryption() {} 


  //key => 32 bytes
  public byte[] encrypt(byte[] data, byte[] key) { return null; }
  public byte[] decrypt(byte[] cipherText, byte[] key) { return null; }
}
