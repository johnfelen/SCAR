package scar;

import java.util.*;
import java.util.concurrent.*;
import java.math.*;
import org.spongycastle.util.encoders.Hex;

public class StoreFile {
  public static final int allowed_threads = 8;
  public static final int added_garbage = 50;
  
  private IServer servers[];
  private byte[] data;
  private String
    fn;
  private int
    buffer,
    k,
    n;
  private byte[] key;

  public StoreFile(byte[] data, String fn, byte[] password,
                   int k, int n, IServer srvs[]) {
    this.data = data;
    this.fn = fn;
    this.buffer = buffer;
    this.k = k;
    this.n = n;
    key = password;
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

  private byte[][] add_garbage(byte[][] good) {
    RndKeyGen rnd = new RndKeyGen();
    byte[][] bad = new byte[good.length+added_garbage][];
    int i;
    System.arraycopy(good, 0, bad, 0, good.length);
    
    for(i=good.length;i<bad.length;++i) {
      bad[i] = rnd.genBytes(bad[0].length);
    }
    return bad;
  }

  // Idea: Do a round robin of the servers on the chunks
  private Chunk[] distribute_chunks(byte[][] chunk_data, byte[][] hc) {
    ArrayList<Integer> id = new ArrayList<Integer>(chunk_data.length);
    ArrayList<byte[]> tmp = new ArrayList<byte[]>(chunk_data.length);
    Chunk[] chunks = new Chunk[chunk_data.length];
    int i, srv;

    //Setup pool
    for(i=0;i<chunk_data.length;++i) {
      id.add(i);
      tmp.add(chunk_data[i]);
    }

    //Round Robin chunks to servers based on hash chain
    srv = 0;
    for(i=0;i<chunk_data.length;++i) {
      final BigInteger num = new BigInteger(Hex.toHexString(hc[i]), 16);
      final int ind = num.mod(new BigInteger(Integer.toString(tmp.size()))).intValue();
      chunks[i] = new Chunk(tmp.remove(ind),
                            hc[i],
                            id.remove(ind),
                            srv);
      srv = (srv + 1) % servers.length;
    }
    
    return chunks;
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
    int padding = (((int)Math.ceil((data.length)/(float)k)) *k) - (data.length);
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
    //TODO: make the HMAC key a seperate key all together
    byte[] ckey = hash.getHash(hash.getHash(key));
    for(int i = 0;i<chunk.length;++i) {
      chunk[i] = Pad.prepend(chunk[i], hash.macSize());
      hash.addHMac(ckey, chunk[i]);
    }

    //7. Introduce random bad data into our chunks
    chunk = add_garbage(chunk);

    
    //8. Compute HashChain and Distibute chunks
    byte[][] hashArr = hash.hashchain(chunk.length, key);
    Chunk[] chunks = distribute_chunks(chunk, hashArr);

    //9. Shuffle our chunks before storing
    chunks = Shuffle.shuffle(chunks);
    
    //10. Store each chunk to it's correct server with filename 
    // chunk[i] corresponds to HashChain_i and belongs at Server_{HashChain_i % NumberOfServers}
    ExecutorService pool = Executors.newFixedThreadPool(allowed_threads);
    int x = 0;
    int numOfServ = servers.length;
    while (x < chunk.length){
      final String cname = Hex.toHexString(hash.getHash(concate(fn.getBytes(), chunks[x].hash)));
      pool.submit(new StorageTask(servers[chunks[x].server],
                                  chunks[x],
                                  cname,
                                  StorageTask.TYPE_STORE));
      
      ++x;
    }

    //wait until tasks are done
    pool.shutdown();
    try {
      while(!pool.awaitTermination(60, TimeUnit.SECONDS)); 
    } catch(Exception e) {/* TODO: This is likely an error if it hits */}
  }
}
