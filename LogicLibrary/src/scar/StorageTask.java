package scar;

import java.util.Random;
import java.util.concurrent.Callable;

public class StorageTask implements Callable<Chunk>{
  public static final int
    TYPE_STORE = 0,
    TYPE_GET = 1;

  private final IServer srv;
  private final Chunk chk;
  private final String nm;
  private final int ind;
  private final int ty;
  
  public StorageTask(final IServer s, final Chunk c, final String name, final int i, final int type) {
    srv = s;
    chk = c;
    nm = name;
    ind = i;
    ty = type;
  }
  
  public Chunk call() {
    Random rnd = new Random(nm.hashCode());
    try {
      Thread.sleep(rnd.nextInt(1000)+50);
    } catch(Exception e) {}
    
    switch(ty) {
    case TYPE_STORE:
      srv.storeData(nm, chk.data);
      break;
    case TYPE_GET:
      byte[] data = srv.getData(nm);
      if(data != null)
        return new Chunk(data, null, ind);
      else
        return null; //failed to get data
    }

    return null;
  }
}
