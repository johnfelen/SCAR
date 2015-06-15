package com.example.scar2.Logic;

import java.security.*;
import javax.crypto.*;
import org.spongycastle.*;
import org.spongycastle.util.encoders.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    /*This static statement must be put into any file using spongy castle crypto so that Android knows to use the spongy castle jars*/
    static
    {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    Cipher cipher;	//cipher to encryp/decrypt

    public byte[] encrypt(byte[] data, String stringOfKey)  //takes the original data and transforms it into cipher text, NOTE: There are a lot of exceptions bu I think it is android, I have not run this code in android yet
    {
        try
        {
            //set up
            SecretKey AESkey = getAESKey(stringOfKey);
            cipher = Cipher.getInstance("AES", "SC");   //set up cipher for AES
            cipher.init(Cipher.ENCRYPT_MODE, AESkey);    //set the cipher as encrypt mode with the key

            byte[] cipherText = cipher.doFinal(data);         //the actual encryption
            return Base64.encode(cipherText);   //return the Base64 encrypted version of the cipher text, Base64 is for padding errors
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] cipherText, String stringOfKey)    //takes the cipherText and returns the original data, NOTE: same as encrypt method
    {
        try
        {
            //set up
            SecretKey AESkey = getAESKey(stringOfKey);
            cipher = Cipher.getInstance("AES", "SC");   //set up cipher for AES
            cipher.init(Cipher.DECRYPT_MODE, AESkey);    //set the cipher as encrypt mode with the key

            cipherText = Base64.decode(cipherText); //remove the Base64 buffer
            return cipher.doFinal(cipherText);    //returns the original data
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private SecretKey getAESKey( String stringOfKey )  throws NoSuchAlgorithmException  //takes the stringOfKey and transforms it into a usuable 256-bit AES key, NOTE: From reading online, I am supposed to through this exception
    {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");   //used to hash the key to get 256-bit sha
        SecretKey AESkey;	//AES key used for cipher
        sha256.update( stringOfKey.getBytes() );   //perform the hash on the stringOfKey
        AESkey = new SecretKeySpec( sha256.digest(), "AES" );  //creates AESkey from the stringOfKey
        return AESkey;
    }
}
