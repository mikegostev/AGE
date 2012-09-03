package uk.ac.ebi.age.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class AgeTabSyntaxParser
{
 public static final String rangeFlag="RANGE";
 public static final String typeFlag="TYPE";
 public static final String targetFlag="TARGET";

 private final SyntaxProfile syntaxProfile;
 private final SyntaxProfileDefinition syntaxProfileDef;

 protected AgeTabSyntaxParser(SyntaxProfile sp)
 {
  syntaxProfile = sp;
  syntaxProfileDef = sp.getCommonSyntaxProfile();
 }
 
 private static interface StrProc
 {
  String getBrackets();
  void process(ClassReference nm, CellValue s, int st, int end) throws ParserException;
 }
 
 private static class IntPair
 {
  int begin;
  int end;

  public IntPair(int begin, int end)
  {
   super();
   this.begin = begin;
   this.end = end;
  }
 }
 
 private final StrProc[] prc = new StrProc[]{
   new StrProc()
   {
    @Override
    public String getBrackets(){ return syntaxProfileDef.getFlagsTokenBrackets(); }
    
    @Override
    public void process(ClassReference nm, CellValue cell, int start, int end) throws ParserException
    {
     String str = cell.getValue();
     
     List<IntPair> flags = new ArrayList<IntPair>(10);
     
     String sep = syntaxProfileDef.getFlagsSeparatorSign();
     
     int fbeg = start;
     int cpos = start;
     int seplen = sep.length();
     
     while( fbeg < end-1 )
     {
      int pos = str.indexOf(sep, cpos);
      
      if( pos == -1 )
      {
       flags.add(new IntPair(fbeg,end));
       break;
      }
      
      cpos = pos + seplen;

      if( ! cell.hasRed(pos,pos+seplen) )
      {
       flags.add(new IntPair(fbeg,pos));
       fbeg=cpos;
      }

     }
     
     
     for( IntPair bnd : flags )
     {
      int eqpos = -1;
      int ptr = bnd.begin;
      
      while( ptr < bnd.end )
      {
       int pos = str.indexOf(syntaxProfileDef.getFlagsEqualSign(),ptr);
       
       if( pos == -1 || pos >= bnd.end )
        break;
       
       if( ! cell.isSymbolRed(pos) )
       {
        eqpos = pos;
        break;
       }
       
       ptr += syntaxProfileDef.getFlagsEqualSign().length();
      }
      
      if( eqpos == -1 )
       nm.addFlag(str.substring(bnd.begin, bnd.end),null);
      else
      {
       String fname = str.substring(bnd.begin,eqpos);
       String fval =  str.substring(eqpos+syntaxProfileDef.getFlagsEqualSign().length(), bnd.end);
       
       nm.addFlag(fname,fval);
       
       if( fname.equals( rangeFlag ) && ! cell.hasRed(bnd.begin,eqpos) )
       {
        try
        {
         nm.setRangeClassRef( string2ClassReference(cell,eqpos+syntaxProfileDef.getFlagsEqualSign().length(), bnd.end) );
        }
        catch (ParserException e)
        {
         throw new ParserException(0,0,"Invalid range class reference: "+e.getMessage());
        }
       }
       else if( fname.equals( targetFlag ) && ! cell.hasRed(bnd.begin,eqpos)  )
       {
        try
        {
         nm.setTargetClassRef( string2ClassReference(cell,eqpos+syntaxProfileDef.getFlagsEqualSign().length(), bnd.end) );
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
    @Override
    public String getBrackets(){ return syntaxProfileDef.getQualifierTokenBrackets(); }
    @Override
    public void process(ClassReference nm, CellValue cell, int start, int end) throws ParserException
    {
     ClassReference cr = string2ClassReference(cell, start, end);
     
     nm.insertQualifier(cr);
    }
   }
   
 };
 
 
 public SyntaxProfile getSyntaxProfile()
 {
  return syntaxProfile;
 }
 
// public static AgeTabSyntaxParser getInstance()
// {
//  return new AgeTabSyntaxParserImpl(  );
// }
//
// public AgeTabModule parse( String txt ) throws ParserException
// {
//  return parse( txt, syntaxProfile );
// }
 
 public abstract AgeTabModule parse( String txt ) throws ParserException;

 public ClassReference string2ClassReference( CellValue cell ) throws ParserException
 {
  cell.trim();
  
  return string2ClassReference(cell, 0, cell.getValue().length());
 }
 
 public ClassReference string2ClassReference( CellValue cell, int start, int end ) throws ParserException
 {
  final ClassReference nm = new ClassReference();
  
  String str = cell.getValue();
  
  nm.setRawReference( str.substring(start,end).trim() );
  
  
  outer: while( start < end )
  {
   
   for(int i=0; i < prc.length; i++ ) // Looking for expressions in braces and calling 'process' for such expressions
   {
    if( str.charAt(end-1) == prc[i].getBrackets().charAt(1) && ! cell.isSymbolRed(end-1) )
    {
     int level = 0;
     
     int j;
     for( j= end-2; j>=start; j--)
     {
      if( str.charAt(j) == prc[i].getBrackets().charAt(1) && ! cell.isSymbolRed(j) ) //We've found another closing brace so assuming a nested expression
       level++;
      else if( str.charAt(j) == prc[i].getBrackets().charAt(0) && ! cell.isSymbolRed(j)  )
      {
       if( level > 0 )
        level--;
       else
       {
        
        prc[i].process(nm, cell, j+1, end-1);
        
        end = j;

        continue outer;
       }
      }
     }
     
     if( j < start )
      throw new ParserException(0,0, "No opening bracket for section: '"+prc[i].getBrackets().charAt(0)+"'");
     
    }
     
   }
   
   break;
  }
  
  
  String name = null;
  
  if( str.charAt(start) == syntaxProfileDef.getCustomTokenBrackets().charAt(0) && ! cell.isSymbolRed(start) )
  {
   int pos=start;
   
   while( pos < end )
   {
    pos = str.indexOf(syntaxProfileDef.getCustomTokenBrackets().charAt(1), pos);
    
    if( pos == -1 || ! cell.isSymbolRed(pos) )
     break;
    
    pos++;
   }
     
     
   
   if( pos == -1 || pos >= end )
    throw new ParserException(0,0, "No closing bracket: '"+syntaxProfileDef.getCustomTokenBrackets().charAt(1)+"'");
   
   if( pos != (end-1) )
    throw new ParserException(0,0, "Invalid character at: "+(pos+1)+". The closing bracket must be the last symbol of the token.");
   
   name = str.substring(start+1, pos);
   nm.setCustom(true);
  }
  else
  {
   if( str.charAt(end-1) == syntaxProfileDef.getCustomTokenBrackets().charAt(1)  && ! cell.isSymbolRed(end-1) )
   {
    
    int pos=start;
    
    while( pos < end )
    {
     pos = str.indexOf(syntaxProfileDef.getCustomTokenBrackets().charAt(0), pos);
     
     if( pos == -1 || ! cell.isSymbolRed(pos) )
      break;
     
     pos++;
    }

    
    if( pos == -1 || pos >= end )
     throw new ParserException(0,0, "Invalid character at: "+(str.length())+". The closing bracket must correspond to opening one.");
    
    name = str.substring(pos+1,end-1);
    nm.setParentClass( str.substring(start,pos) );
    nm.setCustom(true);
   }
   else
   {
    name = str.substring(start,end);
    nm.setCustom(false);
   }
  }
  
  if( name.length() == 0 )
   throw new ParserException(0,0, "Name in the header column can't be empty");
  
  nm.setName(name);
  
  return nm;
 }
 

}

