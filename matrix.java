 /**************************************************************************
  * Sudoku.java
  * 
  **************************************************************************/
  
import java.util.*;
import java.io.*;
import static java.lang.System.*;

class matrix{
   
   static int rows;
   static int columns;
   static boolean echelonFlag = false;
   static boolean printStepsFlag = false;
   static boolean helpFlag = false;
   static boolean forwardPassFinished = false;
   static int leadCount = 0;
   
   static final Fraction negativeOne = new Fraction(-1, 1);
   static final Fraction zero = new Fraction(0, 1);
   static final Fraction one = new Fraction(1, 1);
   
  
    
   public static final class coordinate {
      public final int x;
      public final int y;
      
      public coordinate(int x, int y) {
         this.x = x;
         this.y = y;
      }
      
      public void addCoord (coordinate[] coordArray){
         for (int i = 0; i<coordArray.length; i++){
            if (coordArray[i] == null) {
               coordArray[i]=this;
               return;
            }
         }
         out.printf("Error in adding coordinate.");
         System.exit(1);
         
      }
      
      public void printCoord(){
         out.printf("(%d,%d)",this.x,this.y);
      }
    }
    
    // public static final class fraction {
    //         public final int top;
    //         public final int bottom;
    // 
    //         public fraction(int top, int bottom) {
    //           this.x = x;
    //           if (y==null) y = 1;
    //           else this.y = y;
    //        }
    //      }
    
    static void Usage(){
       System.err.println( "Usage: java matrix [-e -s] [InputFile]" );
       System.exit(1);
    }
    
    //getGrid() will extract integers from input file and put them in the array
    static Fraction[][] getMatrix ( String filename ){
       int rowBuffer = 30;
       int columnBuffer = 30;
       Fraction[][] matrixBuffer = new Fraction[rowBuffer][columnBuffer];
       int rowCt = 0;
       int columnCt = 0;
       
       try {
          Scanner sc = new Scanner( new File( filename ));
          sc.useDelimiter(System.getProperty("line.separator"));
          while (sc.hasNext()) {
             Scanner lineScanner = new Scanner(sc.next());
             columnCt=0;
             while (lineScanner.hasNextInt()){
                Fraction input = new Fraction(lineScanner.nextInt(),1);
                matrixBuffer[rowCt][columnCt] = input;
                //out.printf("\nmatrixBuffer[%d][%d]  ",rowCt,columnCt);
                //matrixBuffer[rowCt][columnCt].output();
                columnCt++;
                if (columnBuffer-2==columnCt){
                     //transfer into bigger array
                     // **THIS HASN'T BEEN TESTED**
                     rowBuffer *= 2;
                     columnBuffer *= 2;
                     Fraction[][] bigger = new Fraction[rowBuffer][columnBuffer];
                     for (int i = 0; i<rowBuffer/2;i++){
                        for (int j = 0; j<columnBuffer/2;j++){
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
       
       Fraction[][] matrix = new Fraction[rowCt+1][columnCt+1];
       for (int i = 0; i<rowCt; i++){
          for (int j = 0; j<columnCt; j++){
             //out.printf("i[%d] j[%d]",i,j);
             matrix[i+1][j+1] = matrixBuffer[i][j];
             //matrix[i+1][j+1].output(); out.printf("\n");
          }
       }
       
       rows = rowCt;
       columns = columnCt;
       return matrix;
    }

   static coordinate[] forwardPass (Fraction[][] matrix) {
      coordinate[] lastLeading = new coordinate[50];
      for ( int i = 1; i<=rows; i++){
         coordinate leftmost = findLeftMost(matrix,i);
         if (forwardPassFinished) break;
         leftmost.addCoord(lastLeading);
         leadCount += 1;
         //printArray(lastLeading);
         //exchange first non zero entry row with first row if needed.
         if (matrix[i][leftmost.y].getNumerator() == 0){
             exchangeRow (matrix, i, leftmost.x);
             if ( printStepsFlag ) {
                out.printf ("Exchanging rows %d <--> %d\n",i,leftmost.x);
                printMatrix(matrix);
             }
         }
         //scale to 1
         if ((matrix[i][leftmost.y].getNumerator() != 1) ||
            (matrix[i][leftmost.y].getDenominator() != 1)){
                //multiple first row by 1/a1j
               Fraction scalar = new Fraction();
               scalar = matrix[i][leftmost.y].inverse(matrix[i][leftmost.y]);
               //scalar.setNumerator(matrix[i][leftmost.y].getDenominator());
               //scalar.setDenominator(matrix[i][leftmost.y].getNumerator());
               scaleRow(matrix, i, scalar);
               if ( printStepsFlag ) {
                   out.printf ("Scaling R%d by ",i,leftmost.x);
                   scalar.output(); out.printf("\n");
                   printMatrix(matrix);
                }
         }
         //zero all entries in column j below scaled entry
         for (int k = i+1; k<=rows; k++){
            if (matrix[k][leftmost.y].getNumerator() != 0){
               Fraction scalar = new Fraction();
               scalar = matrix[k][leftmost.y].multiply(negativeOne);
               //scalar.output();
               eliminateRow(matrix, i, k, scalar);
               if ( printStepsFlag ) {
                  out.printf("R%d = ",k);
                  scalar.output();
                  out.printf (" * R%d + R%d\n",i,k);
                  printMatrix(matrix);
                }
            }
         }
         //out.printf("%d\n",i);
         //if ( printStepsFlag ) printMatrix(matrix);
      }
      //out.printf("Forward pass done.\n");
      return lastLeading;
   }
   
   static void backwardsPass ( Fraction[][] matrix, coordinate[] lastLeading){
      int i = leadCount-1;
      //printArray(lastLeading);
      //while ( lastLeading[i] != null ) ++i;
      //out.printf("i=%d",i);
      //--i;
      while ( i>0 ){
         if (lastLeading[i].x>1 ){ //stop it from redoing row 2
            for (int k = lastLeading[i].x-1; k>0; k--){
               if (matrix[k][lastLeading[i].y].getNumerator() != 0){
                  Fraction scalar = new Fraction();
                  scalar = matrix[k][lastLeading[i].y].multiply(negativeOne);
                  //scalar.output();
                  eliminateRow(matrix, lastLeading[i].x, k, scalar);
                  if ( printStepsFlag ) {
                     out.printf("R%d = ",k);
                     scalar.output();
                     out.printf (" * R%d + R%d\n",lastLeading[i].x,k);
                     printMatrix(matrix);
                   }
               }
            }
         }
         
         i--;
      }
   }
   
   static void printArray(coordinate[] A){
      System.out.print("[ ");
      for(int i=0; i<A.length; i++){
         if (A[i] == null) break;
         A[i].printCoord();
         out.printf(" ");
      }
      System.out.println("]");
   }
   
   static void reduceMatrix ( Fraction[][] matrix ) {
      
      coordinate lastLeading[] = forwardPass(matrix);

      if (!echelonFlag) backwardsPass(matrix,lastLeading);
   }
   
   static void printMatrix ( Fraction[][] G ){
       out.printf("\n");
       int maxLength = 0;
       int[][] lengths = new int[rows+1][columns+1];
       for( int i = 1; i<=rows; i++){
          for (int j = 1; j<=columns; j++ ){
             lengths[i][j] = G[i][j].length();
             if ( lengths[i][j] > maxLength )
               maxLength = lengths[i][j];
          }
        }
        maxLength += 1;
        for ( int i=1; i<=rows; i++ ){
            for ( int j=1; j<=columns; j++ ){
                lengths[i][j] = maxLength - lengths[i][j];
                while (lengths[i][j]>0){
                   out.printf(" ");
                   lengths[i][j]--;
                }
                G[i][j].output();
                if ( j==columns ) System.out.print( "\n" );
            }
        }
        out.printf("\n");
    }
    
   static coordinate findLeftMost (Fraction[][] matrix, int rowStart){
       for (int j = 1; j<=columns; j++){
          for (int i = rowStart; i<=rows; i++){
             if ( matrix[i][j].getNumerator() != 0 ) return new coordinate(i,j);
          }
       }
       forwardPassFinished = true;
       return null;
    }
   
   static void exchangeRow ( Fraction[][] matrix, int row1, int row2){
       for ( int j=1; j<=columns; j++){
          Fraction tmp = matrix[row1][j];
          matrix[row1][j] = matrix[row2][j];
          matrix[row2][j] = tmp;
       }
    }
    
   static void scaleRow ( Fraction[][] matrix, int i, Fraction scalar){
       for (int j = 1; j<=columns; j++){
          matrix[i][j] = matrix[i][j].multiply(scalar);
       }
    }
    
   static void eliminateRow ( Fraction[][] matrix, int row1, int changeThisRow, Fraction scalar){
       for (int j = 1; j<=columns; j++){
          Fraction tmp = new Fraction();
          tmp = scalar.multiply(matrix[row1][j]);
          matrix[changeThisRow][j] = tmp.add(matrix[changeThisRow][j]);
       }
    }
    
   static void printHelp (){
      out.printf("\nMatrix.java\n");
      out.printf("By Stewart Bracken\n\n");
      out.printf("Description - Matrix.java brings a rectangular matrix of arbitrary\n");
      out.printf("              size to it's unique reduced row echelon form using\n");
      out.printf("              gaussian elimination.\n\n");
      out.printf("Usage: java matrix [-h -e -s] [InputFile]\n");
      out.printf("       -h brings up this help panel.\n");
      out.printf("       -e puts the input matrix into row echelon form, NOT reduced\n");
      out.printf("          row echelon form. Keep in mind row echelon form is a\n");
      out.printf("          non-unique form of a matrix, and may vary by the particular\n");
      out.printf("          algorithm one uses. On the other hand, reduced row echelon\n");
      out.printf("          form is unique, and every matrix has one.\n");
      out.printf("       -s shows every step taken to reduce a matrix.\n\n");
      out.printf("[InputFile] is a text file that consists of a rectangular matrix of\n");
      out.printf("            integers of arbitrary size in which each integer is separated\n");
      out.printf("            by a space and each row is separated by a line break.\n\n");
      System.exit(1);
   }
    
   public static void main(String[] args){
        //Get options using GetOpt.java
        GetOpt go = new GetOpt(args, "hes");
        if ( args.length==0 ) helpFlag = true;
        go.optErr = true;
        int ch = -1;
        while ((ch = go.getopt()) != go.optEOF) {
            if ((char)ch == 'h'){
               helpFlag = true;
            }else if ((char)ch == 's'){
             printStepsFlag = true;
             //helpFlag = false;
            }else if ((char)ch == 'e'){
              echelonFlag = true;
              //helpFlag = false;
            }else Usage();
        }
        
        if (helpFlag) printHelp();
        
        Fraction[][] matrix = getMatrix (args[go.optIndexGet()]);
        
        //Print initial matrix
        out.printf("Initial Matrix is %d x %d\n",rows,columns);
        printMatrix(matrix);
        
        //Reduce and print final matrix.
        reduceMatrix(matrix);
        if (echelonFlag) out.printf("\nRow echelon form:\n");
        else out.printf("\nReduced row echelon form:\n");
        printMatrix(matrix);
        System.exit(0);
 
     }
    
}