/**
 * Hash contains any hash-related functionality to the application
 * this includes:
 *  - hashchainning
 *  - generating HMACs
 *  - verifying HMACs
 */
package scar;

import java.security.MessageDigest;
import org.spongycastle.crypto.digests.GeneralDigest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import java.util.ArrayList;


public class Hash {
  private GeneralDigest digest;
  private HMac hmac;

  /**
   * Initialize our Hash class. We make use of the SHA256 Digest algorithm via Spongy Castle
   */
	public Hash(){
    //change below to change digest type, keep both the same...
    digest = new SHA256Digest();
    hmac = new HMac(new SHA256Digest());
  }

  /**
   * @return the size of the hash produced by our digest
   */
  public int hashSize() {
    return digest.getDigestSize();
  }

  /**
   * @return the size of the HMAC produced by our digest
   */
  public int macSize() {
    return hmac.getMacSize();
  }
  
  //prepends HMAC into content 0...HMAC.size bytes
  /**
   * Prepends an HMAC into our data array with a given key
   * for authentication later on
   * @param key private key associated with this data
   * @param content original data byte array with space for the HMAC in the first @see Hash#macSize macSize bytes
   */
  public void addHMac(byte[] key, byte[] content) {
    int msz = hmac.getMacSize();
    hmac.reset();
    hmac.init(new KeyParameter(key));
    hmac.update(content, msz, content.length - msz);
    hmac.doFinal(content, 0);
  }

  /**
   * Input Data format:
   *   _______________________
   *  | n-byte HMAC           |
   *  |-----------------------|
   *  |  Data...              |
   *  |_______________________|
   * Removes the expected n-byte HMAC, computes HMAC of Data
   * and verifies it against what we expected.
   * Returns null if corrupted, otherwise the data itself
   * @param key private key associated with this data
   * @param data data with HMAC prepended to it, see format above
   * @return if HMAC is valid returns the original data byte array, otherwise, null.  
  */
  public byte[] checkHMac(byte[] key, byte[] data) {
    try {
      byte[] expected = new byte[hmac.getMacSize()];
      byte[] datahmac = new byte[expected.length];
      System.arraycopy(data, 0, expected, 0, expected.length);
      data = Pad.deprepend(data, expected.length);

      hmac.reset();
      hmac.init(new KeyParameter(key));
      hmac.update(data, 0, data.length);
      hmac.doFinal(datahmac, 0);
      
      for(int i = 0;i<expected.length;++i)
        if(expected[i] != datahmac[i])
          return null; //corrupted data

      //Valid data
      return data;
    } catch(Exception e) {
      return null; // any errors -> corrupted data
    }
  }

  /**
   * Generic function for getting the hash of a string
   * This called @see Hash#getHash(byte[]) getHash(byte[])
   * @param content string to hash
   * @return hash of input string
   */
	public byte[] getHash(String content){
    return getHash(content.getBytes());
  }

  /**
   * Generic function for getting the hash of a byte array
   * @param content byte array to hash
   * @return hash of input byte array
   */  
	public byte[] getHash(byte[] content){
    digest.update(content, 0, content.length);
    byte[] ret = new byte[digest.getDigestSize()];
    digest.doFinal(ret, 0);
    return ret;
  }

  /**
   * Computes the hashchain given a key and specified number of hashes desired
   * @param n number of hashes desired
   * @param key private key to start the hashchain
   * @return array of each hash in the chain
   */
	public byte[][] hashchain(int n, byte[] key)
	{
    byte[][] hashes = new byte[n][];
    hashes[0] = getHash(key);
    
    for(int i = 1;i<n;++i)
      hashes[i] = getHash(hashes[i-1]);
    
		return hashes;
	}
}
