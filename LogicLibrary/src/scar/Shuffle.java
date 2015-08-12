package scar;

import java.util.*;

public class Shuffle {
  //Shuffles our bytes, there's the possiblity we end up with a lot of overlap
  public static Chunk[] shuffle(Chunk[] data) {
    RndKeyGen rnd = new RndKeyGen(); 
    ArrayList<Chunk> lst = new ArrayList<Chunk>(Arrays.asList(data));
    Collections.shuffle(lst, rnd.rnd);
    return lst.toArray(new Chunk[0]);
  }
}
