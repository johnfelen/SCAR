package scar;

import java.lang.reflect.*;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Encryption
{
    //Use this to get an object instance of the encryption class
    //used in the implementation
    public static Encryption getInstance()
    {
        return new Encryption();
    }

    CBCBlockCipher cipher;	//cipher to encrypt/decrypt

    public Encryption()
    {
        cipher = new CBCBlockCipher( new AESEngine() ); //makes a AES CBC cipher
    }

    //key => 32 bytes
    public byte[] encrypt( byte[] plainText, byte[] key )   //wrapper function to encrypt
    {
        byte[] paddedData = pad( plainText );   //pad the data
        return performCrypto(paddedData, key, true);
    }

    public byte[] decrypt( byte[] cipherText, byte[] key )  //wrapper function to decrypt
    {
        byte[] paddedPlainText = performCrypto( cipherText, key, false );
        return depad( paddedPlainText );    //depad the data
    }

    private byte[] performCrypto( byte[] data, byte[] key, boolean encrypt )  //this function will do the actual encryption and if encrypt is true then this functions encrypts, false means to decrypt
    {
        KeyParameter kp = new KeyParameter( key );
        cipher.init( encrypt, new ParametersWithIV( kp, IV ) );   //initiate the cipher with an IV
        byte[] outPut = new byte[ data.length ];  //get the length of what the output data should be

        int len = 0;
        while( len < data.length )  //encrypt/decrypt each block
        {
            len += cipher.processBlock( data, len, out, len );
        }

        return outPut;
    }

    private byte[] pad( byte[] data )   //pads the data so it can be encrypted
    {
        final int bsz = 16;
        int blocks = (int) Math.ceil( data.length/bsz ) + 1;
        int padding = blocks * bsz - data.length;

        if(padding == 0)
        {
            padding = bsz;
        }

        byte[] ndat = new byte[ data.length + padding ];
        System.arraycopy( data, 0, ndat, 0, data.length );
        ndat[ ndat.length - 1 ] = (byte) padding;
        return ndat;
    }

    private byte[] depad( byte[] data )  //depads the data so it can be decrypted
    {
        int padding = ( data[ data.length - 1 ] & 0xFF );
        byte[] ndat = new byte[ data.length - padding ];
        System.arraycopy( data, 0, ndat, 0, ndat.length );
        return ndat;
    }

    private byte[] genBytes( int amt, int seed )   //generates random bytes to be used as an IV
    {
        byte[] ret = new byte[amt];
        Random r = new Random(seed);

        int i;
        for(i=0;i<amt;++i)
        {
            ret[i] = (byte)r.nextInt(256);
        }

        return ret;
    }

}
