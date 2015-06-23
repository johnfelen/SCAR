package scar;

public class GetFile {
    private String
        fn,
        password;
    private int
        buffer,
        k,
        n;

    public GetFile(String fn, String password, int buffer, int k, int n) {
        this.fn = fn;
        this.password = password;
        this.buffer = buffer;
        this.k = k;
        this.n = n;
    }


    //Get as many blocks from servers, decrypt, apply rs, remove padding
    public void get() {
        Hash hashChain = new Hash();
        hashChain.recursiveKey(n, fn, password);
        ArrayList<String> hashArr = hashChain.getArr();

        int numOfServ = servers.length;
        ArrayList<Chunk> chunk = new ArrayList<Chunk>();

        int x = 0;
        while (x <= hashArr.size()){
            BigInteger num = new BigInteger(hashArr.get(x), 16);
            int i = num.mod(numOfServ).intValue());

            byte[] c = servers[i].get(hashChain.getHashKey(fn + Wordfile.get(hashArr.get(x))));
            if (c !=NULL){
                chunk.add(new Chunk(c, x));
            }

            ++x;
        }
    }
}
