package scar;

import java.util.*;

/**
 * Shuffle wrapper for shuffling an array via java Collections.shuffle
 */
public class Shuffle {
  //Shuffles our bytes, there's the possiblity we end up with a lot of overlap
  /**
   * Shuffles input chunks 
   * Note: this may end up with overlap of the original input
   * @param data input chunk array
   * @return shuffled chunk array
   */
  public static Chunk[] shuffle(Chunk[] data) {
    RndKeyGen rnd = new RndKeyGen(); 
    ArrayList<Chunk> lst = new ArrayList<Chunk>(Arrays.asList(data));
    Collections.shuffle(lst, rnd.rnd);
    return lst.toArray(new Chunk[0]);
  }
}
