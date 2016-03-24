package scar;

public class ChunkMeta {
  public final String name;
  public final int virtual;
  public final int physical;
  public boolean uploaded;
  public ChunkMeta(final String nm, final int v, final int p) {
    name = nm;
    virtual = v;
    physical = p;
    uploaded = false;
  }
}
