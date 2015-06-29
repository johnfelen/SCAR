package scar.pc;

public class SimpleDemo {
  public static void get(String fn, String password, int k, int n) {
    //not sure of the interplay here....
    GetFile gf = new GetFile(fn, password, k, n);
    gf.get();

  }

  public static void store(String path, String fn, String password, int k, int n) {
    
    FileInputStream fin = new FileInputStream(new File(fn));
    byte[] bArr = new byte[n];  //assuming byte[] is length n
    fin.read();  //not sure which implementation would be better

    StoreFile sf = new StoreFile(bArr, fn, password, k, n);
    sf.store();

  }
  
  //Usage: SimpleDemo -get FILENAME PASSWORD K N
  //                  -store FILE-PATH FILENAME PASSWORD K N
  public static void main(String args[]) {

    ///using place holders right now for the demo
    String path;
    String fn;
    String password;
    int k;
    int n;

    store(path, fn, password, k, n);
    get(fn, password, k, n);

    //are we displaying the output for the demo?
  }
}
