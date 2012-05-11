package uk.ac.ebi.age.parser;

public class CellValue
{
 private byte[] rbMarks;
 private String value;
 
 public CellValue( String val, String escSeq )
 {
  int len = val.length();
  StringBuilder sb = null;
  
  int ptr = 0;
  
  while( ptr < len )
  {
   int pos = val.indexOf(escSeq, ptr);
   
   if( pos != -1 )
   {
    if( sb == null )
    {
     sb = new StringBuilder(len);
     rbMarks = new byte[len];
    }
    
    rbMarks[ sb.length() + pos - ptr] = 1;
    
    sb.append( val.substring(ptr, pos) );
    
   }
   else
   { 
    if( sb != null )
     sb.append( val.substring(ptr) );
   
    break;
   }

   
   ptr=pos+1;
  }
  
  if( sb != null )
   value = sb.toString();
  else
   value = val;
 }
 
 public String getValue()
 {
  return value;
 }

 public byte[] getRbMarks()
 {
  return rbMarks;
 }
 
 public boolean matchSubstring(String substr, int offs)
 {
  if( ! value.regionMatches(offs, substr, 0, substr.length()) )
   return false;
  
  if( rbMarks != null )
  {
   for( int i=offs; i < offs+substr.length(); i++)
    if( rbMarks[i] != 0 )
     return false;
  }
  
  return true;
 }
 
}
