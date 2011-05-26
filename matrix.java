 /**************************************************************************
  * Matrix.java
  * By Stewart Bracken
  * stew.bracken@gmail.com
  *
  * See README for more information
  * 
  * Matrix.java was created by me, Stewart. Feel free to modify and make this
  * code your own. Let me know if you have any questions.
  * Fraction.java and Getopt.java were not created by me, but they are
  * both freely available on the web and I'm sure you can use them youself.
  * 
  **************************************************************************/
  
import java.util.*;
import java.io.*;
import static java.lang.System.*;

class matrix{
   
   // Important reference numbers
   static int rows;
   static int columns;
   static int leadCount = 0;
   static int maxLength;
   static int steps = 1;
   
   // Options and such
   static boolean echelonFlag = false;
   static boolean printStepsFlag = false;
   static boolean forwardPassFinished = false;
   
   // Some useful fractions
   static final Fraction negativeOne = new Fraction( -1, 1 );
   static final Fraction one = new Fraction( 1,1 );
   static final Fraction zero = new Fraction( 0, 1 );
   
   // Usage() prints usage info and exits.
   static void Usage(){
      System.err.println( "Usage: java matrix [-h -e -s] [InputFile]" );
      System.exit( 1 );
   }
    
   // getGrid() will extract integers from input file and put them into a fitting array
   static Fraction[][] getMatrix ( String filename ){
      int rowBuffer = 30;
      int columnBuffer = 30;
      Fraction[][] matrixBuffer = new Fraction[rowBuffer][columnBuffer];
      int rowCt = 0;
      int columnCt = 0;

      try {
         Scanner sc = new Scanner( new File( filename ));
         sc.useDelimiter( System.getProperty( "line.separator" ) );
         while ( sc.hasNext() ) {
            Scanner lineScanner = new Scanner( sc.next() );
            columnCt = 0;
            while ( lineScanner.hasNextInt() ){
               Fraction input = new Fraction( lineScanner.nextInt(), 1 );
               matrixBuffer[rowCt][columnCt] = input;
               columnCt++;
               // Transfer into bigger array if necessary.
               // **THIS HASN'T BEEN TESTED**
               if ( columnBuffer-2==columnCt ){
                  rowBuffer *= 2;
                  columnBuffer *= 2;
                  Fraction[][] bigger = new Fraction[rowBuffer][columnBuffer];
                  for ( int i = 0; i < rowBuffer/2; i++ ){
                     for ( int j = 0; j < columnBuffer/2; j++ ){
                        bigger[i][j] = matrixBuffer[i][j];
                     }
                  }
                  matrixBuffer = bigger;
               }
            }
            rowCt++;
         }
      } catch ( FileNotFoundException e ) {
         System.err.println( e.getMessage() );
         Usage();
      }
      
      // I won't use the 0 positions in the array for easier coordinates.
      // For example, the first position on the top left is (1,1) not (0,0)
      // Remember a matrix is (row, column). For reference, generally "i"'s
      // correspond to the row, and "j"'s to columns, throughout this program.
      // For example matrix[i][j] is the same as (i,j) or row i, column j.
      Fraction[][] matrix = new Fraction[rowCt+1][columnCt+1];
      // Put matrixBuffer contents into newer, better fitting matrix.
      for ( int i = 0; i < rowCt; i++ ){
         for ( int j = 0; j < columnCt; j++ ){
            matrix[i+1][j+1] = matrixBuffer[i][j];
         }
      }
      rows = rowCt;
      columns = columnCt;
      return matrix;
   }

   // forwardPass() brings a matrix to row echelon form.
   static coordinate[] forwardPass ( Fraction[][] matrix ) {
      coordinate[] lastLeading = new coordinate[50];
      for ( int i = 1; i<=rows; i++ ){
         //Find pivot column
         coordinate leftmost = findLeftMost( matrix,i );
         if ( forwardPassFinished ) break;
         leadCount += 1;
         // Exchange pivot column with desired row if it's not in place already.
         if ( matrix[i][leftmost.y].getNumerator() == 0 ){
             exchangeRow ( matrix, i, leftmost.x );
             leftmost.x = i;
             if ( printStepsFlag ) {
                out.printf( "%d. ", steps ); steps++;
                out.printf ( "Exchanging rows %d <--> %d\n", i, leftmost.x );
                printMatrix( matrix );
             }
         }
         leftmost.addCoord( lastLeading );
         //Scale pivot row to 1
         if ( ( matrix[i][leftmost.y].getNumerator() != 1 ) ||
               (matrix[i][leftmost.y].getDenominator() != 1 ) ){
               //Multiply row by 1/a1j
               Fraction scalar = new Fraction();
               scalar = matrix[i][leftmost.y].inverse( matrix[i][leftmost.y] );
               scaleRow( matrix, i, scalar );
               if ( printStepsFlag ) {
                  out.printf( "%d. ", steps ); steps++;
                  out.printf( "Scaling R%d by ", i, leftmost.x );
                  scalar.output(); out.printf( "\n" );
                  printMatrix( matrix );
                }
         }
         //Zero all entries in column j below Pivot entry
         for ( int k = i+1; k <= rows; k++ ){
            if ( matrix[k][leftmost.y].getNumerator() != 0 ){
               Fraction scalar = new Fraction();
               scalar = matrix[k][leftmost.y].multiply( negativeOne );
               eliminateRow( matrix, i, k, scalar );
               if ( printStepsFlag ) {
                  out.printf( "%d. ", steps ); steps++;
                  out.printf( "R%d = ", k );
                  scalar.output();
                  out.printf ( " * R%d + R%d\n", i, k );
                  printMatrix( matrix );
                }
            }
         }
      }
      //Forwar pass done, return the array with stored pivot positions.
      return lastLeading;
   }
   
   // backwardsPass() finishes the reduction by bringing the matrix to Reduced
   // Row Echelon Form.
   static void backwardsPass ( Fraction[][] matrix, coordinate[] lastLeading){
      // Begin at the last pivot entry
      int i = leadCount-1;
      while ( i > 0 ){
         // Don't do this for Row 1, as there is nothing above row 1.
         if ( lastLeading[i].x > 1 ){
            for ( int k = lastLeading[i].x-1; k > 0; k-- ){
               if ( matrix[k][lastLeading[i].y].getNumerator() != 0 ){
                  // Zero all entries above pivot columns with scalar eliminaton
                  Fraction scalar = new Fraction();
                  scalar = matrix[k][lastLeading[i].y].multiply( negativeOne );
                  eliminateRow( matrix, lastLeading[i].x, k, scalar );
                  if ( printStepsFlag ) {
                     out.printf( "%d. ", steps ); steps++;
                     out.printf( "R%d = ",k );
                     scalar.output();
                     out.printf ( " * R%d + R%d\n", lastLeading[i].x,k );
                     printMatrix( matrix );
                   }
               }
            }
         }
         i--;
      }
      return;
   }
   
   
   //This is not always relevant since not all matrices are augmented and solveable.
   //Fuctionality deprecated.
   static void printSystem ( Fraction[][] matrix ) {
      int spaces;
      out.printf( "\n" );
      for ( int i = 1; i <= rows; i++ ){
         for ( int j =1; j <= columns; j++ ) {
            if ( j != columns ){
               if ( matrix[i][j].getNumerator() != 0 ){
                  spaces = maxLength - matrix[i][j].length()-2;
               } else {
                  spaces = maxLength+2;
               }
               for ( ; spaces > 0; spaces-- ) {
                  out.printf( " " );
               }
               //if (j!=column)
               if ( matrix[i][j].equals( one ) ) {
                  //matrix[i][j].output();
                  out.printf( "X%d + ", j );
               } else if ( matrix[i][j].getNumerator() != 0 ) {
                  matrix[i][j].output();
                  out.printf( "X%d", j );
                  //if j
               }
            }else {
               out.printf( "=  " );
               matrix[i][j].output();
               out.printf( "\n" );
            }
         }
      }
   }
   
   // Used for debugging.
   static void printArray( coordinate[] A ){
      System.out.print( "[ " );
      for( int i = 0; i < A.length; i++ ){
         if ( A[i] == null ) break;
         A[i].printCoord();
         out.printf( " " );
      }
      System.out.println( "]" );
   }
   
   //Simply to unclutter main()
   static void reduceMatrix ( Fraction[][] matrix ) {
      coordinate lastLeading[] = forwardPass( matrix );
      if ( !echelonFlag ) backwardsPass( matrix, lastLeading );
   }
   
   // printMatrix() finds the max width entry and printing spaces accordingly to stdout
   static void printMatrix ( Fraction[][] G ){
      out.printf( "\n" );
         maxLength = 0;
         int[][] lengths = new int[rows+1][columns+1];
         for( int i = 1; i <= rows; i++){
            for ( int j = 1; j <= columns; j++ ){
               lengths[i][j] = G[i][j].length();
               if ( lengths[i][j] > maxLength )
                  maxLength = lengths[i][j];
            }
         }
         maxLength += 1;
         for ( int i = 1; i <= rows; i++ ){
            for ( int j = 1; j <= columns; j++ ){
               lengths[i][j] = maxLength - lengths[i][j];
                  while ( lengths[i][j] > 0 ){
                     out.printf( " " );
                     lengths[i][j]--;
                  }
                  G[i][j].output();
                  if ( j == columns ) System.out.print( "\n" );
            }
         }
      out.printf( "\n" );
   }
   
   // findLeftMost() finds pivot columns in the matrix.
   static coordinate findLeftMost ( Fraction[][] matrix, int rowStart ){
       for ( int j = 1; j <= columns; j++ ){
          for ( int i = rowStart; i <= rows; i++ ){
             if ( matrix[i][j].getNumerator() != 0 ) return new coordinate( i,j );
          }
       }
       //If there are no more pivot columns, tell forwardPass() to stop.
       forwardPassFinished = true;
       return null;
    }
   
   // exchangeRow() exchanges entries of two rows of a given matrix.
   static void exchangeRow ( Fraction[][] matrix, int row1, int row2 ){
       for ( int j = 1; j <= columns; j++ ){
          Fraction tmp = matrix[row1][j];
          matrix[row1][j] = matrix[row2][j];
          matrix[row2][j] = tmp;
       }
    }
    
   // scaleRow() multiplies every entry in a row in a matrix by a given scalar.
   static void scaleRow ( Fraction[][] matrix, int i, Fraction scalar ){
       for ( int j = 1; j <= columns; j++ ){
          matrix[i][j] = matrix[i][j].multiply( scalar );
       }
    }

   // eliminateRow() multiplies a pivot row by a scalar and adds it to another given row.
   static void eliminateRow ( Fraction[][] matrix, int row1, int changeThisRow, Fraction scalar ){
       for ( int j = 1; j <= columns; j++ ){
          Fraction tmp = new Fraction();
          tmp = scalar.multiply( matrix[row1][j] );
          matrix[changeThisRow][j] = tmp.add( matrix[changeThisRow][j] );
       }
    }
   
   //Prints help about matrix.java. Run the program with no arguments or -h to show this text.
   static void printHelp (){
      out.printf("\nMatrix.java\n");
      out.printf("By Stewart Bracken\n\n");
      out.printf("Description:\n");
      out.printf("       Matrix.java brings a rectangular matrix of arbitrary\n");
      out.printf("       size to it's unique reduced row echelon form using\n");
      out.printf("       gaussian elimination.\n\n");
      out.printf("Usage:\n");
      out.printf("    java -jar matrix [-h | -e | -s] [InputFile]\n");
      out.printf("    -h brings up this help panel.\n");
      out.printf("    -e puts the input matrix into row echelon form, NOT reduced\n");
      out.printf("       row echelon form. Keep in mind row echelon form is a\n");
      out.printf("       non-unique form of a matrix, and may vary by the particular\n");
      out.printf("       algorithm one uses. On the other hand, reduced row echelon\n");
      out.printf("       form is unique, and every matrix has one.\n");
      out.printf("    -s shows every step taken to reduce a matrix.\n\n");
      out.printf("[InputFile]\n");
      out.printf("       is a text file that consists of a rectangular matrix of\n");
      out.printf("       integers of arbitrary size in which each integer is separated\n");
      out.printf("       by a space and each row is separated by a line break.\n\n");
      System.exit(1);
   }
    
   public static void main(String[] args){
      boolean helpFlag = false;
      
      //Get options using GetOpt.java
      GetOpt go = new GetOpt( args, "hes" );
      if ( args.length==0 ) helpFlag = true;
      go.optErr = true;
      int ch = -1;
      while ( ( ch = go.getopt() ) != go.optEOF ) {
         if ( (char)ch == 'h' ){
            helpFlag = true;
         } else if ( (char)ch == 's' ){
            printStepsFlag = true;
         } else if ( (char)ch == 'e' ){
            echelonFlag = true;
         } else Usage();
      }
        
      if ( helpFlag ) printHelp();
      
      //Convert file into matrix array
      Fraction[][] matrix = getMatrix ( args[go.optIndexGet()] );
        
      //Print initial matrix
      out.printf( "Initial Matrix is %d x %d\n", rows, columns );
      printMatrix( matrix );
        
      //Reduce and print final matrix.
      reduceMatrix( matrix );
      if ( echelonFlag ) out.printf( "\nRow echelon form:\n" );
      else out.printf( "\nReduced row echelon form:\n" );
      printMatrix( matrix );
            
      System.exit(0);
   }
}
