package scar.pc;

public class SimpleDemo {
  public static void get(String fn, String password, int k, int n) {

    //like StoreFile, GetFile takes a int buffer.  where is this coming from?

    GetFile gf = new GetFile(fn, password, k, n);
    gf.get();

  }

  public static void store(String path, String fn, String password, int k, int n) {

    //SToreFile.java's constructor takes more parameters than this function takes it
    //like the byte[], int buffer, and the IServers[]??  Create them here?

    StoreFile sf = new StoreFile(fn, password, k, n);
    sf.store();

  }
  
  //Usage: SimpleDemo -get FILENAME PASSWORD K N
  //                  -store FILE-PATH FILENAME PASSWORD K N
  public static void main(String args[]) {

    ///using place holders right now for the demo
    String path;
    String fn = "testFile";
    String password = "securePass";
    int k;
    int n;

    store(path, fn, password, k, n);
    get(fn, passowrd, k, n);

    //are we displaying the output for the demo?
  }
}
