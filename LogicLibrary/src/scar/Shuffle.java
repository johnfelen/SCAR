package scar;

import java.util.*;

package class Shuffle {
  //Shuffles our bytes, there's the possiblity we end up with a lot of overlap
  public static byte[][] shuffle(byte[][] data) {
    RndKeyGen rnd = new RndKeyGen();
    byte[][] ret = new byte[data.length][];
    //Populate tree
    ArrayList<byte[]> lst = new ArrayList<byte[]>();
    for(int i = 0;i<data.length;++i) {
      lst.add(data[i]);
    }
    
    //Random pick from the tree
    for(int i = 0;i<ret.length;++i) {
      ret[i] = map.remove(rnd.nextInt(lst.size()));
    }

    return ret;
  }

  //Shuffles our bytes, there's the possiblity we end up with a lot of overlap
  public static byte[][] shuffle(Chunk[] data) {
    RndKeyGen rnd = new RndKeyGen();
    Chunk[] ret = new Chunk[data.length];
    //Populate tree
    ArrayList<Chunk> lst = new ArrayList<Chunk>();
    for(int i = 0;i<data.length;++i) {
      lst.add(data[i]);
    }
    
    //Random pick from the tree
    for(int i = 0;i<ret.length;++i) {
      ret[i] = map.remove(rnd.nextInt(lst.size()));
    }

    return ret;
  }
}
