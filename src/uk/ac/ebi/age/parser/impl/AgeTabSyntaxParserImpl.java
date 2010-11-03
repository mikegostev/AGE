package uk.ac.ebi.age.parser.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ClassReference;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.service.IdGenerator;
import uk.ac.ebi.age.util.StringUtil;

public class AgeTabSyntaxParserImpl extends AgeTabSyntaxParser
{

 public AgeTabSubmission parse( String txt ) throws ParserException
 {
  AgeTabSubmission data = new AgeTabSubmissionImpl( this );
  
  
  int cpos = 0;
  int len = txt.length();
  int ln = 0;
  char colSep='\t';
  
//  String eol = "\r\n";
//  
//  if( txt.indexOf(eol) == -1)
//   eol="\n";
//
//  if( txt.indexOf(eol) == -1)
//   throw new ParserException(1,1,"File must contains at least 2 lines separated by either \\n or \\r\\n "); 
// 
//  int eolLength = eol.length();
  
//  while( txt.startsWith(eol, cpos) )
//  {
//   cpos+=eolLength;
//   ln++;
//  }
  
  while( cpos < len )
  {
   if( txt.charAt(cpos) == '\r' )
    cpos++;
   else if( txt.charAt(cpos) == '\n' )
   {
    ln++;
    cpos++;
   }
   else
    break;
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

   int pos = txt.indexOf('\n', cpos);

   String line = null;
   
   if(pos == -1)
   {
    line=txt.substring(cpos);
    cpos=len;
   }
   else
   {
    int tpos = cpos;   
    cpos = pos + 1;

    if( txt.charAt( pos-1 ) == '\r')
     pos--;
    
    line=txt.substring(tpos,pos);
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
//    int endSz=-1;
//    int i=-1;
//    for( String pt : parts )
//    {
//     i++;
//     
//     if( pt.length() == 0 )
//     {
//      if( endSz == -1 )
//       endSz=i;
//     }
//     else if( endSz != -1 )
//      throw new ParserException(ln,i,"Invalid cell contetns. Must be empty");
//    }
//    
//    
//    List<String> hdrParts = endSz != -1?parts=parts.subList(0, endSz):parts;

    newContext = false;

    header = new BlockHeaderImpl(data);
    analyzeHeader(header, parts, ln);
    
    cObj = null;
    
    continue;
   }
   
   Iterator<String> partIter = parts.iterator();
   
   String part = partIter.next();
   
   if( part.length() != 0 )
   {
    if( part.equals(getAnonymousObjectId()) )
    { 
     String id = IdGenerator.getInstance().getStringId();
     cObj = data.createObject(id,header,ln);
     cObj.setIdDefined(false);
    }
    else
    {
     String id = part;
     boolean defined = ! part.startsWith(getAnonymousObjectId());
     boolean stable = isUnqualifiedIdsStable() && defined;
     
     String stblIdPfx = getStableIdPrefix();
     if( part.startsWith(stblIdPfx) )
     {
      id = part.substring(stblIdPfx.length());
      
      if( !  id.startsWith(stblIdPfx) )
       stable = true;
     }
     else if( part.startsWith(getUnstableIdPrefix()) )
     {
      id = part.substring(getUnstableIdPrefix().length());
      
      if( !  id.startsWith(getUnstableIdPrefix()) )
       stable = false;
     }
     

     cObj = data.getOrCreateObject(id,header,ln);
     
     cObj.setIdStable(stable);
     cObj.setIdDefined( defined );
     cObj.setPrototype( part.equals(getCommonObjectId()) );
    }
   }
   else if( cObj == null )
    throw new ParserException(ln, 1, "Object identifier is expected");
   
   int col=1; 
   for( ClassReference prop : header.getColumnHeaders() )
   {
    col++;
    
    if(!partIter.hasNext())
     break;
    
    String val = partIter.next();

    if( prop != null )
    {
     if( val.length() > 0 )
      cObj.addValue(ln,col,val,prop);
    }
    else if( val.length() > 0 )
    {
     throw new ParserException(ln,col,"Not empty value in the empty-headed column");
    }
    
    
   }
   
  }

  return data;
 }

 private void analyzeHeader(BlockHeader hdr, List<String> parts, int row) throws ParserException
 {
//  BlockHeader hdr = new BlockHeaderImpl( this );
  
  Iterator<String> itr = parts.iterator();
  
  ClassReference partName;
  try
  {
   partName = string2ClassReference(itr.next());
   partName.setRow(row);
   partName.setCol(1);
  }
  catch(ParserException e)
  {
   e.setLineNumber(row);
   e.setColumn(1);
   throw e;
  }
  
  
  hdr.setClassColumnHeader(partName);
  
  int col=1;
  while( itr.hasNext() )
  {
   col++;
   
   String hdrStr = itr.next();

   
   
   if( hdrStr.trim().length() == 0 )
   {
    hdr.addColumnHeader(null);
    continue;
   }
   
   try
   {
    partName = string2ClassReference(hdrStr);
    partName.setRow(row);
    partName.setCol(col);
    
    if( partName.getQualifiers() != null )
    {
     for( ClassReference qref : partName.getQualifiers() )
     {
      qref.setRow(row);
      qref.setCol(col);
     }
    }
   }
   catch(ParserException e)
   {
    e.setLineNumber(row);
    e.setColumn(col);
    throw e;
   }
   
   
   hdr.addColumnHeader(partName);
  }
  
  
//  return hdr;
 }

 
 private boolean isEmptyLine( List<String> parts )
 {
  for(String pt : parts )
   if( pt.length() != 0 )
    return false;
  
  return true;
 }
 
}

