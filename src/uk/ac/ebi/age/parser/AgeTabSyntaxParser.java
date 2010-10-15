package uk.ac.ebi.age.parser;

import java.util.List;

import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.util.StringUtil;

public abstract class AgeTabSyntaxParser
{
 public static final String rangeFlag="RANGE";
 public static final String typeFlag="TYPE";
 public static final String targetFlag="TARGET";


 private static interface StrProc
 {
  String getBrackets();
  void process(ClassReference nm, String s) throws ParserException;
 }
 
 public String getCustomTokenBrackets()
 {
  return DefaultSyntaxProfile.customTokenBrackets;
 }
 
 public String getFlagsTokenBrackets()
 {
  return DefaultSyntaxProfile.flagsTokenBrackets;
 }
 
 public String getQualifierTokenBrackets()
 {
  return DefaultSyntaxProfile.qualifierTokenBrackets;
 }
 
 public String getFlagsSeparatorSign()
 {
  return DefaultSyntaxProfile.flagsSeparatorSign;
 }
 
 public String getFlagsEqualSign()
 {
  return DefaultSyntaxProfile.flagsEqualSign;
 }
 
 public String getAnonymousObjectId()
 {
  return DefaultSyntaxProfile.anonymousObjectId;
 }
 
 public String getCommonObjectId()
 {
  return DefaultSyntaxProfile.commonObjectId;
 }
 
 public String getParentClassPrefixSeparator()
 {
  return DefaultSyntaxProfile.parentClassPrefixSeparator;
 }
 
 
 private StrProc[] prc = new StrProc[]{
   new StrProc()
   {
    public String getBrackets(){ return getFlagsTokenBrackets(); }
    public void process(ClassReference nm, String s) throws ParserException
    {
     List<String> flags = StringUtil.splitString(s, getFlagsSeparatorSign() );
     
     for( String flagstr : flags )
     {
      int eqpos = flagstr.indexOf(getFlagsEqualSign());
      
      if( eqpos == -1 )
       nm.addFlag(flagstr,null);
      else
      {
       String fname = flagstr.substring(0,eqpos);
       String fval =  flagstr.substring(eqpos+1);
       
       nm.addFlag(fname,fval);
       
       if( fname.equals( rangeFlag ) )
       {
        try
        {
         nm.setRangeClassRef( string2ClassReference(fval) );
        }
        catch (ParserException e)
        {
         throw new ParserException(0,0,"Invalid range class reference: "+e.getMessage());
        }
       }
       else if( fname.equals( targetFlag ) )
       {
        try
        {
         nm.setTargetClassRef( string2ClassReference(fval) );
        }
        catch (ParserException e)
        {
         throw new ParserException(0,0,"Invalid target class reference: "+e.getMessage());
        }
       }
      }
     }
    }
   },
   
   new StrProc()
   {
    public String getBrackets(){ return getQualifierTokenBrackets(); }
    public void process(ClassReference nm,String s) throws ParserException
    {
     ClassReference cr = string2ClassReference(s);
     
     nm.insertQualifier(cr);
    }
   }
   
 };
 
 
 public static AgeTabSyntaxParser getInstance()
 {
  return new AgeTabSyntaxParserImpl(  );
 }

 public abstract AgeTabSubmission parse( String txt ) throws ParserException;

 
 public ClassReference string2ClassReference( String str ) throws ParserException
 {
  final ClassReference nm = new ClassReference();
  
  String brckts = null;
  
  if( str.charAt(0) == getCustomTokenBrackets().charAt(0) )
  {
   nm.setCustom(true);
   brckts  = getCustomTokenBrackets();
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
    throw new ParserException(0,0, "Invalid character at: "+(pos+1)+". The closing bracket must be the last symbol of the token.");
   
   name = str.substring(1, pos);
  }
  else
  {
   if( str.charAt(str.length()-1) == getCustomTokenBrackets().charAt(1) )
   {
    int pos = str.indexOf(getParentClassPrefixSeparator()+getCustomTokenBrackets().charAt(0));
    
    if( pos == -1 )
     throw new ParserException(0,0, "Invalid character at: "+(str.length())+". The closing bracket must correspond opening one.");
    
    name = str.substring(pos+1,str.length()-1);
    nm.setParentClass( str.substring(0,pos) );
    nm.setCustom(true);
   }
   else
    name = str;
  }
  
  if( name.length() == 0 )
   throw new ParserException(0,0, "Name in the header column can't be empty");
  
  nm.setName(name);
  
  return nm;
 }
 

}

