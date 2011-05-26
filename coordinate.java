import static java.lang.System.*;

public class coordinate {
   public int x;
   public int y;
     
   public coordinate( int x, int y ) {
      this.x = x;
      this.y = y;
   }
    
   public void addCoord ( coordinate[] coordArray ){
      for ( int i = 0; i < coordArray.length; i++ ){
         if ( coordArray[i] == null ) {
            coordArray[i] = this;
            return;
         }
      }
      out.printf( "Error in adding coordinate." );
      System.exit( 1 );
   }
  
   public void printCoord(){
      out.printf( "(%d,%d)", this.x, this.y );
   }
}
