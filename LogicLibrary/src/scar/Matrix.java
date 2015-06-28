package scar;

public class Matrix {
  private static GaloisField field = GaloisField.getInstance();
  private byte[][] matrix;
  
  public Matrix(int rows, int cols) {
    //Create matrix into a [rows][cols] all to 0
  }

  public Matrix(byte[][] data) {
    //Initialize matrix based on data given [data.length][data[0].length]
  }

  public static Matrix identity(int n) {
    //Make a N x N identity matrix
    //Identity = (1 0 0 ... 0_n
    //            0 1 0 ... 0_n
    //            0 0 1 ... 0_n
    //            . . . ... ...
    //            0 0 0 ... 1_n)
    //Basically 1's down the diagonal of the matrix, 0 elsewhere
  }

  public Matrix clone() {
    //Returns a _copy_ Matrix of this matrix
  }

  public byte[][] getData() {
    //Return a copy of the matrix data
  }

  public void setCell(int row, int col, int val) {
    //Sets the cell at [row][col] to val
  }

  public int cell(int row, int col) {
    //returns val at [row][col]
  }

  public void swapRows(int r1, int r2) {
    //Swap row r1 into r2 and r2 into r1
  }

  //Matrix Operations
  public Matrix multiply(Matrix other) {
    //Multiply this matrix against another matrix
    //Algoritm:
    //  C = A * B   (A [X x Y], B [Y x Z], C [X x Z] matrices where X,Y,Z are integers (rows x cols))
    //  C[i][j] = Sum_{from k = 0 to Y-1} ( A[i][k] * B[i][k] )
    //Note:
    //  This will be 3 for loops (one for i = 0 ... x-1,
    //                            one for j = 0 ... z-1,
    //                            one for k = 0 ... y-1)
    //  The multiplication of the A, B cells need to be done with
    //  field.multiply( int, int )  
  }

  public Matrix inverse() {
    //Returns the inverse of this matrix
    // Only works if rows = cols (ie: square matrix)
    // Corey will do this
  }


  private int invert(int x) { //multiplicative inverse
    //Inverts a cell with value x
    // x invert = 1 / x
    return field.divide( 1 , x );
  }
  
}