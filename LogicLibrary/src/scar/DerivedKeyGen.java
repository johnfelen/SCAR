package scar;

import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.KeyParameter;

//Used to generate keys from other keys
public class DerivedKeyGen {
  public static final int SALT_SIZE = 32;
  public static final int DEFAULT_ITER = 64000;

  private PKCS5S2ParametersGenerator gen;
  
  public DerivedKeyGen() {
    gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
  }

  /* Generates a new derived key from our given key
   */
  public byte[] generateKey(byte[] key, byte[] salt, int keysize) {
    byte[] pack = generateKeyPackage(key, salt, keysize);
    byte[] gkey = new byte[pack.length-SALT_SIZE];
    System.arraycopy(pack, SALT_SIZE, gkey, 0, gkey.length);
    return gkey;
  }

  /* Generates a new derived key from our given key 
   * Returns the key + generated salt
   *   ____________________
   *  | n-byte Salt        |
   *  |--------------------|
   *  | m-byte derived key |
   *  |____________________|
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

  public byte[] generateKeyPackage(byte[] key, int keysize) {
    return generateKeyPackage(key, null, keysize);
  }
}
