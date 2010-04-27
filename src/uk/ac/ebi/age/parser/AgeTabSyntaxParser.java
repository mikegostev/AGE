package uk.ac.ebi.age.parser;

import java.util.List;

import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.util.StringUtil;

public abstract class AgeTabSyntaxParser
{
 public static final String validatedTokenBrackets="[]";
 public static final String flagsTokenBrackets="()";
 public static final String flagsSeparatorSign=";";
 public static final String flagsEqualSign="=";
 public static final String anonymousObjectId="?";
 
 
 public static AgeTabSyntaxParser getInstance()
 {
  return new AgeTabSyntaxParserImpl(  );
 }

 public abstract AgeTabSubmission parse( String txt ) throws ParserException;

 
 public static ColumnHeader string2ColumnHeader( String str ) throws ParserException
 {
  ColumnHeader nm = new ColumnHeader();
  
  String brckts = null;
  
  if( str.charAt(0) == validatedTokenBrackets.charAt(0) )
  {
   nm.setCustom(false);
   brckts  = validatedTokenBrackets;
  }

  else
   nm.setCustom(true);

  
  if( str.charAt(str.length()-1) == flagsTokenBrackets.charAt(1) )
  {
   int pos = str.lastIndexOf(flagsTokenBrackets.charAt(0));
   
   if( pos == -1 )
    throw new ParserException(0,0, "No opening bracket for flags section: '"+flagsTokenBrackets.charAt(0)+"'");
   
   List<String> flags = StringUtil.splitString(str.substring(pos+1,str.length()-1), flagsSeparatorSign);
   
   for( String flagstr : flags )
   {
    int eqpos = flagstr.indexOf(flagsEqualSign);
    
    if( eqpos == -1 )
     nm.addFlag(flagstr,null);
    else
     nm.addFlag(flagstr.substring(0,eqpos),flagstr.substring(eqpos+1));
   }
   
   str = str.substring(0,pos);
  }

  if( brckts != null)
  {
   int pos = str.indexOf(brckts.charAt(1));
   
   if( pos == -1 )
    throw new ParserException(0,0, "No closing bracket: '"+brckts.charAt(1)+"'");
   
   if( pos != (str.length()-1) )
    throw new ParserException(0,0, "Invalid character at: "+(pos+1)+". The closing bracket must be the last symbol.");
   
   nm.setName(str.substring(1, pos));
  }
  else
   nm.setName(str);
  
//  if( nm.isFlagSet(rangeFlag))
//  {
//   if( nm.getFlagValue(rangeFlag) == null )
//    throw new ParserException(0,0, "A '"+rangeFlag+"' flag mast have a value");
//   
//   nm.setType(Type.RELATION);
//  }
  
  return nm;
 }
 

}

