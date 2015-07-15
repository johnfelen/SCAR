package scar;

import java.util.*;
import java.math.*;
import org.spongycastle.util.encoders.Hex;

public class StoreFile {
  private IServer servers[];
  private byte[] data;
  private String
    fn,
    password,
    key;
  private int
    buffer,
    k,
    n;

  public StoreFile(byte[] data, String fn, String password,
                   int k, int n, IServer srvs[]) {
    this.data = data;
    this.fn = fn;
    this.password = password;
    this.buffer = buffer;
    this.k = k;
    this.n = n;
    key = fn + password;
    servers = srvs;
  }

  //Encode 'val' into 'arr' starting at location 'n'
  public void eint(byte[] arr, int n, int val) {
    arr[n]   = (byte)((val & 0x000000FF) >> 0);
    arr[n+1] = (byte)((val & 0x0000FF00) >> 8);
    arr[n+2] = (byte)((val & 0x00FF0000) >> 16);
    arr[n+3] = (byte)((val & 0xFF000000) >> 24);
  }

  public byte[] concate(byte[] a, byte[] b) {
    //Returns [a1,...,a_n,b1,...,b_n]
    byte[] ret = new byte[a.length+b.length];
    System.arraycopy(a, 0, ret, 0, a.length);
    System.arraycopy(b, 0, ret, a.length, b.length);
    return ret;
  }
  
  
  // Prepare data for RS, Apply RS, Apply Encryption, Store blocks properly
  public void store(){
    //1. Encrypt the data
    //Output format:
    //   _____________________
    //  | 16-byte IV          |
    //  |---------------------|
    //  | Encrypted Data      |
    //  |---------------------|
    //  | 16-byte MAC         |
    //  |_____________________|
    Hash hash = new Hash();
    Encryption encrypt = new Encryption();
    data = encrypt.encrypt(data, hash.getHash(key));

    //2. add paddding information to data for RS
    //   _______________________
    //  | 4-byte # of pad bytes |
    //  |-----------------------|
    //  | 16-byte IV            | ---.
    //  |-----------------------|    |
    //  | Encrypted Data        | ---.
    //  |-----------------------|    |-- data before padding added
    //  | 16-byte MAC           | ---.
    //  |-----------------------|
    //  | Padded bytes...       |
    //  |_______________________|
    data = Pad.prepend(data, 4);
    
    //3. pad data so the chunks are all the same sized
    //    also account for a n-bit hash
    int padding = (((int)Math.ceil((data.length+hash.hashSize())/(float)k)) *k) - (data.length+hash.hashSize());
    data = Pad.append(data, padding);

    //4. Set header bytes to the correct value as computed in (3)
    eint(data, 0, padding);

    //5. Encode data with RS.encode(data, k, n) -> return chunks[n][len]
    RS rs = new RS();
    byte[][] chunk = rs.encode(data, k, n);

    //6. Add in hmac information to each chunk
    //   Note: chunk data is not encrypted, we just need
    //         to ensure the integrity of the chunk for GetFile
    //   _______________________
    //  | n-byte HMAC           |
    //  |-----------------------|
    //  | Chunk data...         |
    //  |_______________________|
    KeyGen keygen = new KeyGen();
    keygen.seed(hash.getHash(key));
    byte[] ckey = keygen.genBytes(hash.hashSize());
    for(int i = 0;i<chunk.length;++i) {
      chunk[i] = Pad.prepend(chunk[i], hash.hashSize());
      hash.addHMac(ckey, chunk[i]);
    }

    //7. Introduce random number of garbage chunks into our chunks [TODO: Finish this]
    int extrachunks = keygen.nextByte(250-(n+Math.min(10, 250-n))) + Math.min(10, 250-n);
    
    //8. Compute HashChain 
    byte[][] hashArr = hash.hashchain(n, key);
    
    //9. Store each chunk to it's correct server with filename 
    // chunk[i] corresponds to HashChain_i and belongs at Server_{HashChain_i % NumberOfServers}

    int x = 0;
    int numOfServ = servers.length;
    while (x < chunk.length){
      BigInteger num = new BigInteger(Hex.toHexString(hashArr[x]), 16);
      int i = num.mod(new BigInteger(Integer.toString(numOfServ))).intValue();

      servers[i].storeData(Hex.toHexString(hash.getHash(concate(fn.getBytes(),hashArr[x]))), chunk[x]);

      ++x;
    }
  }
}
