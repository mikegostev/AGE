package uk.ac.ebi.age.parser;

import java.util.List;

import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.util.StringUtil;

public abstract class AgeTabSyntaxParser
{
 public static final String customTokenBrackets="{}";
 public static final String flagsTokenBrackets="<>";
 public static final String qualifierTokenBrackets="[]";
 public static final String flagsSeparatorSign=";";
 public static final String flagsEqualSign="=";
 public static final String anonymousObjectId="?";
 public static final String commonObjectId="*";
 
 private static interface StrProc
 {
  String getBrackets();
  void process(ClassReference nm, String s) throws ParserException;
 }
 
 
 private static StrProc[] prc = new StrProc[]{
   new StrProc()
   {
    public String getBrackets(){ return flagsTokenBrackets; }
    public void process(ClassReference nm, String s)
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
    public String getBrackets(){ return qualifierTokenBrackets; }
    public void process(ClassReference nm,String s) throws ParserException
    {
     ClassReference cr = string2ClassReference(s);
     
     nm.setQualifier(cr);
    }
   }
   
 };
 
 
 public static AgeTabSyntaxParser getInstance()
 {
  return new AgeTabSyntaxParserImpl(  );
 }

 public abstract AgeTabSubmission parse( String txt ) throws ParserException;

 
 public static ClassReference string2ClassReference( String str ) throws ParserException
 {
  final ClassReference nm = new ClassReference();
  
  String brckts = null;
  
  if( str.charAt(0) == customTokenBrackets.charAt(0) )
  {
   nm.setCustom(true);
   brckts  = customTokenBrackets;
  }
  else
   nm.setCustom(false);

  while( str.length() > 0 )
  {
   String ps = null;
   
   for(int i=0; i < 2; i++ )
   {
    if( str.charAt(str.length()-1) == prc[i].getBrackets().charAt(1) )
    {
     int level = 0;
     
     int j;
     for( j= str.length()-2; j>=0; j--)
     {
      if( str.charAt(j) == prc[i].getBrackets().charAt(1) )
       level++;
      else if( str.charAt(j) == prc[i].getBrackets().charAt(0) )
      {
       if( level > 0 )
        level--;
       else
       {
        ps = str.substring(j+1,str.length()-1);
        prc[i].process(nm, ps);

        str=str.substring(0,j);

        break;
       }
      }
     }
     
     if( j < 0 )
      throw new ParserException(0,0, "No opening bracket for section: '"+prc[i].getBrackets().charAt(0)+"'");
     
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

