package scar;

import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.KeyParameter;


/**
 * DerivedKeyGen is a wrapper class for Spongy Castles PKCS5S2 (PBKDF2) to generated
 * keys derived from a password with a random salt.
 */
public class DerivedKeyGen {
  /**
   * Salt size as number of bytes
   */
  public static final int SALT_SIZE = 32;
  /**
   * Default number of iterations to run the PBKDF2 algorithm
   */
  public static final int DEFAULT_ITER = 64000;

  private PKCS5S2ParametersGenerator gen;
    
  /**
     Initialize the PKCS5S2 generator
  */
  public DerivedKeyGen() {
    gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
  }

  /** Generates a new derived key from our given key, salt, and desired keysize
   * @param key original key 
   * @param salt  generated salt for this key
   * @param keysize  desired keysize in bytes
   * @return a derived key of size keysize
   */
  public byte[] generateKey(byte[] key, byte[] salt, int keysize) {
    byte[] pack = generateKeyPackage(key, salt, keysize);
    byte[] gkey = new byte[pack.length-SALT_SIZE];
    System.arraycopy(pack, SALT_SIZE, gkey, 0, gkey.length);
    return gkey;
  }

  /** Generates a new derived key from our given key 
   * Returns the key + generated salt<br>
   *   ____________________<br>
   *  | n-byte Salt        |<br>
   *  |--------------------|<br>
   *  | m-byte derived key |<br>
   *  |____________________|<br>
   * @param key  original key 
   * @param salt  generated salt for this key, if null a random one will be generated
   * @param keysize  desired keysize in bytes
   * @return A byte[] of the format specified above
   */
  public byte[] generateKeyPackage(byte[] key, byte[] salt, int keysize) {
    if(salt == null) {
      RndKeyGen rnd = new RndKeyGen();
      salt = rnd.genBytes(SALT_SIZE);
    }
    gen.init(key, salt, DEFAULT_ITER);
    byte[] gkey = ((KeyParameter)gen.generateDerivedParameters(keysize)).getKey();
    byte[] pack = new byte[salt.length + gkey.length];
    
    System.arraycopy(salt, 0, pack, 0, salt.length);
    System.arraycopy(gkey, 0, pack, salt.length, gkey.length);
    
    return pack;
  }


  /** Generates a new derived key from our given key 
   * Returns the key + generated salt<br>
   *   ____________________<br>
   *  | n-byte Salt        |<br>
   *  |--------------------|<br>
   *  | m-byte derived key |<br>
   *  |____________________|<br>
   * @param key  original key 
   * @param keysize  desired keysize in bytes
   * @return A byte[] of the format specified above
   */
  public byte[] generateKeyPackage(byte[] key, int keysize) {
    return generateKeyPackage(key, null, keysize);
  }
}
