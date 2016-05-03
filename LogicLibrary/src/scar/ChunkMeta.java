package scar;

/**
 * ChunkMeta[] is the output of StoreFile and input to GetFile
 * This stores meta data about each chunk associated with the file
 * short of the actual position in the hashchain which is later
 * calculated with the key during that actual process.
 *
 * These Meta data are stored on the applications private encrypted data
 * for later retrivals and also partially in the public monitoring database
 * for checking if the chunk needs to be redistributed to another server 
 */
public class ChunkMeta {
  public final String name;
  public final int virtual;
  public final int physical;
  public final boolean uploaded;
  public ChunkMeta(final String nm, final int v, final int p) {
    name = nm;
    virtual = v;
    physical = p;
    uploaded = false;
  }
}
