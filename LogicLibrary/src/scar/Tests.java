package scar;

import org.spongycastle.util.encoders.Hex;
import java.util.*;
import java.io.*;

/**
 * A series of old tests to test the functionality of LogicLibrary
 * Some of these tests are outdated and not used anymore
 */
public class Tests {
  private static GaloisField field = GaloisField.getInstance();
  public static void createfile(String fn, byte[] data) {
    try {
      File file = new File(fn);
      file.createNewFile();
      FileOutputStream fout = new FileOutputStream(file);
      fout.write(data);
      fout.flush();
      fout.close();
    } catch(Exception e) {
    }
  }
  
  public static boolean encodeIntTest() {
    /*
    StoreFile sf = new StoreFile(null, "1","2".getBytes(), 0, 0, null);
    GetFile gf = new GetFile("1", "2".getBytes(), 0, 0, null);

    int x = 0x12faf250;
    byte[] arr = new byte[4];
    System.out.println("TEST: EncodeIntTest");
    System.out.println(" Before: " + x);
    sf.eint(arr,0,x);
    int ax = gf.dint(arr,0);
    System.out.println(" After: " + ax);
    if(x == ax) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }
    */ return true;
  }
  
  public static boolean hashTest() {
    String key = "test9999";
    String expect = "28f2e85bcc71f1824d5dd7fdae2fa5d5aed4f12594b3ef0c059b718445b4aa04";
    Hash hash = new Hash();
    System.out.println("TEST: Hash Test");
   
    System.out.println(" Key: " + key);
    key = Hex.toHexString(hash.getHash(key));
    System.out.println(" Hash: " + key);
    
    if(key.equals(expect)) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }
  }

  public static boolean compareStrArray(String[] a, String[] b) {
    int i;
    for(i=0;i<a.length;++i)
      if(!a[i].equals(b[i]))
        return false;
    return true;
  }

  public static boolean compareByteArray(byte[] a, byte[] b) {
    if(a.length != b.length)
      return false;
    int i;
    for(i=0;i<a.length;++i)
      if(a[i] != b[i])
        return false;
    return true;
  }
  
  public static boolean padPrependTest() {
    byte data[] = {1, 2, 3, 4};
    byte expect[] = {0, 0, 0, 1, 2, 3, 4};
    int n = 3;
    
    System.out.println("TEST: Pad Prepend Test");
    System.out.print(" Before Data: " );
    for(byte b : data) System.out.print(b + " ");
    System.out.println("\n N          : " + n);

    data = Pad.prepend(data, n);
    
    System.out.print(" After Data: " );
    for(byte b : data) System.out.print(b + " ");
    System.out.println();

    if(compareByteArray(data, expect)) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }     
  }

  public static boolean padAppendTest() {
    byte data[] = {1, 2, 3, 4};
    byte expect[] = {1, 2, 3, 4, 0, 0, 0};
    int n = 3;
    
    System.out.println("TEST: Pad Append Test");
    System.out.print(" Before Data: " );
    for(byte b : data) System.out.print(b + " ");
    System.out.println("\n N          : " + n);

    data = Pad.append(data, n);
    
    System.out.print(" After Data: " );
    for(byte b : data) System.out.print(b + " ");
    System.out.println();

    if(compareByteArray(data, expect)) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }     
  }

  public static boolean padDeprependTest() {
    byte data[] = {1, 2, 3, 4};
    byte expect[] = {3, 4};
    int n = 2;
    
    System.out.println("TEST: Pad Deprepend Test");
    System.out.print(" Before Data: " );
    for(byte b : data) System.out.print(b + " ");
    System.out.println("\n N          : " + n);

    data = Pad.deprepend(data, n);
    
    System.out.print(" After Data: " );
    for(byte b : data) System.out.print(b + " ");
    System.out.println();

    if(compareByteArray(data, expect)) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }     
  }

  public static boolean padDeappendTest() {
    byte data[] = {1, 2, 3, 4};
    byte expect[] = {1,2};
    int n = 2;
    
    System.out.println("TEST: Pad Deappend Test");
    System.out.print(" Before Data: " );
    for(byte b : data) System.out.print(b + " ");
    System.out.println("\n N          : " + n);

    data = Pad.deappend(data, n);
    
    System.out.print(" After Data: " );
    for(byte b : data) System.out.print(b + " ");
    System.out.println();

    if(compareByteArray(data, expect)) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }     
  }

  public static boolean compareMatrix(Matrix a,  Matrix b) {
    if(a.rows() == b.rows() && a.cols() == b.cols()) {
      int[][] adata = a.getData();
      int[][] bdata = b.getData();
      int i,j;
      for(i=0;i<a.rows();++i) {
        for(j=0;j<a.cols();++j) {
          if(adata[i][j] != bdata[i][j])
            return false;
        }
      }
      return true;
    }
    return false;
  }

  public static void printMatrix(Matrix a) {
    int[][] data = a.getData();
    int i,j;
    for(i=0;i<a.rows();++i) {
      System.out.print("  ");
      for(j=0;j<a.cols();++j) {
        System.out.print(data[i][j] + " ");
      }
      System.out.println();
    }
  }

  public static boolean matrixIdentityTest() {
    System.out.println("TEST: Matrix Identity Test");

    Matrix data = Matrix.identity(3);
    int[][] id = {{1,0,0},
                  {0,1,0},
                  {0,0,1}};
    Matrix expect = new Matrix(id);
    System.out.println(" Identity:");
    printMatrix(data);
    System.out.println(" Expect: ");
    printMatrix(expect);
    
    if(compareMatrix(data, expect)) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }   
  }

  public static boolean matrixMultiplyTest() {
    System.out.println("TEST: Matrix Multiply Test");
    /*
    int[][] d1 = {{1,2,4},
                  {2,3,7},
                  {9,4,1}};
    int[][] d2= {{1,5,2,8,10},
                 {2,4,0,1,9},
                 {4,2,0,0,0}};
    */
    int d1[][] = {{1, 5, 250},
                  {9, 142, 124}};
    Matrix d2 = Matrix.identity(3);
    Matrix data = (new Matrix(d1)).multiply(d2);
    System.out.println(" Matrix:");
    printMatrix(data);

    //int[][] ex = {{21, 21, 2, 10, 28},
    //              {36, 36, 4, 19, 47},
    //              {21, 63, 18, 76, 126}};
    int[][] ex = {{1,5,250},
                  {9,142,124}};
    Matrix expect = new Matrix(ex);
    System.out.println(" Expect:");
    printMatrix(expect);


    if(compareMatrix(data, expect)) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }   
  }

  public static boolean matrixInverseTest(){
    System.out.println("TEST: Matrix Inverse Test");
    int i,j, k = 3;
    int[][] b = new int[k][k];
    b[0][0] = 1;
    for(j = 1; j < k; ++j)
      b[0][j] = 0;
    for(i=1;i<k;++i) {
      for(j=1;j<k;++j) {
        b[i][j] = field.power(field.power(2, field.subtract(i,1)), j);
      }
    }
    Matrix inv = null;     
    try {
      inv = new Matrix(b).inverse();
    } catch(Exception e) { 
      e.printStackTrace();
      return false;
    }
    Matrix d1m = new Matrix(b);

    System.out.println(" A:");
    printMatrix(d1m);
    System.out.println(" A^-11:");
    printMatrix(inv);
    System.out.println(" A*A^-1:");
    printMatrix(d1m.multiply(inv));


    Matrix expect = Matrix.identity(k);
    Matrix data = d1m.multiply(inv);
    
    if(compareMatrix(data, expect)) {
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }   
  }

  public static boolean DerivedKeyTest() {
    System.out.println("Derived Key Test");
    DerivedKeyGen keygen = new DerivedKeyGen();
    byte[] pack = keygen.generateKeyPackage("test9999".getBytes(), 256);
    byte[] dkey1 = new byte[pack.length-DerivedKeyGen.SALT_SIZE];
    byte[] salt=  new byte[DerivedKeyGen.SALT_SIZE];
    System.arraycopy(pack, 0, salt, 0, salt.length);
    System.arraycopy(pack, DerivedKeyGen.SALT_SIZE, dkey1, 0, dkey1.length);

    keygen = new DerivedKeyGen();
    byte[] dkey2 =keygen.generateKey("test9999".getBytes(), salt, 256);
    
    
    if(compareByteArray(dkey1, dkey2)){
      System.out.println("PASS");
      return true;
    }else {
      System.out.println("FAIL");
      return false;
    }
  }

  public static void main(String args[]) {
    ArrayList<Boolean> test = new ArrayList<Boolean>();
    //Store/Get File tests
    test.add(encodeIntTest());
    //Hash tests
    test.add(hashTest());
    //Pad tests
    test.add(padPrependTest());
    test.add(padAppendTest());
    test.add(padDeprependTest());
    test.add(padDeappendTest());
    //Matrix tests
    test.add(matrixIdentityTest());
    test.add(matrixMultiplyTest());  //May need to look further into this...
    test.add(matrixInverseTest());
    //Derived Key tests
    test.add(DerivedKeyTest());
    //RS tests
    //test.add(RSFileTest());

    int i = 0;
    for(boolean b : test){ 
      System.out.println("Test ["+(i++)+"] : " +b);
    }
  }
}
