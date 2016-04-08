package scar;

import java.util.Random;
import java.util.concurrent.Callable;

public class StorageTask implements Callable<Chunk>{
  public static final int
    TYPE_STORE = 0,
    TYPE_GET = 1;
    TYPE_DELETE = 2;

  private final IServer srv;
  private final Chunk chk;
  private final ChunkMeta meta;
  private final String nm;
  private final int ty;
  
  public StorageTask(final IServer s, final Chunk c, final ChunkMeta meta, final String name, final int type) {
    srv = s;
    chk = c;
    nm = name;
    ty = type;
    this.meta = meta;
  }
  
  public Chunk call() {
    Random rnd = new Random(nm.hashCode());
    try {
      Thread.sleep(rnd.nextInt(1000)+50);
    } catch(Exception e) {}
    
    switch(ty) {
    case TYPE_STORE:
      if(srv.storeData(nm, chk.data))
        meta.uploaded = true;
      else
        meta.uploaded = false;
      break;
    case TYPE_DELETE:
      svr.deleteFile(nm);
      break;

    case TYPE_GET:
      byte[] data = srv.getData(nm);
      if(data != null)
        return new Chunk(data,
                         chk.hash,
                         chk.ind,
                         chk.virtual,
                         chk.physical,
                         chk.server);
      else
        return null; //failed to get data
    }

    return null;
  }
}
  //TODO 4Ryan: Adjus
