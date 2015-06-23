package scar;

import java.security.MessageDigest;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


public class Hash {

	private ArrayList<String>keys;
	
	public Hash(){
		
	}
	
	/**
	 * @description returns the hashed verison of the string in Hex String form
	 * @param content
	 * @return
	 */
	public String getHashKey(String content){
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(content.getBytes("UTF-8"));
			
			return new String(Hex.encodeHex(DigestUtils.md5(hash)));
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Hashing failed");
			return "";
		}
	}
	
	/**
	 * @description returns the last known value of the recurisve thing. use getArr to the a list. 
	 * @param n - "f"
	 * @param key - filename
	 * @param password - password
	 * @return
	 */

	public void recursiveKey(int n, String key, String password)	//none recursive version, return value is now void
	{
		String newKey = getHashKey( key + password );	//gets the first hash
		while( n > 0 )	//TODO Check if 0 is the right number to stop at
		{
			newKey = getHashKey( newKey + key + password );	//get the hash of the current key and password with the previous hash
			this.keys.add( newKey );	//addas to the arraylist keys
			n--;	//decrement n
		}

		return;
	}

	
	/**
	 * initliaze the arraylist
	 */
	public void setArr(){
		this.keys = new ArrayList();
	}
	
	/**
	 * returns the keys
	 * @return
	 */
	public ArrayList<String> getArr(){
		return this.keys;
	}
}
