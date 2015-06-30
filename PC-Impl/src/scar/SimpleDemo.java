package scar.pc;

public class SimpleDemo {
  public static void get(String fn, String password, int k, int n) {   
    GetFile gf = new GetFile(fn, password, k, n);
    
    byte[] data = gf.get(); //data => file bytes
    //Make a new file with filename 'fn'
    //Store the data bytes via an FileOutputStream.write for every byte into the file
  }

  public static void store(String path, String fn, String password, int k, int n) {
    FileInputStream fin = new FileInputStream(new File(fn));
    //filesize = File.length();
    byte[] bArr = new byte[(new File(fn)).length()]; 
    
    //Read in the file bArr[0...len] = fin.read();

    StoreFile sf = new StoreFile(bArr, fn, password, k, n);
    sf.store();
  }
  
  //Usage: SimpleDemo -get FILENAME PASSWORD K N
  //                  -store FILE-PATH FILENAME PASSWORD K N
  public static void main(String args[]) {
    
    //Assign these via their approriate args[index];
    String path;
    String fn;
    String password;
    int k;
    int n;

    if(args[0].equals("-store"))
      store(path, fn, password, k, n);
    else if(args[0].equals("-get"))
      get(fn, password, k, n);
    else {
      //invalid usage
    }
  }
}
