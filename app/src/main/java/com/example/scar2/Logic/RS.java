package com.example.scar2.Logic;

import org.jlinalg.IRingElement;
import org.jlinalg.IRingElementFactory;
import org.jlinalg.Matrix;
import org.jlinalg.RingElement;
import org.jlinalg.Vector;
import org.jlinalg.complex.Complex;
import org.jlinalg.field_p.FieldP;
import org.jlinalg.field_p.FieldPFactoryMap;

public class RS {
  static final long PRIME = 257;

  
  //Only used for decoding to give indices for blocks given
  //These would be created during the process of retrieving data
  //from servers based on their position in the HashChain
  public class Chunk {
    public final byte[] data;
    public final int ind;

    public Chunk(byte[] d, int i) {
      data = d;
      ind = i;
    }
  }


  //See: Figure 22, page 82 from ROMR: Robust Multicast Routing In Mobile Ad-HOC Networks 
  // for more details
  public Matrix makeEncodingMatrix(int k, int n) {
    Vector a[], b[], e[];
    IRingElement row[];
    int i,j;
    
    IRingElementFactory fact = new IRingElementFactory(PRIME);
    
    IRingElement zero = fact.get(0);
    IRingElement one = fact.get(1);
    IRingElement two = fact.get(2);


    //A(i,j) = (2^(n-k+1))^j for i = 0...n-k-1 and j = 0...(k-1)
    a = new Vector[k];
    for(i=0; i < n-k; ++i) {
      row = new IRingElement[k];
      for(j=0; j < k; ++j) {
        row[j] = pow(two, 
                     fact.get(n).subtract(fact.get(k)).add(fact.get(i)).multiply(fact.get(j)));
      }
      a[i] = new Vector(row);
    }

    b = new Vector[k];
    //B(0,0) = 1
    //B(0,j) = 0 for j = 1...(k-1)
    row = new IRingElement[k];
    row[0] = one;
    for(i=1; i < k; ++i)
      row[i] = zero;
    
    //B(i,j) = (2^(i-1))^j for i = 1...(k-1) and j = 0...(k-1)
    for(i=0; i < k; ++i) {
      row = new IRingElement[k];
      for(j=0; j < k; ++j) {
        row[j] = pow(two,
                     fact.get(i).subtract(one).multiply(fact.get(j)));
      }
      b[i] = new Vector(row);
    }

    //C = A * B^(-1)
    Matrix C = (new Matrix(A)).multiply((new Matrix(B)).inverse());
    
    //E(0-(k-1), j) = Identity Matrix
    e = new Vector[n];
    for(i=0; i < k; ++i) {
      row = new IRingElement[k];
      for(j=0; j < k; ++j) {
        row[j] = i != j ? zero : one;
      }
    }
    //E(k, j) = C
    for(i=k; i < n; ++i) {
      row[i] = C.getRow(i-k+1);
    }
    
    return new Matrix(e);
  }

  //Transforms a byte array into a K x Size Matrix
  public Matrix makeDataMatrix(byte[] data, int k) {
    int size = data.length / k ;//Size of each chunk
    Vector a[] = new Vector[k];
    IRingElementFactory fact = new IRingElementFactory(PRIME);
    IRingElement row[];
    int i, j;
   
    //Transform data array into a K x Size Matrix
    //Basically transforming the 1D array into a 2D.
    for(i=0; i < k; ++i) {
      row = new IRingElement[size];
      for(j=0; j < size; ++j) {
        row[j] = elements.get(data[(i * size) + j]);
      }
      a[i] = new Vector(row);
    }

    return new Matrix(a);
  }

  //Tranforms Rows x Cols matrix into byte[Rows][Cols]
  public byte[][] matrixTo2DBytes(Matrix m) {
    int 
      rows = m.getRows(),
      cols = m.getCols(),
      i,j;
    byte[][] data = new byte[rows][cols];
    
    for(i=0; i < rows; ++i) {
      for(j=0; j < cols; ++j) {
        data[i][j] = Integer.parseInt(m.get(i+1,j+1).toString("m")[0]);
      }
    }
    
    return data;
  }

  //Tranforms Rows x Cols matrix into byte[Rows*Cols]
  public byte[] matrixToBytes(Matrix m) {
    int 
      rows = m.getRows(),
      cols = m.getCols(),
      i,j;
    byte[] data = new byte[rows*cols];
    
    for(i=0; i < rows; ++i) {
      for(j=0; j < cols; ++j) {
        data[(i*cols) + j] = Integer.parseInt(m.get(i+1,j+1).toString("m")[0]);
      }
    }
    
    return data;
  }

  //Reverses the order of the matrix rows
  // 1 -> n
  //...
  // n -> 1
  public void reverseOrder(Matrix m) {
    int i, rows = m.getRows();
    for(i=0;i<rows/2;++i) {
      Vector row = m.getRow(i+1);
      Vector row2 = m.getRow(rows-i);
      m.setRow(rows-i, row);
      m.setRow(i+1, row2);
    }
  }

  //Creates a new matrix made up of rows selects from an existing matrix
  public Matrix selectRowsFromMatrix(Matrix m, int row_ids[]) {
    Vector v[] = new Vector[row_ids.length];
    int i;
    
    for(i=0;i<row_ids.length;++i) {
      v[i] = m.getRow(row_ids[i]+1);
    } 
    
    return new Matrix(v);
  }
  
  //Make a matrix from k chunks of data
  public Matrix makeMatrixFromChunks(Chunk chunks[], int k) {
    Vector a[] = new Vector[k];
    IRingElementFactory fact = new IRingElementFactory(PRIME);
    IRingElement row[];
    int i, j;

    for(i=0;i<k;++i) {
      row = new IRingElement[chunks[i].data.length];
      for(j=0;j<chunks[i].data.length;++j) {
        row[j] = fact.get(chunks[i].data[j]);
      }
      a[i] = new Vector(row);
    }
    
    return new Matrix(a);
  }

  //Algorithm: EncodedChunks = EncodeMatrix * DataMatrix
  public byte[][] encode(byte[] data, int k, int n) { 
    Matrix encoder = makeEncodingMatrix(k,n);
    Matrix data = makeDataMatrix(data, k);
    
    Matrix ret = encoder.multiply(data);
    
    return matrixTo2DBytes(ret); 
  }

  
  ///Algorithm: 
  // 1. Take K chunks in order of their index
  // 2. Make Encoding matrix [encode]
  // 3. Select the rows in Encoding that correspond to our K chunks
  // 4. Make K chunks into Matrix [data]
  // 5. reverse encode and data
  // 6. multiply encode and data 
  // 7. convert result to byte array
  public byte[] decode(Chunk[] chunks, int k, int n) {
    int i, rows[];
    
    Matrix encoder = makeEncodingMatrix(k,n);
    
    rows = new int[k];
    for(i=0;i<k;++i) {
      rows[i] = chunks[i].ind;
    }

    encoder = selectRowsFromMatrix(encoder, rows);

    Matrix data = makeMatrixFromChunks(chunks, k);

    reverseMatrix(encoder);
    reverseMatrix(data);

    return matrixToBytes(encoder.multiply(data));
  }
}
