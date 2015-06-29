package scar;

public class StoreFile {
  private IServer servers[];
  private byte[] data;
  private String
    fn,
    password,
    key;
  private int
    buffer,
    k,
    n;

  public StoreFile(byte[] data, String fn, String password,
                   int k, int n, IServer srvs[]) {
    this.data = data;
    this.fn = fn;
    this.password = password;
    this.buffer = buffer;
    this.k = k;
    this.n = n;
    Hash hash = new Hash();
    key = hash.getHashKey(fn + password);
    servers = srvs;
  }

  //Encode 'val' into 'arr' starting at location 'n'
  public void eint(byte[] arr, int n, int val) {
    arr[n]   = (byte)((val & 0x000000FF) >> 0);
    arr[n+1] = (byte)((val & 0x0000FF00) >> 8);
    arr[n+2] = (byte)((val & 0x00FF0000) >> 16);
    arr[n+3] = (byte)((val & 0xFF000000) >> 24);
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
    
    //1. Encrypt the data
    Encryption encrypt = Encryption.getInstance();
    data = encrypt.encrypt(data, key);

    //2. add header information to data
    //   _______________________
    //  |# of Pad bytes [int32] |
    //  |-----------------------|
    //  |Data data.length bytes |
    //  |_______________________|
    data = Pad.prepend(data, 4);

    //3. pad data with (ceiling(data.length/k) - data.length)*k = len
    int padding = ((int)Math.ceil(data.length/k) *k) - data.length;
    data = Pad.append(data, padding);

    //4. Set header bytes to the correct value as computed in (2)
    eint(data, 0, padding);

    //5. Encode data with RS.encode(data, k, n) -> return chunks[n][len]
    RS rs = new RS();
    byte[][] chunk = rs.encode(data, k, n);
    
    //TODO: for Ryan
    //6. Compute HashChain 
    //See: Hash.class
    //Initial hash: fn, password

    Hash hashChain = new Hash();
    hashChain.recursiveKey(n, key, "");
    ArrayList<String> hashArr = hashChain.getArr();

    //7. Store each chunk to it's correct server with filename 
    // chunk[i] corresponds to HashChain_i and belongs at Server_{HashChain_i % NumberOfServers}
    //See: IServer.class for server functions, servers Variable @ top
    //See: Hash.class

    int x = 0;
    int numOfServ = servers.length;
    while (x <= chunk.length){
      BigInteger num = new BigInteger(hashArr.get(x), 16);
      int i = num.mod(numOfServ).intValue();

      servers[i].store(hashChain.getHashKey(hashArr.get(x)), chunk[x]);
2
      ++x;
    }
  }
}
