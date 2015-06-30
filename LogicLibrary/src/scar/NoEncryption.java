package scar;


//This is meant for testing purposes only
public class NoEncryption extends Encryption{
  public NoEncryption() { super(); }

  
  public byte[] encrypt(byte[] data, String stringOfKey) { return data; }
  public byte[] decrypt(byte[] cipherText, String stringOfKey) { return cipherText; }
}
