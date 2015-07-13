package scar;

import java.lang.reflect.*;

public class Encryption {
    //Use this to get an object instance of the encryption class
    //used in the implementation
    public static Encryption getInstance()
    {
        return new Encryption();
    }

    Cipher cipher;	//cipher to encrypt/decrypt

    public Encryption() {}

    //key => 32 bytes
    public byte[] encrypt( byte[] plainText, byte[] key )   //wrapper function to encrypt
    {
        return encrypt( plainText, key, true );
    }

    public byte[] decrypt( byte[] cipherText, byte[] key )  //wrapper function to decrypt
    {
        return decrypt( cipherText, key, false );
    }

    private byte[] performCrypto( byte[] startingText, byte[] key, boolean encrypt )  //this function will do the actual encryption and if encrypt is true then this functions encrypts, false means to decrypt
    {

    }

}
