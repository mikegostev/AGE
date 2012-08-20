package uk.ac.ebi.age.parser;


public class CellValue
{
 private byte[] rbMarks;
 private String value;
 private String rawValue;
 
 private CellValue( )
 {}
 
 public CellValue( String val, String escSeq )
 {
  rawValue = val;
  
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
 
 public boolean matchString(String substr)
 {
  if( substr.length() != value.length() )
   return false;
  
  return matchSubstring(substr,0);
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

 public boolean isSymbolRed(int i)
 {
  if( rbMarks == null )
   return false;
  
  return rbMarks[i] != 0;
 }

 public boolean hasRed(int beg, int end)
 {
  if( rbMarks != null )
  {
   for( int i=beg; i < end; i++)
    if( rbMarks[i] != 0 )
     return true;
  }
  
  return false;
 }
 
 public void trim()
 {
  int len = value.length();
  int st = 0;

  while ((st < len) && ( value.charAt(st) <= ' ' ))
      st++;

  while ((st < len) && (value.charAt(len - 1) <= ' '))
      len--;
  
  if( st == 0 && len == value.length() )
   return;
  
  value = value.substring(st,len);
  
  if( rbMarks == null || st == 0 )
   return;
   
  for( int i = 0; i < value.length(); i++ )
   rbMarks[i] = rbMarks[i+st];
 }
 
 public String getRawValue()
 {
  return rawValue;
 }
}
