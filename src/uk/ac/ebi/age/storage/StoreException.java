package uk.ac.ebi.age.storage;

public class StoreException extends Exception
{
 private int lineNumber;
 private int columnNumber;
 
 public StoreException( int ln, int cl, String msg )
 {
  super( msg );
  lineNumber=ln;
  columnNumber=cl;
 }
 
 public int getLineNumber()
 {
  return lineNumber;
 }

 public int getColumnNumber()
 {
  return columnNumber;
 }

 public void setColumn(int i)
 {
  columnNumber=i;
 }
}
