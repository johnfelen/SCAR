package com.example.scar2.Logic;

public class GetFile {
  private IServer servers[]
    private Wordfile wordfile;
    private String
        fn,
        password;
    private int
        buffer,
        k,
        n;

    public GetFile(byte[] wordFile, String fn, String password, int buffer, 
                   int k, int n, IServer srvs[]) {
      this.wordfile = convertWordFile(wordFile);
        this.fn = fn;
        this.password = password;
        this.buffer = buffer;
        this.k = k;
        this.n = n;
        servers = srvs;
    }

  //Converts the wordfile into a String array
  public Wordfile convertWordFile(byte[] wf) {
    HashSet<String> data = new HashSet<String>();
    StringBuffer sb;
    int i;

    i = 0;
    while(i < wf.length) {
      //Scan in a string [32+]
      sb = new StringBuffer();
      while(wf[i] >= 32 && i < wf.length) {
        sb.append(wf[i]);
        ++i;
      }
      if(sb.length() > 0)
        data.add(sb.toString());

      //Scan past values [0-31]
      while(wf[i] < 32 && i < wf.length) {
        ++i;
      }
    }
    
    return new Wordfile(data.toArray(new String[0]));
  }


    //Get as many blocks from servers, decrypt, apply rs, remove padding
    public byte[] get() {
      //0. Validate input

      //1. Compute HashChain
      //See: Hash.class
      //Initial hash: fn, password
        Hash hash=new Hash();
        hash.recursiveKey(n, fn, password);
        ArrayList<String> hashArr=hash.getArr();

        //2. Get as many chunks as we can from the servers
        //See: IServer.class for getting data from servers[..] given filename
        // Chunk[i] corresponds to hashArr[i] and belongs to servers[hashArr[i] % NumberOfServers]
        // See: Wordfile.get(String) for how to convert hashArr[i] to integer
        //filename = hash.getHashKey(fn + wordFile.get(hashArr[i]))
        //Note: Use RS.Chunk[] class as wrapper for data from server to hold data and index in hashchain
        int numOfServ=2;
        int x=hashArr.size();
        String[][] matrix=new String[x][x];


        //TODO: Corey
        //3. Decrypt each chunk with key: fn+password
        //4. Decode chunks with RS.decode(chunks, k, n) -> return data[..]
        //5. Remove padding
        //Return original input
        return null;
    }
}
