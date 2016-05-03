package scar;


//This is meant for testing purposes only

/**
 * Encryption method if no encryption on the file is desired
 * The methods here make no changes to the input data
 */
public class NoEncryption extends Encryption {
  public NoEncryption() { super(); }

  
  public byte[] encrypt(byte[] data, String stringOfKey) {
    return data;
  }

  public byte[] decrypt(byte[] cipherText, String stringOfKey) {
    return cipherText;
  }
}
