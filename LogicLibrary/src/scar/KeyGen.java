package scar;

import org.spongycastle.crypto.prng.*;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.macs.HMac;

//Used to generate random bytes of various lengths based on keys as seed
public class KeyGen {
  SP800SecureRandom rnd;
  
  public KeyGen() {
    SP800SecureRandomBuilder rndbuild = new SP800SecureRandomBuilder();
    rnd = rndbuild.buildHMAC(new HMac(new SHA256Digest()), null, false);
  }
  
  public byte[] genBytes(int bytes) {
    byte[] ret = new byte[bytes];
    rnd.nextBytes(ret);
    return ret;
  }
  
  public byte nextByte(int max) {
    byte n = genBytes(1)[0];
    return (byte)(n % max);
  }
  
  public void seed(byte[] seed) {
    rnd.setSeed(seed);
  }
}
