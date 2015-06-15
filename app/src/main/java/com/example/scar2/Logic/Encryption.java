package com.example.scar2.Logic;

import java.security.*;
import javax.crypto.*;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;    TODO:fix this line
//import org.bouncycastle.util.encoders.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class Encryption {

    //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());	//add bouncy castle as a provider   TODO:fix this line
    Cipher cipher;	//cipher to encryp/decrypt
    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");   //used to hash the key to get 256-bit sha

    public byte[] encrypt(byte[] data, String stringOfKey)  throws Exception    //takes the original data and transforms it into cipher text*/
    {
        //set up
        SecretKey AESkey = getAESKey( stringOfKey );
        cipher = Cipher.getInstance("AES", "BC");   //set up cipher for AES
        cipher.init( Cipher.ENCRYPT_MODE, AESkey );	//set the cipher as encrypt mode with the key

        byte[] cipherText = cipher.doFinal(data);         //the actual encryption
        return Base64.encodeBase64( cipherText );   //return the Base64 encrypted version of the cipher text, Base64 is for padding errors
    }

    public byte[] decrypt(byte[] cipherText, String stringOfKey)    throws Exception    //takes the cipherText and returns the original data*/
    {
        //set up
        SecretKey AESkey = getAESKey( stringOfKey );
        cipher = Cipher.getInstance("AES", "BC");   //set up cipher for AES
        cipher.init( Cipher.DECRYPT_MODE, AESkey );	//set the cipher as encrypt mode with the key

        cipherText = Base64.decodeBase64( cipherText ); //remove the Base64 buffer
        return cipher.doFinal( cipherText );    //returns the original data
    }

    private SecretKey getAESKey( String stringOfKey )  //takes the stringOfKey and transforms it into a usuable 256-bit AES key
    {
        SecretKey AESkey;	//AES key used for cipher
        sha256.update( stringOfKey.getBytes() );   //perform the hash on the stringOfKey
        AESkey = new SecretKeySpec( sha256.digest(), "AES" );  //creates AESkey from the stringOfKey
        return AESkey;
    }
}
