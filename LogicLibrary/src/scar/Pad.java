package scar;


public class Pad {
  //Prepends 'data' array with 'n' bytes, only defined for n > 0
  public static byte[] prepend(byte[] data, int n) {
    if(n <= 0)
      return data;

    byte[] ret = new byte[data.length+n];
    System.arraycopy(data, 0, ret, n, data.length);
    return ret;
  }

  //Appends 'data' array with 'n' bytes, only defined for n > 0
  public static byte[] append(byte[] data, int n) {
    if(n <= 0)
      return data;

    byte[] ret = new byte[data.length+n];
    System.arraycopy(data, 0, ret, 0, data.length);
    return ret;
  }
}
