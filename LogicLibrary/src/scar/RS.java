package scar;


public class RS {
  private static GaloisField field = GaloisField.getInstance();
  static final long PRIME = 257;

  //See: Figure 22, page 82 from ROMR: Robust Multicast Routing In Mobile Ad-HOC Networks 
  // for more details
  public Matrix makeEncodingMatrix(final int k, final int n) {
    int a[][], b[][], e[][];
    int i,j;
    
    //A - (n-k) x k matrix
    //A(i,j) = (2^(n-k+1))^j for i = 0..n-k-1 and j = 0...(k-1)
    a = new int[n-k][k];
    for(i=0;i<a.length;++i) {
      for(j=0;j<k;++j) {
        a[i][j] = field.power(field.power(2, field.add(field.subtract(n, k), i)), j);
      }
    }

    //B - k x k matrix
    //B(0,0) = 1, B(0,j) = 0 for j = 1...(k-1)
    //B(i,j) = (2^(i-1))^j for i = 1..(k-1) and j = 0...(k-1) 
    b = new int[k][k];
    b[0][0] = 1;
    for(j = 1; j < k; ++j)
      b[0][j] = 0;
    for(i=1;i<k;++i) {
      for(j=1;j<k;++j) {
        b[i][j] = field.power(field.power(2, field.subtract(i,1)), j);
      }
    }

    Matrix A = new Matrix(a), B = new Matrix(b);
    //C = A * B^-1  - n-k x k matrix
    int c[][] = null;
    try {
      c = A.multiply(B.inverse()).getData();
    } catch(Exception ee) {
      ee.printStackTrace();
      System.exit(1);
    }

    //E - n x k matrix
    //first k rows = identity 
    int id[][] = Matrix.identity(k).getData();
    e = new int[n][k];
    for(i=0;i<k;++i)
      e[i] = id[i];
    //last (n-k) rows = matrix C
    for(i=k;i<n;++i)
      e[i] = c[i-k];
    
    return new Matrix(e);
  }
  
  //Transforms a byte array into a K x Size Matrix
  public Matrix makeDataMatrix(byte[] data, int k) {
    int size = data.length / k;
    int a[][] = new int[k][size];
    int i,j;
    
    for(i=0;i<k;++i) {
      for(j=0;j<size;++j) {
        a[i][j] = data[(i * size) + j];
      }
    }

    return new Matrix(a);
  }

  //Tranforms Rows x Cols matrix into byte[Rows][Cols]
  public byte[][] matrixTo2DBytes(Matrix m) {
    int 
      rows = m.rows(),
      cols = m.cols(),
      i,j;
    int back[][] = m.getData();
    byte[][] data = new byte[rows][cols];
   
    
    for(i=0; i < rows; ++i) {
      for(j=0; j < cols; ++j) {
        data[i][j] = (byte)back[i][j];
      }
    }
    
    return data;
  }

  //Tranforms Rows x Cols matrix into byte[Rows*Cols]
  public byte[] matrixToBytes(Matrix m) {
    int 
      rows = m.rows(),
      cols = m.cols(),
      i,j;
    int back[][] = m.getData();
    byte[] data = new byte[rows*cols];
   
    
    for(i=0; i < rows; ++i) {
      for(j=0; j < cols; ++j) {
        data[(i*cols) + j] = (byte)back[i][j] ;
      }
    }
    
    return data;
  }

  //Reverses the order of the matrix rows
  // 1 -> n
  //...
  // n -> 1
  public void reverseMatrix(Matrix m) {
    int i, rows = m.rows();
    for(i=0;i<rows/2;++i) {
      m.swapRows(i, rows-i-1);
    }
  }

  //Creates a new matrix made up of rows selects from an existing matrix
  public Matrix selectRowsFromMatrix(Matrix m, int row_ids[]) {
    int v[][] = new int[row_ids.length][];
    int i;
    
    for(i=0;i<row_ids.length;++i) {
      v[i] = m.row(row_ids[i]);
    } 
    
    return new Matrix(v);
  }
  
  //Make a matrix from k chunks of data
  public Matrix makeMatrixFromChunks(Chunk chunks[], int k) {
    int a[][] = new int[k][chunks[0].data.length];
    int i, j;

    for(i=0;i<k;++i) {
      for(j=0;j<chunks[i].data.length;++j)
        a[i][j] = chunks[i].data[j];
    }
    
    return new Matrix(a);
  }

  //Algorithm: EncodedChunks = EncodeMatrix * DataMatrix
  public byte[][] encode(byte[] data, int k, int n) { 
    Matrix encoder = makeEncodingMatrix(k,n);
    Matrix dm = makeDataMatrix(data, k);
    
    Matrix ret = encoder.multiply(dm);
    
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
    
    
    try {
      return matrixToBytes(encoder.inverse().multiply(data));
    } catch(Exception e) { 
      e.printStackTrace();
      System.out.println("Failed to decode data");
    } 
    return null;
  }
}
