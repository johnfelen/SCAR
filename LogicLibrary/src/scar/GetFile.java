package scar;

import java.math.*;
import java.util.*;
import org.apache.commons.codec.binary.Hex;

public class GetFile {
  private String
    fn,
    password,
    key;
  private int
    buffer,
    k,
    n;
  private IServer servers[];

  public GetFile(String fn, String password, int k, int n, IServer srvs[]) {
    this.fn = fn;
    this.password = password;
    this.buffer = buffer;
    this.k = k;
    this.n = n;
    Hash hash = new Hash();
    key = fn + password;
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
    Hash hashChain = new Hash();
    byte[][] hashArr = hashChain.hashchain(n, key);
    
    int numOfServ = servers.length;
    ArrayList<Chunk> chunk = new ArrayList<Chunk>();

    //2. Get data
    int x = 0;
    while (x < n){
      BigInteger num = new BigInteger(Hex.encodeHex(hashArr[x]), 16);
      int i = num.mod(new BigInteger(Integer.toString(numOfServ))).intValue();
      
      byte[] c = servers[i].getData(Hex.encodeHex(hashChain.getHashKey(concate(fn,hashArr[x]))));
      if (c != null){
        chunk.add(new Chunk(c, x));
      }
      
      ++x;
    }
    
    //3. Decode k chunks to get encrypted data back
    RS rs = new RS();
    byte[] data = rs.decode(chunk.toArray(new Chunk[0]), k, n);

    
    //4. Get the padding
    int padding = dint(data, 0);
    
    //5. Depad the data & remove header
    data = Pad.deappend(data, padding);
    data = Pad.deprepend(data, 4);

    //6. Decrypt the data
    Encryption decrypt = Encryption.getInstance();
    data = decrypt.decrypt(data, hashChain.getHashKey(key));

    return data;
  }
}
