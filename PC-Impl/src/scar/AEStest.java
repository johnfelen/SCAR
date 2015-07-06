import java.util.*;
import java.io.*;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class AEStest {
    String srcfn;

    public AEStest(String f) {
	srcfn = f;
    }

    public byte[] genBytes(int amt, int seed) {
	byte[] ret = new byte[amt];
	Random r = new Random(seed);
	int i;
	for(i=0;i<amt;++i)
	    ret[i] = (byte)r.nextInt(256);
	return ret;
    }

    public void print(String name, byte[] b) {
	int i;
	System.out.println(name + ": ");
	for(i=0;i<b.length;++i) {
	    System.out.print((b[i] & 0xFF) + " ");
	    if((i+1) % 8 == 0)
		System.out.println();
	}
	System.out.println();
    }
    
    public byte[] getData() throws Exception {
	File f = new File(srcfn);
	FileInputStream fis = new FileInputStream(f);
	byte[] ret = new byte[(int)f.length()];
	
	fis.read(ret);
	
	return ret;
    }

    public void writeData(String fn, byte[] data) throws Exception {
	File f = new File(fn);
	f.createNewFile();
	
	FileOutputStream fos = new FileOutputStream(f);
	
	fos.write(data);
	fos.flush();
	fos.close();
    }

    public byte[] pad(byte[] data) {
	final int bsz = 16;
	int blocks = (int)Math.ceil(data.length/bsz) + 1;
	int padding = blocks*bsz - data.length;
	if(padding == 0)
	    padding = bsz;
	byte[] ndat = new byte[data.length+padding];
	System.arraycopy(data, 0, ndat, 0, data.length);
	ndat[ndat.length-1] = (byte)padding;
	return ndat;
    }

    public void encrypt(String ofn) throws Exception {
	byte[] key = genBytes(32, 9999);
	byte[] IV = genBytes(16, 9998);
	byte[] data = getData();
	print("Key", key);
	print("IV", IV);
	
	//PaddedBufferedBlockCipher cipher
	//    = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
	data = pad(data);
	CBCBlockCipher cipher
	    = new CBCBlockCipher(new AESEngine());
	
	KeyParameter kp = new KeyParameter(key);
	
	cipher.init(true, new ParametersWithIV(kp, IV));
	
	byte[] out = new byte[data.length];//cipher.getOutputSize(data.length)];
	
	int len = 0;
	while( len < data.length ) {
	    len += cipher.processBlock(data, len, out, len);
	}

	//cipher.doFinal(out, len);
	
	writeData(ofn, out);
    }

    public byte[] depad(byte[] data) {
	int padding = (data[data.length-1] &  0xFF);
	byte[] ndat = new byte[data.length-padding];
	System.arraycopy(data, 0, ndat, 0, ndat.length);
	return ndat;
    }

    public void decrypt(String dfn) throws Exception {
	byte[] key = genBytes(32, 9999);
	byte[] IV = genBytes(16, 9998);
	byte[] data = getData();
	print("Key", key);
	print("IV", IV);
	
       
	//PaddedBufferedBlockCipher cipher
	//    = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
	CBCBlockCipher cipher
	    = new CBCBlockCipher(new AESEngine());
	KeyParameter kp = new KeyParameter(key);
	
	cipher.init(false, new ParametersWithIV(kp, IV));
	
	byte[] out = new byte[data.length];//cipher.getOutputSize(data.length)];
	
	int len = 0;
	while( len < data.length ) {
	    len += cipher.processBlock(data, len, out, len);
	}
	
	out = depad(out);

	//int len = cipher.processBytes(data, 0, data.length, out, 0);
	
	//cipher.doFinal(out, len);
	
	writeData(dfn, out);
    }


    public static void main(String args[]) throws Exception {
	if(args[0].equals("1"))
	    (new AEStest("bcprov-jdk15on-152.jar")).encrypt("bcen");
	else
	    (new AEStest("bcen")).decrypt("bcde");
    }
}
