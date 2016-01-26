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
    while(futures.size() > 0) {
      final ListIterator<Future<Chunk>> itr = futures.listIterator(0);
      while(itr.hasNext()) {
        final Future<Chunk> ftr = itr.next();
        if(ftr.isDone()){
          itr.remove();
          Chunk chk = null; 
          try {
            chk = ftr.get();
          } catch(Exception e) {/* TODO: Handle error*/}
          if(chk != null && chk.ind < n) {
            byte[] data = chk.data;
            data = hash.checkHMac(key, data);
            if(data != null) {
              lst.add(new Chunk(data, chk.hash, chk.ind, chk.server));
            }
          }
        }
      }
      
      if(futures.size() > 0) {
        // no change, sleep
        try {
          Thread.sleep(500);
        }catch(Exception e) {}
      }
    }
  }

  private int lookupMeta(ChunkMeta metas[], String name){
    int i;
    for(i=0;i<metas.length;++i)
      if(metas[i].name.equals(name))
        return i;
    return -1; //should never reach here unless the get-file key is wrong
  }
  
  private void fetchChunks(ArrayList<Chunk> lst, Chunk[] data, ChunkMeta[] metas, byte[] key) {
    Hash hash = new Hash();
    ArrayList<Future<Chunk>> futures = new ArrayList<Future<Chunk>>(n);
    ExecutorService pool = Executors.newFixedThreadPool(StoreFile.allowed_threads);
    for(int x = 0;x < n; ++x) {
      //Clear futures if we maxed out
      if(futures.size() >= k) {
        clearFutures(futures, lst, key);
      } else {
        //Stop if we already have enough
        if(lst.size() >= k) {
        break;
        }
        
        //add new task if needed
        final String name = Hex.toHexString(hash.getHash(concate(fn.getBytes(), data[x].hash)));
        int meta = lookupMeta(metas, name);
        if(meta >= 0) {
          futures.add(pool.submit(new StorageTask(servers[metas[meta].physical],
                                                  data[x],
                                                  name,
                                                  StorageTask.TYPE_GET)));
        }
      }
    }
    
    pool.shutdown();
    //Clear all ongoing futures
    clearFutures(futures, lst, key);
  }
  
  // Idea: Do a round robin of the servers on the chunks
  private Chunk[] distribute_chunks(byte[][] hc) {
    ArrayList<Integer> tmp = new ArrayList<Integer>(n);
    Chunk[] chunks = new Chunk[n+StoreFile.added_garbage];
    int i, srv;

    //Setup pool
    for(i=0;i<n+StoreFile.added_garbage;++i) {
      tmp.add(i);
    }

    //Round Robin chunks to servers based on hash chain
    srv = 0;
    for(i=0;i<n+StoreFile.added_garbage;++i) {
      final BigInteger num = new BigInteger(Hex.toHexString(hc[i]), 16);
      final int ind = num.mod(new BigInteger(Integer.toString(tmp.size()))).intValue();
      final String name = Hex.toHexString(new Hash().getHash(concate(fn.getBytes(), hc[i])));
      chunks[i] = new Chunk(null,
                            hc[i],
                            tmp.remove(ind),
                            srv);
      srv = (srv + 1) % servers.length;
    }
    
    return chunks;
  }

  //Get as many blocks from servers, decrypt, apply rs, remove padding
  //TODO 4Ryan: Adjust get(ChunkMeta[]) to use the ChunkMeta[] array cms
  //            for gathering the chunk data from their physical servers.
  // 
  //            What you basically need to do is take the hash chain hashArr
  //            and generate the chunk filenames and lookup in the cms array
  //            for the chunk filename you generated. Then from that ChunkMeta
  //            use the physical server it has listed to get the data for that
  //            chunk.
  // 
  //            You also need to mark the Chunk id based off the index in
  //            the hash chain it is.
  //
  //     Overview: hashArr[i] -> Chunk.name[i] -> cms[j]
  //               Chunk.data[i] = servers[cms[j].physical].get...
  //               Chunk.ind[i] = i
  public byte[] get(final ChunkMeta cms[]) throws Exception {
    //1. Compute HashChain
    Hash hash = new Hash();
    byte[][] hashArr = hash.hashchain(n+StoreFile.added_garbage, key);
    Chunk[] chunk_data = distribute_chunks(hashArr);
    
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
    fetchChunks(chunk, chunk_data, cms, ckey);
    
    //3. Decode k chunks to get encrypted data back via RS
    if(chunk.size() < k) 
      throw new Exception("Not enough chunks of data to recover the data");
    RS rs = new RS();
    byte[] data = rs.decode(chunk.toArray(new Chunk[0]), k, n);
    if(data == null) //Failed the MAC check
      throw new Exception("Decryption failed the GMAC test");
      
    
    //RS Output:
    //  Encrypted:                   NoEncrypt:
    //   _______________________   +-----------------------+
    //  | 4-byte # of pad bytes |  | 4-byte # of pad bytes |
    //  |-----------------------|  +-----------------------+
    //  | 1-byte encrypt flag   |  | 1-byte encrypt flag   |
    //  |-----------------------|  +-----------------------+
    //  | 16-byte IV            |  | Unencrypted data      |
    //  |-----------------------|  +-----------------------+
    //  | Encrypted Data        |  | Padded bytes...       |
    //  |-----------------------|  +-----------------------+
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
    //  Encrypted:                  NoEncrypt:
    //  +---------------------+   +---------------------+
    //  | 1-byte encrypt flag |   | 1-byte encrypt flag |
    //  |---------------------|   +---------------------+
    //  | 16-byte IV          |   | Unencrypted Data    |
    //  |---------------------|   +---------------------+
    //  | Encrypted Data      |
    //  |---------------------|
    //  | 16-byte MAC         |
    //  |_____________________|
    //get Enc flag
    boolean enc = data[0] == 1;
    data = Pad.deprepend(data, 1);
    Encryption decrypt = enc ? new Encryption() : new NoEncryption();
    data = decrypt.decrypt(data, hash.getHash(key));

    return data;
  }
}
