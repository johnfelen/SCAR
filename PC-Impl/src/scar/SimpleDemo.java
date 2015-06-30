package scar.pc;

public class SimpleDemo {
  public static void get(String fn, String password, int k, int n) {
    GetFile gf = new GetFile(fn, password, k, n);

    byte[] data = gf.get(); //data => file bytes
    //Make a new file with filename 'fn'
    //Store the data bytes via an FileOutputStream.write for every byte into the file
    File file = new File(fn);
    file.createNewFile();
    FileOutputStream fout = new FileOUtputStream(file);
    fout.write(data);

  }

  public static void store(String path, String fn, String password, int k, int n) {
    File file = new File(fn);
    FileInputStream fin = new FileInputStream(file);
    filesize = file.length();
    byte[] bArr = new byte[filesize];

    //Read in the file bArr[0...len] = fin.read();
    fin.read(bArr);

    StoreFile sf = new StoreFile(bArr, fn, password, k, n);
    sf.store();
  }
  
  //Usage: SimpleDemo -get FILENAME PASSWORD K N
  //                  -store FILE-PATH FILENAME PASSWORD K N
  public static void main(String args[]) {

    String path;
    String fn;
    String password;
    int k;
    int n;

    if(args[0].equals("-store")) {
      path = args[1];
      fn = args[2];
      password = args[3];
      k = args[4];
      n = args[5];
      store(path, fn, password, k, n);
    }
    else if(args[0].equals("-get")) {
      fn = args[1];
      password = args[2];
      k = args[3];
      n = args[4];
      get(fn, password, k, n);
    }
    else {
      //invalid usage
      System.out.println("Invalid usage!")
    }
  }
}
