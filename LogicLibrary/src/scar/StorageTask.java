/**
 * Handles actual uploading and downloading of a chunk to a server
 */
package scar;

import java.util.Random;
import java.util.concurrent.Callable;

public class StorageTask implements Callable<Chunk>{
  /**
   * Types of the task that can be done
   */
  public static final int
    TYPE_STORE = 0,
    TYPE_GET = 1;

  private final IServer srv;
  private final Chunk chk;
  private final ChunkMeta meta;
  private final String nm;
  private final int ty;

  /**
   * Initializes this Storage Task to be run later on through a Java thread pool
   * @param s The server for this task
   * @param c The chunk for this task
   * @param meta The chunk's meta data for this task
   * @param name the name of this chunk
   * @param type the type of task TYPE_STORE or TYPE_GET
   */
  public StorageTask(final IServer s, final Chunk c, final ChunkMeta meta, final String name, final int type) {
    srv = s;
    chk = c;
    nm = name;
    ty = type;
    this.meta = meta;
  }

  /**
   * Performs this storage task 
   * General algorithm:
   *  1. sleep for random period of time between 50ms-1050ms
   *  2. perform the upload/download
   *  3.1. If TYPE_STORE, set whether the upload was successful or not  
   *  3.2. return the chunk if successful, only matters for TYPE_GET
   * @return The chunk downloaded in the case of a TYPE_GET. Return for a TYPE_STORE is meaningless. 
   */
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
    case TYPE_GET:
      byte[] data = srv.getData(nm);
      if(data != null)
        return new Chunk(data,
                         chk.hash,
                         chk.ind,
                         chk.server);
      else
        return null; //failed to get data
    }

    return null;
  }
}
