package scar;

public class StoreFile {
  private IServer servers[];
  private byte[] data;
  private Wordfile wordfile;
  private String
    fn,
    password;
  private int
    buffer,
    k,
    n;

  public StoreFile(byte[] data, byte[] wordFile, String fn, String password, 
                   int buffer, int k, int n, IServer srvs[]) {
    this.data = data;
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

  //Encode 'val' into 'arr' starting at location 'n'
  public void eint(byte[] arr, int n, int val) {
    arr[n]   = val & 000000FF;
    arr[n+1] = val & 0000FF00;
    arr[n+2] = val & 00FF0000;
    arr[n+3] = val & FF000000;
  }


  // Prepare data for RS, Apply RS, Apply Encryption, Store blocks properly
  public void store() {
    //0. Validate input
    if(n <= k)
      throw InvalidInputException("N needs to be bigger than K");
    if(wordFile.length < n)
      throw InvalidInputException("Wordfile needs atleast N distinct words");
    if(servers == null)
      throw InvalidInputException("You need atleast one server to store data");
    
    //1. add header information to data
    //   _______________________
    //  |# of Pad bytes [int32] |
    //  |-----------------------|
    //  |Data data.length bytes |
    //  |_______________________|
    data = Pad.prepend(data, 4);

    //2. pad data with (ceiling(data.length/k) - data.length)*k = len
    int padding = ((int)Math.ceil(data.length/k) - (int)(data.length/k)) * k;
    data = Pad.append(data, padding);

    //3. Set header bytes to the correct value as computed in (2)
    eint(data, 0, padding);

    //4. Encode data with RS.encode(data, k, n) -> return chunks[n][len]
    RS rs = new RS();
    byte[][] chunk = rs.encode(data, k, n);

    //5. Encrypt each edata[i] chunk for i = 0 -> n-1 with key: fn+password
    //We can change key if needed later on to be more customized
    Encryption encrypt = Encryption.getInstance();
    for(int i = 0; i < n; ++i) {
      chunk[i] = encrypt(chunk[i], fn+password);
    }
    
    //TODO: for Ryan
    //6. Compute HashChain 
    //See: Hash.class
    //Initial hash: fn, password

    Hash hashChain = new Hash();
    hashChain.recursiveKey(n, fn, password);
    ArrayList<String> hashArr = hashChain.getArr();

    //Still working!

    //7. Store each chunk to it's correct server with filename 
    // chunk[i] corresponds to HashChain_i and belongs at Server_{HashChain_i % NumberOfServers}
    //See: IServer.class for server functions, servers Variable @ top
    //filename = hash.getHashKey(fn + Wordfile.get(HashChain_i))
    //See: Hash.class

    int x = 0;
    int numOfServ = servers.length;
    while (x <= chunk.length){
      BigInteger num = new BigInteger(hashArr.get(x), 16);
      int i = num.mod(numOfServ).intValue());

      servers[i].store(hashChain.getHashKey(fn + Wordfile.get(hashArr.get(x))), chunk[x]);

      ++x;
    }
  }
}
