package scar;

import java.security.MessageDigest;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Hex;
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
	
	/**
	 * @description returns the hashed verison of the string in Hex String form
	 * @param content
	 * @return
	 */
	public String getHashKey(String content){
    try {
    byte[] hash = digest.digest(content.getBytes("UTF-8"));
    
    return new String(Hex.encodeHex(hash));
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }
	
	/**
	 * @description returns the last known value of the recurisve thing. use getArr to the a list. 
	 * @param n - "f"
	 * @param key - filename
	 * @param password - password
	 * @return
	 */

	public ArrayList<String> hashchain(int n, String key)
	//none recursive version
	{
    ArrayList<String> hashes = new ArrayList<String>();
    hashes.add(getHashKey(key));
    
    for(int i = 1;i<n;++i)
      hashes.add(getHashKey(hashes.get(i-1)));

		return hashes;
	}
}
