package scar;

public class GetFile {
  private String
    fn,
    password,
    key;
  private int
    buffer,
    k,
    n;
  private IServer servers[];

  public GetFile(String fn, String password, int k, int n, IServer srvs[]) {
    this.fn = fn;
    this.password = password;
    this.buffer = buffer;
    this.k = k;
    this.n = n;
    Hash hash = new Hash();
    key = hash.getHashKey(fn + password);
    servers = srvs;
  }
  
  public int dint(byte[] data, int start) {
    return 
      data[start] +
      (data[start+1] << 8) +
      (data[start+2] << 16) +
      (data[start+3] << 24);
  }


  //Get as many blocks from servers, decrypt, apply rs, remove padding
  public byte[] get() {
    //1. Compute HashChain
    Hash hashChain = new Hash();
    hashChain.recursiveKey(n, key, "");
    ArrayList<String> hashArr = hashChain.getArr();

    int numOfServ = servers.length;
    ArrayList<RS.Chunk> chunk = new ArrayList<RS.Chunk>();

    //2. Get data
    int x = 0;
    while (x <= hashArr.size()){
      BigInteger num = new BigInteger(hashArr.get(x), 16);
      int i = num.mod(numOfServ).intValue();
      
      byte[] c = servers[i].get(hashChain.getHashKey(hashArr.get(x)));
      if (c !=NULL){
        chunk.add(new Chunk(c, x));
      }
      
      ++x;
    }
    
    //3. Decode k chunks to get encrypted data back
    RS rs = new RS();
    byte[] data = rs.decode(chunk.toArray(new RS.Chunk[0]), k, n);
    
    //4. Get the padding
    int padding = dint(data, 0);
    
    //5. Depad the data & remove header
    data = Pad.deappend(data, padding);
    data = Pad.deprepend(data, 4);

    //6. Decrypt the data
    Encryption decrypt = Encryption.getInstance();
    data = decrypt.decrypt(data, key);

    return data;
  }
}
