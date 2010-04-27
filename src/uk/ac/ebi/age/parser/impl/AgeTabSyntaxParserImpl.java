package uk.ac.ebi.age.parser.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ColumnHeader;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.service.IdGenerator;
import uk.ac.ebi.age.util.StringUtil;

public class AgeTabSyntaxParserImpl extends AgeTabSyntaxParser
{

 public AgeTabSubmission parse( String txt ) throws ParserException
 {
  AgeTabSubmission data = new AgeTabSubmissionImpl();
  
  
  int cpos = 0;
  int len = txt.length();
  int ln = 0;
  char colSep='\t';
  
  String eol = "\r\n";
  
  if( txt.indexOf(eol) == -1)
   eol="\n";

  if( txt.indexOf(eol) == -1)
   throw new ParserException(1,1,"File must contains at least 2 lines separated by either \\n or \\r\\n "); 
 
  int eolLength = eol.length();
  
  while( txt.startsWith(eol, cpos) )
  {
   cpos+=eolLength;
   ln++;
  }
  
  {  // looking for column separator
   int commaPos = txt.indexOf(',',cpos);
   int tabPos = txt.indexOf('\t',cpos);
   
   commaPos = commaPos==-1?Integer.MAX_VALUE:commaPos;
   tabPos = tabPos==-1?Integer.MAX_VALUE:tabPos;
   
   if( commaPos < tabPos )
    colSep = ',';
  }
  
  String sep = ""+colSep;

  List<String> parts = new ArrayList<String>(100);
  boolean newContext = true;
  BlockHeader header = null;
  
//  AgeObject cObj=null;
//  
//  ModelFactory fact=ModelFactory.getInstance();
//  
//  Map<String,AgeObject> classMap = null;
//  Map<AgeClass,Map<String,AgeObject>> objMap = new TreeMap<AgeClass,Map<String,AgeObject>>();
  
  AgeTabObject cObj=null;
  
  while(cpos < len)
  {
   ln++;

   int pos = txt.indexOf(eol, cpos);

   String line = null;
   
   if(pos == -1)
   {
    line=txt.substring(cpos);
    cpos=len;
   }
   else
   {
    line=txt.substring(cpos,pos);
    cpos = pos + eolLength;
   }

   parts.clear();
   StringUtil.splitExcelString(line, sep, parts);
   
   if( isEmptyLine(parts) )
   {
    newContext=true;
    continue;
   }
   
   
   if( newContext )
   {
    int endSz=-1;
    int i=-1;
    for( String pt : parts )
    {
     i++;
     
     if( pt.length() == 0 )
     {
      if( endSz == -1 )
       endSz=i;
     }
     else if( endSz != -1 )
      throw new ParserException(ln,i,"Invalid cell contetns. Must be empty");
    }
    
    
    List<String> hdrParts = endSz != -1?parts=parts.subList(0, endSz):parts;

    newContext = false;

    header = analyzeHeader(hdrParts,ln);
    
    cObj = null;
    
    continue;
   }
   
   Iterator<String> partIter = parts.iterator();
   
   String part = partIter.next();
   
   if( part.length() != 0 )
   {
    if( part.equals(anonymousObjectId) )
    { 
     String id = IdGenerator.getInstance().getStringId();
     cObj = data.createObject(id,header,ln);
    }
    else
     cObj = data.getOrCreateObject(part,header,ln);
   }
   else if( cObj == null )
    throw new ParserException(ln, 1, "Object identifier is expected");
   
   int col=1; 
   for( ColumnHeader prop : header.getColumnHeaders() )
   {
    col++;
    
    if(!partIter.hasNext())
     break;
    
    String val = partIter.next();

    cObj.addValue(ln,col,val,prop);
   }
   
  }

  return data;
 }

 private BlockHeader analyzeHeader(List<String> parts, int row) throws ParserException
 {
  BlockHeader hdr = new BlockHeaderImpl();
  
  Iterator<String> itr = parts.iterator();
  
  ColumnHeader partName;
  try
  {
   partName = string2ColumnHeader(itr.next());
   partName.setRow(row);
   partName.setCol(1);
  }
  catch(ParserException e)
  {
   e.setColumn(1);
   throw e;
  }
  
  
  hdr.setClassColumnHeader(partName);
  
  int col=1;
  while( itr.hasNext() )
  {
   col++;
   
   try
   {
    partName = string2ColumnHeader(itr.next());
    partName.setRow(row);
    partName.setCol(col);
   }
   catch(ParserException e)
   {
    e.setColumn(col);
    throw e;
   }
   
   
   hdr.addColumnHeader(partName);
  }
  
  
  return hdr;
 }

 
 private boolean isEmptyLine( List<String> parts )
 {
  for(String pt : parts )
   if( pt.length() != 0 )
    return false;
  
  return true;
 }
 
}

