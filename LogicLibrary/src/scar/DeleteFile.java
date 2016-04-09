package scar;

import java.math.*;
import java.util.*;
import java.util.HashSet;
import java.util.concurrent.*;
import org.spongycastle.util.encoders.Hex;

public class DeleteFile
{
    private String fn;
    private IServer servers[];
    private byte[] key;
    int n;

    public DeleteFile(String fn, byte[] password, IServer srvs[], int num)
    {
        this.fn = fn;
        servers = srvs;
        key = password;
        n = num;
    }

    private int lookupMeta(ChunkMeta metas[], String name)
    {
        int i;
        for(i=0;i<metas.length;++i)
            if(metas[i].name.equals(name))
                return i;
        return -1; //should never reach here unless the get-file key is wrong
    }

    private Chunk[] distribute_chunks(byte[][] hc, ChunkMeta[] cms)
    {
        ArrayList<Integer> tmp = new ArrayList<Integer>(n);
        Chunk[] chunks = new Chunk[n+StoreFile.added_garbage];
        int i, srv;

        //Setup pool
        for(i=0;i<n+StoreFile.added_garbage;++i) {
            tmp.add(i);
        }

        //Round Robin chunks to servers based on hash chain
        srv = 0;
        for(i=0;i<n+StoreFile.added_garbage;++i) {
            final BigInteger num = new BigInteger(Hex.toHexString(hc[i]), 16);
            final int ind = num.mod(new BigInteger(Integer.toString(tmp.size()))).intValue();
            final String name = Hex.toHexString(new Hash().getHash(concate(fn.getBytes(), hc[i])));
            int meta = lookupMeta(cms, name);
            chunks[i] = new Chunk(null,
                    hc[i],
                    tmp.remove(ind),
                    cms[meta].virtual,
                    cms[meta].physical,
                    srv);
            srv = (srv + 1) % servers.length;
        }

        return chunks;
    }

    public void delete(final ChunkMeta cms[]) throws Exception
    {
        Hash hash = new Hash();
        byte[][] hashArr = hash.hashchain(n + StoreFile.added_garbage, key);
        Chunk[] chunk_data = distribute_chunks(hashArr, cms);

        int numOfServ = servers.length;

        killChunks(chunk_data, cms);

    }


    private void killChunks(Chunk[] data, ChunkMeta[] metas) {
        Hash hash = new Hash();
        ExecutorService pool = Executors.newFixedThreadPool(StoreFile.allowed_threads);
        for(int x = 0;x < n + StoreFile.added_garbage; ++x) {
                final String name = Hex.toHexString(hash.getHash(concate(fn.getBytes(), data[x].hash)));
                //System.out.println(name);
                int meta = lookupMeta(metas, name);
               // System.out.println("in loop! " + meta);
                if(meta >= 0) {
                    pool.submit(new StorageTask(servers[metas[meta].physical],
                            null, //data[x]
                            metas[meta],
                            name,
                            StorageTask.TYPE_DELETE));
                }
            }
             pool.shutdown();
        }

    public byte[] concate(byte[] a, byte[] b) {
        //Returns [a1,...,a_n,b1,...,b_n]
        byte[] ret = new byte[a.length+b.length];
        System.arraycopy(a, 0, ret, 0, a.length);
        System.arraycopy(b, 0, ret, a.length, b.length);
        return ret;
    }

}

