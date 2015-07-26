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
	
	public Hash(){
    //change below to change digest type, keep both the same...
    digest = new SHA256Digest();
    hmac = new HMac(new SHA256Digest());
  }

  public int hashSize() {
    return digest.getDigestSize();
  }

  public int macSize() {
    return hmac.getMacSize();
  }

  public static void print(byte[] data, int len) {
    for(int i = 0;i<len;++i)
      System.out.print((data[i] & 0xFF) + " ");
    System.out.println();
  }
  
  //prepends HMAC into content 0...HMAC.size bytes
  public void addHMac(byte[] key, byte[] content) {
    int msz = hmac.getMacSize();
    hmac.reset();
    hmac.init(new KeyParameter(key));
    hmac.update(content, msz, content.length - msz);
    hmac.doFinal(content, 0);
  }

  // Data format:
  //   _______________________
  //  | n-byte HMAC           |
  //  |-----------------------|
  //  |  Data...              |
  //  |_______________________|
  // Removes the expected n-byte HMAC, computes HMAC of Data
  // and verifies it against what we expected.
  // Returns null if corrupted, otherwise the data itself
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
  
	public byte[] getHash(String content){
    return getHash(content.getBytes());
  }
  
	public byte[] getHash(byte[] content){
    digest.update(content, 0, content.length);
    byte[] ret = new byte[digest.getDigestSize()];
    digest.doFinal(ret, 0);
    return ret;
  }
	
	public byte[][] hashchain(int n, byte[] key)
	{
    byte[][] hashes = new byte[n][];
    hashes[0] = getHash(key);
    
    for(int i = 1;i<n;++i)
      hashes[i] = getHash(hashes[i-1]);
    
		return hashes;
	}
}
