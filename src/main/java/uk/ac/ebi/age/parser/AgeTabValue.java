package uk.ac.ebi.age.parser;


public class AgeTabValue extends AgeTabElement
{
 private byte[] rbMarks;
 private String value;
 private ClassReference colHeader;

 public AgeTabValue(int row, int col, ClassReference prop, String value, byte[] rbMarks)
 {
  super(row, col);
  this.value=value;
  colHeader=prop;
  this.rbMarks = rbMarks;
 }

 public String getValue()
 {
  return value;
 }

 public ClassReference getColumnHeader()
 {
  return colHeader;
 }

 public boolean matchPrefix( String pfx )
 {
  if( ! value.startsWith(pfx) )
   return false;
  
  if( rbMarks == null )
   return true;
  
  int pfxLen = pfx.length();
  
  for( int i=0; i < pfxLen; i++ )
   if( rbMarks[i] != 0 )
    return false;
  
  return true;
 }

 public void trim()
 {
  value=value.trim();
 }
 
}
