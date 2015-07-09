package scar;

import java.security.MessageDigest;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;


public class Hash {
  private static String DIGEST_METHOD = "SHA-256";
  private MessageDigest digest;
	
	public Hash(){
    try {
      digest = MessageDigest.getInstance(DIGEST_METHOD);
    } catch(Exception e) {
      System.out.println("Can't find hashing algorithm for : " + DIGEST_METHOD);
      e.printStackTrace();
      System.exit(1);
    }
	}
	
	public byte[] getHashKey(String content){
    return getHashKey(content.getBytes());
  }

  
	public byte[] getHashKey(byte[] content){
    return digest.digest(content);2
  }
	
	public byte[][] hashchain(int n, String key)
	{
    byte[][] hashes = new byte[n][];
    hashes[0] = getHashKey(key);
    
    for(int i = 1;i<n;++i)
      hashes[i] = getHashKey(hashes[i-1]);
    
		return hashes;
	}
}
