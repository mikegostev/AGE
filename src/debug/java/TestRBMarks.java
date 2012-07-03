import uk.ac.ebi.age.parser.CellValue;


public class TestRBMarks
{

 /**
  * @param args
  */
 public static void main(String[] args)
 {
  String str = "\\abcdef\\g\\";
  
  CellValue cv = new CellValue(str, "\\");
  
  System.out.println( str );
  System.out.println( cv.getValue() );
  
  
  if( cv.getRbMarks() != null )
  for( int i=0; i< cv.getValue().length(); i++ )
   System.out.print( cv.getRbMarks()[i] );
  
  System.out.print("\n");
  
  System.out.println( cv.matchSubstring("bcd", 1) );
 }

}
