package scar;
  
//Only used for decoding to give indices for blocks given
//These would be created during the process of retrieving data
//from servers based on their position in the HashChain
public class Chunk {
  public final byte[] data;
  public final byte[] hash;
  public final int ind;
  public final int server;
  
  public Chunk(final byte[] d, final byte[] h, final int i, final int srv) {
    data = d;
    hash = h;
    ind = i;
    server = srv;
  }
}
