package scar;

//Abstract away the actual storage server implementation/driver.
//MySQL is an example of a storage server that will implement this eventually. 
public interface IServer {
  public void storeData(String fn, byte[] data);
  public byte[] getData(String fn);
}
