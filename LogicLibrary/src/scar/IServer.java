package scar;

//Abstract away the actual storage server implementation/driver.
//MySQL is an example of a storage server that will implement this eventually. 

/**
 * Standard interface for servers to download/upload chunks to.
 * All servers givne to GetFile/StoreFile must implement this interface
 */
public interface IServer {
  public boolean storeData(String fn, byte[] data);
  public byte[] getData(String fn);
  public int id();
  public boolean getStatus();
  public void close();
}
