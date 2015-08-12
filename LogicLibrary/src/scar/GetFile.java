package scar;

import java.math.*;
import java.util.*;
import java.util.concurrent.*;
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

  private void clearFutures(ArrayList<Future<Chunk>> futures, ArrayList<Chunk> lst, byte[] key) {
    Hash hash = new Hash();
    while(futures.size() == n) {
      final ListIterator<Future<Chunk>> itr = futures.listIterator(0);
      while(itr.hasNext()) {
        final Future<Chunk> ftr = itr.next();
        if(ftr.isDone()){
          itr.remove();
          Chunk chk = null; 
          try {
            chk = ftr.get();
          } catch(Exception e) {/* TODO: Handle error*/}
          if(chk != null) {
            byte[] data = chk.data;
            data = hash.checkHMac(key, data);
            if(data != null) {
              lst.add(new Chunk(data, null, chk.ind));
            }
          }
        }
      }
      
      if(futures.size() == n) {
        // no change, sleep
        try {
          Thread.sleep(500);
        }catch(Exception e) {}
      }
    }
  }

  private void fetchChunks(ArrayList<Chunk> lst, byte[][] hashchain, byte[] key) {
    Hash hash = new Hash();
    ArrayList<Future<Chunk>> futures = new ArrayList<Future<Chunk>>(n);
    ExecutorService pool = Executors.newFixedThreadPool(StoreFile.allowed_threads);
    for(int x = 0;x < n; ++x) {
      //Clear futures if we maxed out
      clearFutures(futures, lst, key);
      
      //add new task if needed
      final BigInteger num = new BigInteger(Hex.toHexString(hashchain[x]), 16);
      final int i = num.mod(new BigInteger(Integer.toString(servers.length))).intValue();
      final String name = Hex.toHexString(hash.getHash(concate(fn.getBytes(),hashchain[x])));
      
      futures.add(pool.submit(new StorageTask(servers[i], null, name, x, StorageTask.TYPE_GET)));
    }

    //Clear all ongoing futures
    clearFutures(futures, lst, key);
  }
  

  //Get as many blocks from servers, decrypt, apply rs, remove padding
  public byte[] get() throws Exception {
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
    //TODO: make the HMAC key a seperate key all together
    byte[] ckey = hash.getHash(hash.getHash(key));
    fetchChunks(chunk, hashArr, ckey);
    
    //3. Decode k chunks to get encrypted data back via RS
    if(chunk.size() < k) 
      throw new Exception("Not enough chunks of data to recover the data");
    RS rs = new RS();
    byte[] data = rs.decode(chunk.toArray(new Chunk[0]), k, n);
    if(data == null) //Failed the MAC check
      throw new Exception("Decryption failed the GMAC test");
      
    
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
