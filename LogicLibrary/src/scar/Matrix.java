package scar;

/**
 * Colleciton of Matrix operations needed for @see RS RS to work
 * All operations are done in a @see GaloisField GaloisField with 2^8 elements
 */
public class Matrix {
  private static GaloisField field = GaloisField.getInstance();
  private int[][] matrix;

  /**
   * Initialize a matrix of zeros of size rows by cols
   * @param rows number of rows
   * @param cols number of columns
   */
  public Matrix(int rows, int cols) {
    //Create matrix into a [rows][cols] all to 0
    matrix = new int[rows][cols];
  }

  /**
   * Initialize a copy of another matrix in the form of a 
   * 2d byte array
   * @param data matrix to make copy of
   */
  public Matrix(int[][] data) {
    //Initialize matrix based on data given [data.length][data[0].length]
    matrix = new int[data.length][data[0].length];
    int i,j;
    for(i=0;i<data.length;++i)
      for(j=0;j<data[i].length;++j)
        matrix[i][j] = (data[i][j] & 0x000000FF);
  }

  /**
   * Generates the identity matrix of a specified size
   * @param n number or rows/columns
   * @return Identity matrix of size NxN 
   */
  public static Matrix identity(int n) {
    //Make a N x N identity matrix
    //Identity = (1 0 0 ... 0_n
    //            0 1 0 ... 0_n
    //            0 0 1 ... 0_n
    //            . . . ... ...
    //            0 0 0 ... 1_n)
    //Basically 1's down the diagonal of the matrix, 0 elsewhere
    Matrix newMat = new Matrix(n, n);
    
    byte zero = 0x0;
    byte one = 0x1;
    
    for (int i = 0; i < n; ++i){
      for (int j = 0; j < n; ++j){
        if (i != j){
          newMat.setCell(i, j, zero);
        }
        else{
          newMat.setCell(i, j, one);
        }
      }
    }
    return newMat;
  }

  /**
   * Makes a clone of the current matrix
   * @return a clone of this matrix
   */
  public Matrix clone() {
    //Returns a _copy_ Matrix of this matrix
    Matrix temp = new Matrix(this.getData());
    return temp;
  }

  /**
   * @return this matrix's data in a 2d byte-array
   */
  public int[][] getData() {
    //Return a copy of the matrix data
    int[][] temp = new int [rows()][cols()];
    for (int i = 0; i < this.matrix.length; ++i){
      for (int j = 0; j < this.matrix[i].length; ++j){
        temp[i][j] = matrix[i][j];
      }
    }
    return temp;
  }

  /**
   * Set a specified cell in a matrix
   * @param row given row
   * @param col given column
   * @param val new value for [row][col]
   */
  public void setCell(int row, int col, int val) {
    //Sets the cell at [row][col] to val
    this.matrix[row][col] = val;
  }

  /**
   * @param row given row
   * @param col given column
   * @return returns the cell at a given row and column
   */
  public int cell(int row, int col) {
    //returns val at [row][col]
    return this.matrix[row][col];
  }

  /**
   * @param row given row
   * @return a row vector for the given row from this matrix
   */
  public int[] row(int row) {
    //returns the row @ [row][]
    return this.matrix[row];
  }

  /**
   * Swaps two rows in this matrix
   * @param r1 first row to swap
   * @param r2 second row to swap with r1
   */
  public void swapRows(int r1, int r2) {
    //Swap row r1 into r2 and r2 into r1
    int[] temp = this.matrix[r1];
    this.matrix[r1] = this.matrix[r2];
    this.matrix[r2] = temp;
  }

  public int rows() { return matrix.length; }
  public int cols() { return matrix[0].length; }

  //Matrix Operations
  /**
   * Multiplies two matrices together via matrix multiplication
   * @param other Other matrix to multiply against
   * @return the result of this matrix multipled by other
   */
  public Matrix multiply(Matrix other) {
    //Multiply this matrix against another matrix
    //Algoritm:
    //  C = A * B   (A [X x Y], B [Y x Z], C [X x Z] matrices where X,Y,Z are integers (rows x cols))
    //  X = A.rows()
    //  Y = B.rows()
    //  Z = B.cols()
    //  C[i][j] = Sum_{from k = 0 to Y-1} ( A[i][k] * B[k][j] )
    //Note:
    //  This will be 3 for loops (one for i = 0 ... x-1,
    //                            one for j = 0 ... z-1,
    //                            one for k = 0 ... y-1)
    //  The multiplication of the A, B cells need to be done with
    //  field.multiply( int, int )
    int[][] c = new int[rows()][other.cols()];
    int[][] a = this.matrix;
    int[][] b = other.getData();

    for (int i = 0; i < a.length; ++i){
      for (int j = 0; j < b[0].length; ++j) {
        c[i][j] = 0;
        //  C[i][j] = Sum_{from k = 0 to Y-1} ( A[i][k] * B[k][j] )
        for (int k = 0; k < b.length; ++k){
          c[i][j] = field.add(c[i][j], field.multiply(a[i][k], b[k][j]));
        }
      }
    }

    return new Matrix(c);
  }

  /**
   * @return the inverse of the current matrix via Gauss-Jordan Elimination
   * @throws InvalidInputException if no inverse can be found
   */
  public Matrix inverse() throws InvalidInputException {
    //Returns the inverse of this matrix via Gauss-Jordan Elimination (US | ID) -> (ID | US^-1)
    // Only works if rows = cols (ie: square matrix)
    if(matrix.length == matrix[0].length) {
      //ID matrix will be transformed into the inverse, 
      //given that we (this.matrix) converges into the identity
      Matrix id = identity(matrix.length);
      Matrix us = new Matrix(getData()); // make a copy of ourselves 
      
      int col = 0, row = 0, i, j;
      int maxcol = us.cols(), maxrow = us.rows();
      int cell;
      
      //This will repeat at most matrix.length # of times
      //each iteration will take a column and reduce it to one row containing a '1' and the rest '0'
      //We're estenially transforming 'us' into the identity matrix one column at a time
      //while 'id' tranforming a column at a time into  our inverse
      while( row < maxrow && col < maxcol ) {
        //First we need a row that has a number != 0 in column 'col'
        cell = us.cell(row, col);
        if(cell == 0) {
          //find another row
          int goodrow = row + 1;
          for(;goodrow < maxrow; ++goodrow) {
            cell = us.cell(goodrow, col);
            if(cell != 0) {
              //We found a row that has a number in column 'col'
              //We need to swap this row into the place of 'row'
              //This ensures the identity matrix we're trying to make will have the 1's in
              //the right locations
              us.swapRows(row, goodrow);
              id.swapRows(row, goodrow);
              break; //stop searching
            }
          }
          
          if(goodrow > maxrow) //failed to find a row with a number in it
            throw new InvalidInputException("This matrix can't be inverted");
        }
        
        //At this point we have a row and we need to normalize it based on 'row, col' value
        //ie: (0 0 0 X # # #) -> (0 0 0 1 #/X #/X #/X)
        cell = field.invert(cell);
        for(j=0;j<maxcol;++j) {
          id.setCell(row, j, field.multiply(id.cell(row,j), cell));
          us.setCell(row, j, field.multiply(us.cell(row,j), cell));
        }

        //At this poitn we have a row like (0 0 0 1 #/X #/X #/X) where X was the original value
        //Now we need to make the rows above and below the '1' into 0's
        //Hence why i can claim the columns left of 1 have to be 0
        for(i=0;i<maxrow;++i) {
          cell = us.cell(i, col);
          if(i == row || cell == 0) // skip ourselves.. and rows already with a 0
            continue;
          
          //Subtract row 'i' from our row 'row'
          //'i' - ('row' * cell)
          //This makes cell -> 0 
          for(j=0;j<maxcol;j++) {
            us.setCell(i, j, field.subtract(us.cell(i,j), field.multiply(us.cell(row, j), cell)));
            id.setCell(i, j, field.subtract(id.cell(i,j), field.multiply(id.cell(row, j), cell)));
          }
        }
        

        
        //Search for next row
        row++;
        col++;
      }
      
      //Check us to make sure it's the identity now
      for(i=0;i<maxrow;++i)
        for(j=0;j<maxcol;++j){
          if((j != i && us.cell(i,j) != 0) ||
             (j == i && us.cell(i,j) != 1))
            throw new InvalidInputException("This matrix can't be inverted");
        }

      //return our inverse
      return id;
    } else {
      throw new InvalidInputException("Only a square matrix can be inverted");
    }  
  }  
}
