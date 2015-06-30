package scar;
  
//Only used for decoding to give indices for blocks given
//These would be created during the process of retrieving data
//from servers based on their position in the HashChain
public class Chunk {
  public final byte[] data;
  public final int ind;
  
  public Chunk(byte[] d, int i) {
    data = d;
    ind = i;
  }
}
