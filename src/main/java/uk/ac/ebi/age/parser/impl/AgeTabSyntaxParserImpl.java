package uk.ac.ebi.age.parser.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.AgeTabValue;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.CellValue;
import uk.ac.ebi.age.parser.ClassReference;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.SyntaxProfile;
import uk.ac.ebi.age.parser.SyntaxProfileDefinition;
import uk.ac.ebi.age.service.id.IdGenerator;

import com.pri.util.SpreadsheetReader;

public class AgeTabSyntaxParserImpl extends AgeTabSyntaxParser
{
 public AgeTabSyntaxParserImpl(SyntaxProfile sp)
 {
  super(sp);
 }


 interface BlockSupplier
 {
//  List<String> getHeaderLine();

  int getRecNum();

  List<CellValue> getRecord(List<CellValue> parts);
 }
 
 class HorizontalBlockSupplier implements BlockSupplier
 {
  private List<String> firstLine;
  private SpreadsheetReader reader;
  
  HorizontalBlockSupplier(SpreadsheetReader r, List<String> fstLine)
  {
   reader = r;
   firstLine = new ArrayList<String>( fstLine.size() );
   
   for( String s : fstLine )
    firstLine.add( s );
  }
  

//  @Override
//  public List<String> getHeaderLine()
//  {
//   return firstLine;
//  }


  @Override
  public int getRecNum()
  {
   return reader.getLineNumber();
  }


  @Override
  public List<CellValue> getRecord(List<CellValue> parts)
  {
   List<String> line = null;
   
   if( firstLine.size() == 0 )
   {
    line = reader.readRow(firstLine);
   
    if( line == null )
     return null;
    
    if( isEmptyLine(line) )
     return null;
   }
   
   if( parts == null )
    parts = new ArrayList<CellValue>( firstLine.size() );
   else
    parts.clear();
    
     
   for( String s : firstLine )
    parts.add( new CellValue(s, getSyntaxProfile().getEscapeSequence() ) );
     
   firstLine.clear();
     
   return parts;
  }
  
 }
 
 class VerticalBlockSupplier implements BlockSupplier
 {
//  private List<List<String>> matrix = new ArrayList<List<String>>( 100 );
  
  private int ptr = 0;
  private List<List<String>> lines = new ArrayList<List<String>>( 50 );
  private int maxDim = 0;
 
  VerticalBlockSupplier(SpreadsheetReader reader, List<String> fstLine)
  {
   List<String> line = new ArrayList<String>( fstLine.size() );
   
   for( String s : fstLine )
    line.add(s);

   lines.add(line);
   
   while( ( line = reader.readRow(null) ) != null && ! isEmptyLine(line) )
   {
    lines.add(line);
   
    if( maxDim < line.size() )
     maxDim = line.size();
   }
   
  
  }
  

  @Override
  public int getRecNum()
  {
   return ptr;
  }


  @Override
  public List<CellValue> getRecord(List<CellValue> line)
  {
   if( ptr >= maxDim )
    return null;
   
   if( line == null )
    line = new ArrayList<CellValue>( lines.size() );
   else
    line.clear();
   
   for( List<String> l : lines )
    line.add(  new CellValue(ptr >= l.size()?"":l.get(ptr), getSyntaxProfile().getEscapeSequence() ) );
   
   ptr++;
   
   return line;
  }
  
 }

 

 @Override
 public AgeTabModule parse( String txt ) throws ParserException
 {
  SyntaxProfile profile = getSyntaxProfile();
  
  AgeTabModule data = new AgeTabModuleImpl( getSyntaxProfile() );
  
  List<String> parts = new ArrayList<String>(100);
  List<CellValue> cells = new ArrayList<CellValue>(100);

  SpreadsheetReader reader = new SpreadsheetReader(txt);
 
  BlockSupplier block;
  
  while( reader.readRow(parts) != null )
  {
   if( isEmptyLine(parts) )
    continue;

   String classRef = parts.get(0);
   
   BlockHeader header = new BlockHeaderImpl(data);

   if( classRef.startsWith(profile.getCommonSyntaxProfile().getHorizontalBlockPrefix()) )
   {
    parts.set(0, classRef.substring(profile.getCommonSyntaxProfile().getHorizontalBlockPrefix().length()));
    block = new HorizontalBlockSupplier( reader, parts );
    
    header.setHorizontal(true);
   }
   else if( classRef.startsWith(profile.getCommonSyntaxProfile().getVerticalBlockPrefix()) )
   {
    parts.set(0, classRef.substring(profile.getCommonSyntaxProfile().getVerticalBlockPrefix().length()));
    block = new VerticalBlockSupplier( reader, parts );
    header.setHorizontal(false);
   }
   else if( profile.getClassSpecificSyntaxProfile(classRef).isHorizontalBlockDefault() )
   {
    block = new HorizontalBlockSupplier( reader, parts );
    header.setHorizontal(true);
   }
   else
   {
    block = new VerticalBlockSupplier( reader, parts );

    header.setHorizontal(false);
   }
   
   analyzeHeader(header, block.getRecord(cells), block.getRecNum() );
   data.addBlock(header);
   
   AgeTabObject cObj = null;
   
   SyntaxProfileDefinition profileDef = header.getClassColumnHeader().isCustom()?
     profile.getCommonSyntaxProfile():profile.getClassSpecificSyntaxProfile(header.getClassColumnHeader().getName());
   
   parts.clear();
   while( block.getRecord(cells) != null )
   {
    Iterator<CellValue> cellIter = cells.iterator();
    
    CellValue cell = cellIter.next();
    
    cell.trim();
    
    if( cell.getValue().length() != 0 )
    {
     if( cell.matchString(profileDef.getAnonymousObjectId()) )
     { 
      String id = "??"+IdGenerator.getInstance().getStringId("tempObjectId");
      cObj = data.createObject(id,header,block.getRecNum());
      cObj.setIdDefined(false);
      cObj.setIdScope(IdScope.MODULE);
     }
     else
     {
      boolean defined = ! cell.matchSubstring(profileDef.getAnonymousObjectId(), 0);
      
      IdScope scope = defined? profileDef.getDefaultIdScope() : IdScope.MODULE;

      String pfx = profileDef.getGlobalIdPrefix();
      
      String id = cell.getValue();
      
      if( cell.matchSubstring(pfx,0) )
      {
       id = cell.getValue().substring(pfx.length());
       scope = IdScope.GLOBAL;
      }
      else
      {
       pfx = profileDef.getClusterIdPrefix();
       
       if( cell.matchSubstring(pfx,0) )
       {
        id = cell.getValue().substring(pfx.length());
        scope = IdScope.CLUSTER;
       }
       else
       {
        pfx = profileDef.getModuleIdPrefix();
        
        if( cell.matchSubstring(pfx,0) )
        {
         id = cell.getValue().substring(pfx.length());
         scope = IdScope.MODULE;
        }
       }
      }
      
   

      cObj = data.getOrCreateObject(id,header, block.getRecNum() );
      
      cObj.setIdScope(scope);
      cObj.setIdDefined( defined );
      cObj.setPrototype( cell.matchString( profileDef.getPrototypeObjectId() ) );
     }
    }
    else if( cObj == null )
     throw new ParserException(block.getRecNum(), 1, "Object identifier is expected"); // TODO provide correct coords here
   
    int col=1; 
    for( ClassReference prop : header.getColumnHeaders() )
    {
     col++;
     
     if(!cellIter.hasNext())
      break;
     
     cell = cellIter.next();

     if( prop != null )
     {
      if( cell.getValue().length() > 0 )
       cObj.addValue( new AgeTabValue(block.getRecNum(), col, prop, cell)  );
     }
     else if( cell.getValue().trim().length() > 0 )
     {
      throw new ParserException(block.getRecNum(),col,"Not empty value in the empty-headed column");
     }
     
    }
   }
   
  }

  return data;
 }

 private void analyzeHeader(BlockHeader hdr, List<CellValue> parts, int row) throws ParserException
 {
//  BlockHeader hdr = new BlockHeaderImpl( this );
  
  Iterator<CellValue> itr = parts.iterator();
  
  CellValue cell = itr.next();
  
  ClassReference partName;
  try
  {
   partName = string2ClassReference( cell );
   partName.setHorizontal(hdr.isHorizontal());
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
  
  int ord=1;
  while( itr.hasNext() )
  {
   ord++;
   
   cell = itr.next();

   
   
   if( cell.getValue().trim().length() == 0 )
   {
    hdr.addColumnHeader(null);
    continue;
   }
   
   try
   {
    partName = string2ClassReference(cell);
    
    if( hdr.isHorizontal() )
    {
     partName.setRow(row);
     partName.setCol(ord);
    }
    else
    {
     partName.setRow(row+ord);
     partName.setCol(1);
    }
    
    if( partName.getQualifiers() != null )
    {
     for( ClassReference qref : partName.getQualifiers() )
     {
      if( hdr.isHorizontal() )
      {
       qref.setRow(row);
       qref.setCol(ord);
      }
      else
      {
       qref.setRow(row+ord);
       qref.setCol(1);
      }
     }
    }
   }
   catch(ParserException e)
   {
    if( hdr.isHorizontal() )
    {
     e.setLineNumber(row);
     e.setColumn(ord);
    }
    else
    {
     e.setLineNumber(row+ord);
     e.setColumn(1);
    }

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

