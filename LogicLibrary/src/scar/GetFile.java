package scar;

import java.math.*;
import java.util.*;
import org.spongycastle.util.encoders.Hex;

public class GetFile {
  private String
    fn;
  private int
    buffer,
    k,
    n;
  private IServer servers[];
  private byte[] key;

  public GetFile(String fn, byte[] password, int k, int n, IServer srvs[]) {
    this.fn = fn;
    this.buffer = buffer;
    this.k = k;
    this.n = n;
    key = password;
    servers = srvs;
  }
  
  public int dint(byte[] data, int start) {
    return 
      (data[start] & 0x000000FF) +
      ((data[start+1] << 8) & 0x0000FF00) +
      ((data[start+2] << 16) & 0x00FF0000) +
      ((data[start+3] << 24) & 0xFF000000);
  }

  public byte[] concate(byte[] a, byte[] b) {
    //Returns [a1,...,a_n,b1,...,b_n]
    byte[] ret = new byte[a.length+b.length];
    System.arraycopy(a, 0, ret, 0, a.length);
    System.arraycopy(b, 0, ret, a.length, b.length);
    return ret;
  }

  //Get as many blocks from servers, decrypt, apply rs, remove padding
  public byte[] get() {
    //1. Compute HashChain
    Hash hash = new Hash();
    byte[][] hashArr = hash.hashchain(n, key);
    
    int numOfServ = servers.length;
    ArrayList<Chunk> chunk = new ArrayList<Chunk>();

    //2. Get data
    // Each chunk:
    //   _______________________
    //  | n-byte hash           |
    //  |-----------------------|
    //  | Chunk data...         |
    //  |_______________________|
    byte[] ckey = hash.getHash(key);
    int x = 0;
    while (x < n){
      BigInteger num = new BigInteger(Hex.toHexString(hashArr[x]), 16);
      int i = num.mod(new BigInteger(Integer.toString(numOfServ))).intValue();
      
      byte[] c = servers[i].getData(Hex.toHexString(hash.getHash(concate(fn.getBytes(),hashArr[x]))));
      if(c != null) {
        // System.out.println("Chunk: " + x);
        c = hash.checkHMac(ckey, c);
        if (c != null){ //Only add if valid data
          chunk.add(new Chunk(c, x));
        }
      }
      
      ++x;
    }
    
    //3. Decode k chunks to get encrypted data back via RS
    if(chunk.size() < k)
      return null; //We don't have enough chunks to recover all the data
    RS rs = new RS();
    byte[] data = rs.decode(chunk.toArray(new Chunk[0]), k, n);
    
    //RS Output:
    //   _______________________
    //  | 4-byte # of pad bytes |
    //  |-----------------------|
    //  | 16-byte IV            |
    //  |-----------------------|
    //  | Encrypted Data        |
    //  |-----------------------|
    //  | 16-byte MAC           | 
    //  |-----------------------|
    //  | Padded bytes...       |
    //  |_______________________|
    
    //4. Get the padding
    int padding = dint(data, 0);
    
    //5. Depad the data & remove header
    data = Pad.deappend(data, padding);
    data = Pad.deprepend(data, 4);

    //6. Decrypt the data
    //   _______________________
    //  | 16-byte IV            |
    //  |-----------------------|
    //  | Encrypted Data        |
    //  |-----------------------|
    //  | 16-byte MAC           | 
    //  |_______________________|
    Encryption decrypt = new Encryption();
    data = decrypt.decrypt(data, hash.getHash(key));

    return data;
  }
}
