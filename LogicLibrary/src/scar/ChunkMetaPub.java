package scar;

public class ChunkMetaPub {
    public final int chunkID;
    public final int virtual;
    public final int physical;
    public boolean uploaded;
    public ChunkMetaPub(final int cID, final int v, final int p) {
        chunkID = cID;
        virtual = v;
        physical = p;
        uploaded = false;
    }
}
