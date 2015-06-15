package com.example.scar2.Logic;

import java.security.*;
import javax.crypto.*;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;    TODO:fix this line
//import org.bouncycastle.util.encoders.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());	//add bouncy castle as a provider   TODO:fix this line
    Cipher cipher;	//cipher to encryp/decrypt
    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");   //used to hash the key to get 256-bit sha

    public byte[] encrypt(byte[] data, String stringOfKey)
    {
        SecretKey AESkey = getAESKey(stringOfKey);
        return data;
    }

    public byte[] decrypt(byte[] data, String stringOfKey)
    {
        SecretKey AESkey = getAESKey( stringOfKey );
        return data;
    }

    private SecretKey getAESKey( String stringOfKey )  //takes the stringOfKey and transforms it into a usuable 256-bit AES key
    {
        SecretKey AESkey;	//AES key used for cipher
        sha256.update( stringOfKey.getBytes() );   //perform the hash on the stringOfKey
        AESkey = new SecretKeySpec( sha256.digest(), "AES" );  //creates AESkey from the stringOfKey
        return AESkey;
    }
}
