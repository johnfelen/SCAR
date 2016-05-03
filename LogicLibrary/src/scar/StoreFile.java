package scar;

import java.util.*;
import java.util.concurrent.*;
import java.math.*;
import org.spongycastle.util.encoders.Hex;

/**
 * StoreFile has the combined logic to take a file and store it across our servers in chunk form
 * An overview of the process can be seen:
 * <img src="../../store.png" alt="store-flow">
 */
public class StoreFile {
  /**
   * maximum number of threads in our java threadpool for uploading tasks
   */
  public static final int allowed_threads = 8;
  /**
   * number of garbage chunks added
   */
  public static final int added_garbage = 50;
  /**
   * maximum number of virtual servers, meant to be higher than 
   * the number of physical servers for a typical user
   */
  public static final int virtual_servers = 51;
  
  private IServer servers[];
  private byte[] data;
  private String
    fn;
  private int
    buffer,
    k,
    n;
  private final boolean
    enc;
  private byte[] key;

  /**
   * Intialize the StoreFile request 
   * @param data original data to be stored
   * @param fn filename
   * @param password key
   * @param k k
   * @param n n
   * @param srvs all known sevrers in the system
   * @param enc whether or not to encrypt before doing RS
   */
  public StoreFile(byte[] data, String fn, byte[] password,
                   int k, int n, IServer srvs[],
                   boolean enc) {
    this.data = data;
    this.fn = fn;
    this.buffer = buffer;
    this.k = k;
    this.n = n;
    key = password;
    servers = srvs;
    this.enc = enc;
  }

  /**
   * Encode 'val' into 'arr' starting at location 'n'
   * Value is taken as a 4-byte int
   * @param arr byte array
   * @param n offset
   * @param val value to insert
   */
  public void eint(byte[] arr, int n, int val) {
    arr[n]   = (byte)((val & 0x000000FF) >> 0);
    arr[n+1] = (byte)((val & 0x0000FF00) >> 8);
    arr[n+2] = (byte)((val & 0x00FF0000) >> 16);
    arr[n+3] = (byte)((val & 0xFF000000) >> 24);
  }

  /**
   * Concatenates two byte arrays together
   * @param a first input array
   * @param b second input array
   * @return concatenated byte array of a b
   */
  public byte[] concate(byte[] a, byte[] b) {
    //Returns [a1,...,a_n,b1,...,b_n]
    byte[] ret = new byte[a.length+b.length];
    System.arraycopy(a, 0, ret, 0, a.length);
    System.arraycopy(b, 0, ret, a.length, b.length);
    return ret;
  }

  /**
   * generates and inserts garbage chunks into our
   * chunk array to confuse attackers
   * @param good our good valid chunks
   */
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

  
  /**
   * Genererates the distribution of chunks given the hashchain
   * Basic Algorithm<br>
   *  1. make a list of chunk ids<br>
   *  2. pick from this list with removal via current hash in hashchain<br>
   *  3. assign this chunk to the current virtual server<br>
   *  4. increment to next virtual server modulo the maximum virtual servers<br>
   * Basically a round robin distribution of chunks given by the hashchain as far as what
   * chunk goes to what server
   * @param hc hashchain
   * @return array of chunks with their server information set and indexing set
   */
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
                            srv,
                            servers[srv % servers.length].id(),
                            srv % servers.length);
      srv = (srv + 1) % virtual_servers;
    }
    
    return chunks;
  }
  
  // Prepare data for RS, Apply RS, Apply Encryption, Store blocks properly
  /**
   * Actual combined logic to take a file from a byte array and store it as chunks across the servers
   * we gave as input
   * The flow of this is as followed:<br>
   *  1. Encrypt our input data if desired<br>
   *  2. Pad the encrypted data for RS<br>
   *  3. Perform RS on the data to get our N chunks<br>
   *  4. Compute the hashchain for the N chunks<br>
   *  5. Compute the distribution of chunks to servers<br>
   *  6. Shuffle the chunks<br>
   *  7. Execute the uploads of the chunks to their servers<br>
   * @return An array of chunk meta corresponding to all the chunks we uploaded
   */ 
  public ChunkMeta[] store(){
    //1. Encrypt the data
    //Output format:
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
    Hash hash = new Hash();
    Encryption encrypt = enc ? new Encryption() : new NoEncryption();
    data = encrypt.encrypt(data, hash.getHash(key));
    //Store encrypt flag
    data = Pad.prepend(data, 1);
    data[0] = enc ? (byte)1 : (byte)0;
      
    //2. add paddding information to data for RS
    //   Encrypted:                 NoEncrypt:
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
    //TODO 4Ryan: Create array of ChunkMeta objects and for each Chunk make a ChunkMeta for that chunk
    //            where ChunkMeta.name = cname, ChunkMeta.virtual = Chunk.virtual,
    //            ChunkMeta.physical = Chunk.physical   (See: ChunkMeta.java)
    ChunkMeta meta[] = new ChunkMeta[chunk.length];
    while (x < chunk.length){
      final String cname = Hex.toHexString(hash.getHash(concate(fn.getBytes(), chunks[x].hash)));
      meta[x] = new ChunkMeta(cname, chunks[x].virtual, chunks[x].physical);
      pool.submit(new StorageTask(servers[chunks[x].server],
                                  chunks[x],
                                  meta[x],
                                  cname,
                                  StorageTask.TYPE_STORE));
      
      ++x;
    }

    //wait until tasks are done
    pool.shutdown();
    try {
      while(!pool.awaitTermination(60, TimeUnit.SECONDS)); 
    } catch(Exception e) {/* TODO: This is likely an error if it hits */}

    //TODO 4Ryan: Return the ChunkMeta array you created before
    return meta;
  }
}
