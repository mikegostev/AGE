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

//  int getRecNum();
//  boolean isHorizontal();

  List<CellValue> getRecord(List<CellValue> parts);
 
  int getOrder( CellValue cv );
 }
 
 class HorizontalBlockSupplier implements BlockSupplier
 {
  private final List<String> firstLine;
  private final SpreadsheetReader reader;
  
  HorizontalBlockSupplier(SpreadsheetReader r, List<String> fstLine)
  {
   reader = r;
   
   firstLine = new ArrayList<String>( fstLine.size() );
   
   for( String s : fstLine )
    firstLine.add( s );
  }
  

  @Override
  public int getOrder(CellValue cv)
  {
   return cv.getRow();
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
    
   int ln = reader.getLineNumber();
   
   int col=0;  
   for( String s : firstLine )
   {
    col++;
    
    CellValue cv = new CellValue(s, getSyntaxProfile().getEscapeSequence() );
    
    cv.setRow(ln);
    cv.setCol(col);
    
    parts.add( cv );
   }
   
   firstLine.clear();
     
   return parts;
  }
  
 }
 
 class VerticalBlockSupplier implements BlockSupplier
 {
//  private List<List<String>> matrix = new ArrayList<List<String>>( 100 );
  
  private int ptr = 0;
  private final List<List<String>> lines = new ArrayList<List<String>>( 50 );
  private int maxDim = 0;
  private final int firstLineNum;
 
  VerticalBlockSupplier(SpreadsheetReader reader, List<String> fstLine)
  {
   firstLineNum = reader.getLineNumber();
   
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
  public int getOrder(CellValue cv)
  {
   return cv.getCol();
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
   
   int row=firstLineNum;
   int col = ptr+1;
   
   for( List<String> l : lines )
   {
    row++;
    
    CellValue cv = new CellValue(ptr >= l.size()?"":l.get(ptr), getSyntaxProfile().getEscapeSequence() ) ;
    
    cv.setCol(col);
    cv.setRow(row);
    
    line.add(  cv );
   }
   
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

   CellValue classRef = new CellValue(parts.get(0), profile.getEscapeSequence());
   
   BlockHeader header = new BlockHeaderImpl(data);

   if( classRef.matchSubstring(profile.getCommonSyntaxProfile().getHorizontalBlockPrefix(), 0) )
   {
    parts.set(0, classRef.getRawValue().substring(profile.getCommonSyntaxProfile().getHorizontalBlockPrefix().length()));
    block = new HorizontalBlockSupplier( reader, parts );
    
    header.setHorizontal(true);
   }
   else if( classRef.matchSubstring(profile.getCommonSyntaxProfile().getVerticalBlockPrefix(), 0) )
   {
    parts.set(0, classRef.getRawValue().substring(profile.getCommonSyntaxProfile().getVerticalBlockPrefix().length()));
    block = new VerticalBlockSupplier( reader, parts );
    header.setHorizontal(false);
   }
   else if(  profile.getClassSpecificSyntaxProfile(classRef.getRawValue()).isHorizontalBlockDefault() ) //If class is custom raw value will not match any specific profile 
   {
    block = new HorizontalBlockSupplier( reader, parts );
    header.setHorizontal(true);
   }
   else
   {
    block = new VerticalBlockSupplier( reader, parts );

    header.setHorizontal(false);
   }
   
   analyzeHeader( header, block.getRecord(cells) );
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
      cObj = data.createObject( id, header, block.getOrder(cell) );
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
        else
        {
         pfx = profileDef.getDefaultScopeIdPrefix();
         
         if( cell.matchSubstring(pfx,0) )
         {
          id = cell.getValue().substring(pfx.length());
          scope = profileDef.getDefaultIdScope();
         }
        }
       }
      }
      
   

      cObj = data.getOrCreateObject(id,header,block.getOrder(cell) );
      
      cObj.setIdScope(scope);
      cObj.setIdDefined( defined );
      cObj.setPrototype( cell.matchString( profileDef.getPrototypeObjectId() ) );
     }
    }
    else if( cObj == null )
     throw new ParserException(cell.getRow(), cell.getCol(), "Object identifier is expected");
   
    for( ClassReference prop : header.getColumnHeaders() )
    {
     
     if(!cellIter.hasNext())
      break;
     
     cell = cellIter.next();

     if( prop != null )
     {
//      if( cell.getValue().length() > 0 )
      cObj.addValue( new AgeTabValue(cell.getRow(), cell.getCol() , prop, cell)  );
     }
     else if( cell.getValue().trim().length() > 0 )
     {
      throw new ParserException(cell.getRow(), cell.getCol(),"Not empty value in the empty-headed column");
     }
     
    }
   }
   
  }

  return data;
 }

 private ClassReference createClassReference( CellValue cell, SyntaxProfileDefinition profDef ) throws ParserException
 {
  int embedSepLen = profDef.getDefaultEmbeddedObjectAttributeSeparator().length(); 
  
   String rawVal = cell.getRawValue();
   
   ClassReference embeddedProperty=null;
   
   int offs=0;
   while( true )
   {
    int pos = rawVal.indexOf( profDef.getDefaultEmbeddedObjectAttributeSeparator(), offs );
    
    if( pos == -1 )
     break;
    
    offs = pos+embedSepLen;

    if( ! cell.hasRed(pos, offs) )
    {
     embeddedProperty = createClassReference( new CellValue(cell.getRawValue().substring(offs), profDef.getEscapeSequence(),cell.getRow(),cell.getCol()), profDef );
     cell =  new CellValue(cell.getRawValue().substring(0,pos), profDef.getEscapeSequence(), cell.getRow(), cell.getCol() );
     break;
    }
    
   }
  
   if( cell.getValue().trim().length() == 0 )
    return null;
   
   ClassReference partName = null;
   
   try
   {
    partName = string2ClassReference(cell);
    partName.setRawReference(rawVal);

    partName.setRow(cell.getRow());
    partName.setCol(cell.getCol());

    if( partName.getQualifiers() != null )
    {
     for( ClassReference qref : partName.getQualifiers() )
     {
      qref.setRow(cell.getRow());
      qref.setCol(cell.getCol());
     }
    }
    
    if( embeddedProperty != null )
     partName.setEmbeddedClassRef(embeddedProperty);
   }
   catch(ParserException e)
   {
    e.setLineNumber(cell.getRow());
    e.setColumn(cell.getCol());

    throw e;
   }

   return partName;
 }
 
 private void analyzeHeader(BlockHeader hdr, List<CellValue> parts) throws ParserException
 {
  Iterator<CellValue> itr = parts.iterator();
  
  CellValue cell = itr.next();
  
  
  ClassReference partName;
  try
  {
   partName = string2ClassReference( cell );
   partName.setHorizontal(hdr.isHorizontal());
   partName.setRow(cell.getRow());
   partName.setCol(cell.getCol());
   partName.setRawReference(cell.getRawValue());
  }
  catch(ParserException e)
  {
   e.setLineNumber(cell.getRow());
   e.setColumn(cell.getCol());
   throw e;
  }
  
  
  hdr.setClassColumnHeader(partName);
  
  SyntaxProfileDefinition profDef = partName.isCustom()?getSyntaxProfile().getCommonSyntaxProfile():getSyntaxProfile().getClassSpecificSyntaxProfile(partName.getName());
  
  while( itr.hasNext() )
  {
   cell = itr.next();

   hdr.addColumnHeader( createClassReference(cell, profDef) );
  }
 }

 
 private static boolean isEmptyLine( List<String> parts )
 {
  for(String pt : parts )
   if( pt.length() != 0 )
    return false;
  
  return true;
 }
 
}

