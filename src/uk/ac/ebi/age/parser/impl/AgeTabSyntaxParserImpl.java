package uk.ac.ebi.age.parser.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ClassReference;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.SyntaxProfile;
import uk.ac.ebi.age.service.id.IdGenerator;
import uk.ac.ebi.age.util.StringUtil;

public class AgeTabSyntaxParserImpl extends AgeTabSyntaxParser
{
 interface BlockSupplier
 {
//  List<String> getHeaderLine();

  int getLineNum();

  List<String> getLine(List<String> parts);
 }
 
 static class HorizontalBlockSupplier implements BlockSupplier
 {
  private List<String> firstLine;
  private LineReader reader;
  
  HorizontalBlockSupplier(LineReader r, List<String> fstLine)
  {
   reader = r;
   firstLine = new ArrayList<String>( fstLine.size() );
   
   for( String s : fstLine )
    firstLine.add(s);
  }
  

//  @Override
//  public List<String> getHeaderLine()
//  {
//   return firstLine;
//  }


  @Override
  public int getLineNum()
  {
   return reader.getLineNumber();
  }


  @Override
  public List<String> getLine(List<String> parts)
  {
   if( firstLine != null )
   {
    
    if( parts != null )
    {
     parts.clear();
     
     for( String s : firstLine )
      parts.add(s);
     
     firstLine=null;
     
     return parts;
    }
    
    List<String> fl = firstLine;
    firstLine = null;
    return fl;
   }
   
   List<String> line = reader.readLine(parts);
   
   if( line == null )
    return null;
   
   if( isEmptyLine(line) )
    return null;
   
   return line;
  }
  
 }
 
 static class VerticalBlockSupplier implements BlockSupplier
 {
//  private List<List<String>> matrix = new ArrayList<List<String>>( 100 );
  
  private int ptr = 0;
  private List<List<String>> lines = new ArrayList<List<String>>( 50 );
  private int maxDim = 0;
 
  VerticalBlockSupplier(LineReader reader, List<String> fstLine)
  {
   List<String> line = new ArrayList<String>( fstLine.size() );
   
   for( String s : fstLine )
    line.add(s);

   lines.add(line);
   
   while( ( line = reader.readLine(null) ) != null && ! isEmptyLine(line) )
   {
    lines.add(line);
   
    if( maxDim < line.size() )
     maxDim = line.size();
   }
   
//   for( int i=0; i < maxDim; i++ )
//   {
//    line = new ArrayList<String>( lines.size() );
//    matrix.add(line);
//    
//    for( List<String> l : lines )
//     line.add( i >= l.size()?"":l.get(i));
//   }
   
  }
  

  @Override
  public int getLineNum()
  {
   return ptr;
  }


  @Override
  public List<String> getLine(List<String> line)
  {
   if( ptr >= maxDim )
    return null;
   
   if( line == null )
    line = new ArrayList<String>( lines.size() );
   else
    line.clear();
   
   for( List<String> l : lines )
    line.add( ptr >= l.size()?"":l.get(ptr));
   
   ptr++;
   
   return line;
  }
  
 }

 
 private static class LineReader
 {
  String text;
  String columnSep="\t";
  
  int cpos=0;
  int lpos;
  int ln=0;
  
  int textLen;
  
  LineReader( String text )
  {
   textLen = text.length();
   
   while( cpos < textLen )
   {
    if( text.charAt(cpos) == '\r' )
     cpos++;
    else if( text.charAt(cpos) == '\n' )
    {
     ln++;
     cpos++;
    }
    else
     break;
   }
   
   {  // looking for column separator
    int commaPos = text.indexOf(',',cpos);
    int tabPos = text.indexOf('\t',cpos);
    
    commaPos = commaPos==-1?Integer.MAX_VALUE:commaPos;
    tabPos = tabPos==-1?Integer.MAX_VALUE:tabPos;
    
    if( commaPos < tabPos )
     columnSep = ",";
   }
   
   this.text = text;
  }
  
  int getLineNumber()
  {
   return ln;
  }
  
  int getCurrentPosition()
  {
   return cpos;
  }
  
  int getLineBeginPosition()
  {
   return lpos;
  }
  
  List<String> readLine( List<String> accum )
  {
   if( cpos >= textLen )
    return null;
   
   lpos = cpos;
   
   ln++;

   if( accum == null )
    accum = new ArrayList<String>(50);
   else
    accum.clear();
   
   int pos = text.indexOf('\n', cpos);

   String line = null;
   
   if(pos == -1)
   {
    line=text.substring(cpos);
    cpos=text.length();
   }
   else
   {
    int tpos = cpos;   
    cpos = pos + 1;

    if( text.charAt( pos-1 ) == '\r')
     pos--;
    
    line=text.substring(tpos,pos);
   }

   StringUtil.splitExcelString(line, columnSep, accum);
  
   return accum;
  }
 }
 
 public AgeTabModule parse( String txt, SyntaxProfile profile ) throws ParserException
 {
  AgeTabModule data = new AgeTabModuleImpl( this );
  
  List<String> parts = new ArrayList<String>(100);

  LineReader reader = new LineReader(txt);
 
  BlockSupplier block;
  
  while( reader.readLine(parts) != null )
  {
   if( isEmptyLine(parts) )
    continue;

   String classRef = parts.get(0);
   
   if( classRef.startsWith(profile.getHorizontalBlockPrefix()) )
   {
    parts.set(0, classRef.substring(profile.getHorizontalBlockPrefix().length()));
    block = new HorizontalBlockSupplier( reader, parts );
   }
   else if( classRef.startsWith(profile.getVerticalBlockPrefix()) )
   {
    parts.set(0, classRef.substring(profile.getVerticalBlockPrefix().length()));
    block = new VerticalBlockSupplier( reader, parts );
   }
   else if( profile.isHorizontalBlockDefault() )
    block = new HorizontalBlockSupplier( reader, parts );
   else
    block = new VerticalBlockSupplier( reader, parts );
  
   BlockHeader header = new BlockHeaderImpl(data);
   analyzeHeader(header, block.getLine(parts), block.getLineNum() );
   data.addBlock(header);
   
   AgeTabObject cObj = null;
   
   parts.clear();
   while( block.getLine(parts) != null )
   {
    Iterator<String> partIter = parts.iterator();
    
    String part = partIter.next();
    
    if( part.length() != 0 )
    {
     if( part.equals( profile.getAnonymousObjectId() ) )
     { 
      String id = "??"+IdGenerator.getInstance().getStringId("tempObjectId");
      cObj = data.createObject(id,header,block.getLineNum());
      cObj.setIdDefined(false);
      cObj.setIdScope(IdScope.MODULE);
     }
     else
     {
      String id = part;
      boolean defined = ! part.startsWith( profile.getAnonymousObjectId());
      
      IdScope scope = defined? profile.getDefaultIdScope() : IdScope.MODULE;

      String pfx = profile.getGlobalIdPrefix();
      
      if( part.startsWith(pfx) )
      {
       id = part.substring(pfx.length());
       scope = IdScope.GLOBAL;
      }
      else
      {
       pfx = profile.getClusterIdPrefix();
       
       if( part.startsWith(pfx) )
       {
        id = part.substring(pfx.length());
        scope = IdScope.CLUSTER;
       }
       else
       {
        pfx = profile.getModuleIdPrefix();
        
        if( part.startsWith(pfx) )
        {
         id = part.substring(pfx.length());
         scope = IdScope.MODULE;
        }
       }
      }
      
   

      cObj = data.getOrCreateObject(id,header, block.getLineNum() );
      
      cObj.setIdScope(scope);
      cObj.setIdDefined( defined );
      cObj.setPrototype( part.equals( profile.getPrototypeObjectId() ) );
     }
    }
    else if( cObj == null )
     throw new ParserException(block.getLineNum(), 1, "Object identifier is expected"); // TODO provide correct coords here
   
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
       cObj.addValue(block.getLineNum(),col,val,prop);
     }
     else if( val.length() > 0 )
     {
      throw new ParserException(block.getLineNum(),col,"Not empty value in the empty-headed column");
     }
     
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

 
 private static boolean isEmptyLine( List<String> parts )
 {
  for(String pt : parts )
   if( pt.length() != 0 )
    return false;
  
  return true;
 }
 
}

