package scar;
  
//Only used for decoding to give indices for blocks given
//These would be created during the process of retrieving data
//from servers based on their position in the HashChain

/**
 * Chunk stores all the data and information related to a chunk during GetFile/StoreFile
 * for use in Downloading/Uploading the chunk itself and for reassembly information
 * during RS decode
 */
public class Chunk {
  public final byte[] data;
  public final byte[] hash;
  public final int virtual;
  public final int physical;
  public final int ind;
  public final int server;
  
  public Chunk(final byte[] d, final byte[] h, final int i, final int v, final int p, final int srv) {
    data = d;
    hash = h;
    virtual = v;
    physical = p;
    ind = i;
    server = srv;
  }
}
