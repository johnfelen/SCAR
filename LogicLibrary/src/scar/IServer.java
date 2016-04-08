package scar;

//Abstract away the actual storage server implementation/driver.
//MySQL is an example of a storage server that will implement this eventually. 
public interface IServer {
  public boolean storeData(String fn, byte[] data);
  public boolean deleteFile(String name);
  public byte[] getData(String fn);
  public int id();
  public boolean getStatus();
  public void close();
}
