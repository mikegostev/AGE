package uk.ac.ebi.age.parser;

import java.util.List;

import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.util.StringUtil;

public abstract class AgeTabSyntaxParser
{
 public static final String validatedTokenBrackets="{}";
 public static final String flagsTokenBrackets="()";
 public static final String variantTokenBrackets="[]";
 public static final String flagsSeparatorSign=";";
 public static final String flagsEqualSign="=";
 public static final String anonymousObjectId="?";
 
 private static interface StrProc
 {
  String getBrackets();
  void process(String s);
 }
 
 public static AgeTabSyntaxParser getInstance()
 {
  return new AgeTabSyntaxParserImpl(  );
 }

 public abstract AgeTabSubmission parse( String txt ) throws ParserException;

 
 public static ColumnHeader string2ColumnHeader( String str ) throws ParserException
 {
  final ColumnHeader nm = new ColumnHeader();
  
  String brckts = null;
  
  if( str.charAt(0) == validatedTokenBrackets.charAt(0) )
  {
   nm.setCustom(false);
   brckts  = validatedTokenBrackets;
  }
  else
   nm.setCustom(true);

  StrProc[] prc = new StrProc[]{
    new StrProc()
    {
     public String getBrackets(){ return flagsTokenBrackets; }
     public void process(String s)
     {
      List<String> flags = StringUtil.splitString(s, flagsSeparatorSign);
      
      for( String flagstr : flags )
      {
       int eqpos = flagstr.indexOf(flagsEqualSign);
       
       if( eqpos == -1 )
        nm.addFlag(flagstr,null);
       else
        nm.addFlag(flagstr.substring(0,eqpos),flagstr.substring(eqpos+1));
      }
     }
    },
    
    new StrProc()
    {
     public String getBrackets(){ return variantTokenBrackets; }
     public void process(String s)
     {
      nm.setParameter(s);
     }
    }
    
  };
  
  while( str.length() > 0 )
  {
   String ps = null;
   
   for(int i=0; i < 2; i++ )
   {
    if( str.charAt(str.length()-1) == prc[i].getBrackets().charAt(1) )
    {
     int pos = str.lastIndexOf(prc[i].getBrackets().charAt(0));
     
     if( pos == -1 )
      throw new ParserException(0,0, "No opening bracket for section: '"+prc[i].getBrackets().charAt(0)+"'");
     
     ps = str.substring(pos+1,str.length()-1);
     prc[i].process(ps);

     str=str.substring(0,pos);
     
     break;
    }
     
   }
   
   if( ps == null )
    break;
  }
  

  String name = null;
  
  if( brckts != null)
  {
   int pos = str.indexOf(brckts.charAt(1));
   
   if( pos == -1 )
    throw new ParserException(0,0, "No closing bracket: '"+brckts.charAt(1)+"'");
   
   if( pos != (str.length()-1) )
    throw new ParserException(0,0, "Invalid character at: "+(pos+1)+". The closing bracket must be the last symbol.");
   
   name = str.substring(1, pos);
  }
  else
   name = str;
  
  if( name.length() == 0 )
   throw new ParserException(0,0, "Name in the header column can't be empty");
  
  nm.setName(name);
  
  return nm;
 }
 

}

