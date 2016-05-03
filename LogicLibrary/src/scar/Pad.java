package scar;


/**
 * Pad handles how to pad data in various ways and to unpad that same data
 */
public class Pad {
  //Prepends 'data' array with 'n' bytes, only defined for n > 0
  /**
   * Prepends data array wiht n bytes 
   * @param data input data
   * @param n number of bytes to prepend into our data
   * @return input data with prepended bytes added
   */
  public static byte[] prepend(byte[] data, int n) {
    if(n <= 0)
      return data;

    byte[] ret = new byte[data.length+n];
    System.arraycopy(data, 0, ret, n, data.length);
    return ret;
  }

  //Appends 'data' array with 'n' bytes, only defined for n > 0
  /**
   * Appends data array wiht n bytes 
   * @param data input data
   * @param n number of bytes to append into our data
   * @return input data with appended bytes added
   */
  public static byte[] append(byte[] data, int n) {
    if(n <= 0)
      return data;

    byte[] ret = new byte[data.length+n];
    System.arraycopy(data, 0, ret, 0, data.length);
    return ret;
  }

  //Removes the last 'n' bytes from data
  /**
   * Removes the last n bytes from data
   * @param data padded input data
   * @param n number of bytes to deappend by
   * @return data deappended by n bytes
   */
  public static byte[] deappend(byte[] data, int n) {
    if(n <= 0)
      return data;
    
    byte[] ret = new byte[data.length-n];
    System.arraycopy(data, 0, ret, 0, ret.length);
    return ret;
  }

  //Removes the first 'n' bytes from data
  /**
   * Removes the first n bytes from data
   * @param data padded input data
   * @param n number of bytes to deprepend by
   * @return data deprepended by n bytes
   */
  public static byte[] deprepend(byte[] data, int n) {
    if(n <= 0)
      return data;
    
    byte[] ret = new byte[data.length-n];
    System.arraycopy(data, n, ret, 0, ret.length);
    return ret;
  }
}
