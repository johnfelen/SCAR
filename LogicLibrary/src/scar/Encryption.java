package scar;

import java.lang.reflect.*;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.AEADParameters;

/**
 * Encryption handles both the encryption and decryption of files in the SCAR system
 * this is done via Spongy Castle using AES-GCM 256
 */
public class Encryption
{
  /**
   * IV length in bytes
   */
  private static final int IV_LEN = 16;
  /**
   * MAC length in bytes
   */
  private static final int MAC_SIZE = 16;

  /**
   * encrypts the given plainText with our key via AES-GCM
   * key = 32 bytes
   * Output Format:
   *  _________________
   * | 16-byte IV      |
   * |-----------------|
   * | encrypted data  |
   * |-----------------|
   * | 16-byte MAC     |
   * |_________________|
   * @param plainText  data to be encrypted
   * @param key  the key for this encryption
   * @return Binary data in the format specified above
   */
  public byte[] encrypt( byte[] plainText, byte[] key )
  //wrapper function to encrypt
  {
    //pad the data
    byte[] paddedData = pad( plainText );
    
    //generate the IV
    RndKeyGen keygen = new RndKeyGen();
    keygen.seed(System.currentTimeMillis());
    byte[] IV = keygen.genBytes(IV_LEN);

    //Encrypt
    byte[] cipherText = performCrypto(paddedData, key, IV, true);
    
    //Prepend IV to first IV.length bytes of cipherText
    cipherText = Pad.prepend(cipherText, IV.length);
    for(int i = 0;i<IV.length;++i) {
      cipherText[i] = IV[i];
    }
    
    return cipherText;
  }

  /**
   * Decrypts our encrypted data back into plaintext via our key
   * via AES-GCM
   *
   * @param data  encrypted data
   * @param key  key for this encrypted data
   * @return The original plaintext given to @see Encryption#encrypt(byte[], byte[]) encrypt. This will return null if the GCM MAC check fails. 
   */
  public byte[] decrypt( byte[] data, byte[] key )
  //wrapper function to decrypt
  {
    //Get IV from data
    byte[] IV = new byte[IV_LEN];
    System.arraycopy(data, 0, IV, 0, IV.length);
    
    //Remove IV from data
    byte[] cipherText = new byte[data.length-16]; 
    System.arraycopy(data, 16, cipherText, 0, cipherText.length);

    //Decrypt
    byte[] paddedPlainText = performCrypto(cipherText, key, IV, false);

    //depad the data
    return depad(paddedPlainText);    
  }

  /**
   * Generic execution of either encrypt or decrypt via AES-GCM
   * @param data  input data
   * @param key  key for this data
   * @param IV  IV array for this data
   * @param encrypt  true for encryption, false for decryption
   */
  private byte[] performCrypto( byte[] data, byte[] key, byte[] IV, boolean encrypt )
  //this function will do the actual encryption and if encrypt is true then this functions encrypts,
  //false means to decrypt
  {
    GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
    
    KeyParameter kp = new KeyParameter( key );
    cipher.init( encrypt, new AEADParameters( kp, MAC_SIZE*8, IV, null ) );
    //get the length of what the output data should be +/- MAC
    int inputlen = encrypt ? data.length : data.length - MAC_SIZE;
    int outputlen = encrypt ? data.length + MAC_SIZE : data.length - MAC_SIZE;
    byte[] output = new byte[ outputlen ];  

    int len = 0;
    while( len < inputlen ) { //encrypt/decrypt each block
      len += cipher.processBytes( data, len, data.length, output, len );
    }

    try {
      //If Encryption, appends our MAC to the last 16 bytes of data
      //If Decryption, generates MAC and verifies vs MAC encoded in data
      cipher.doFinal(output, len);
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
    
    return output;
  }


  //PKCS7 padding
  /**
   * Pads our input data to a multiple of the block size
   * via PKCS7. The last byte in the new padded data will
   * specify how much padding was added. 
   * @param data  input data to be padded
   * @return padded data array 
   */
  private byte[] pad( byte[] data )
  //pads the data so it can be encrypted
  {
    final int bsz = 16;
    int blocks = (int) Math.ceil( data.length/bsz ) + 1;
    int padding = blocks * bsz - data.length;

    if(padding == 0)
      padding = bsz;

    byte[] ndat = new byte[ data.length + padding ];
    System.arraycopy( data, 0, ndat, 0, data.length );
    ndat[ ndat.length - 1 ] = (byte) padding;
    return ndat;
  }

  /**
   * Depads our input data based off how much padding was added
   * @param data  padded input data
   * @return original unpadded data
   */
  private byte[] depad( byte[] data )
  //depads the data so it can be decrypted
  {
    if(data != null) {
      int padding = ( data[ data.length - 1 ] & 0xFF );
      if(padding > 16 || padding == 0)
        return null; //Invalid padding -> corrupted data
      byte[] ndat = new byte[ data.length - padding ];
      System.arraycopy( data, 0, ndat, 0, ndat.length );
      return ndat;
    } else {
      return data; // corrupted data
    }
  }
}
