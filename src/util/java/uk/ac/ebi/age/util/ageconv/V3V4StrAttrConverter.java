package uk.ac.ebi.age.util.ageconv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.age.model.impl.ModelFactoryImpl;
import uk.ac.ebi.age.model.impl.v3.AgeStringAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.storage.DataModuleReaderWriter;
import uk.ac.ebi.age.storage.impl.SerializedDataModuleReaderWriter;
import uk.ac.ebi.mg.packedstring.DualBandString;
import uk.ac.ebi.mg.packedstring.SingleBandString;

public class V3V4StrAttrConverter
{
 private static DataModuleReaderWriter modRW = new SerializedDataModuleReaderWriter();

 private static int totalCount;
 private static int singleCount;
 private static int dualCount;
 private static int packedLength;
 
 public static void main(String[] args)
 {
  if( args.length != 2 )
  {
   System.err.println("Arguments have to be <indir> <outdir>");
   System.exit(1);
  }
  
  File indir = new File( args[0] );
  
  if( ! indir.isDirectory() )
  {
   System.err.println("<indir> should be directory");
   System.exit(1);
  }
  
  File outdir = new File( args[1] );
  
  if( outdir.exists() )
  {
   if( ! outdir.isDirectory() )
   {
    System.err.println("<outdir> should be directory");
    System.exit(1);
   }
  }
  else
  {
   if( ! outdir.mkdirs() )
   {
    System.err.println("Can't create output directory");
    System.exit(1);
   }
  }
  
  processDirectory(indir, outdir);
 }

 private static void processDirectory(File indir, File outdir)
 {
//  File procDir = new File(indir,relPath);
  
  for( File subEl : indir.listFiles() )
  {
   if( subEl.isDirectory() )
   {
    File newOutDir =  new File(outdir,subEl.getName());
    
    newOutDir.mkdirs();
    
    processDirectory( new File(indir,subEl.getName()), newOutDir );
   }
   else
   {
    convert( subEl, new File(outdir,subEl.getName()) );
   }
  }
 }
 
 private static void convert( File infile, File outfile )
 {
  try
  {
   System.out.println("Processing file: "+infile.getAbsolutePath());
   
   DataModuleWritable mod = modRW.read(infile);
   
   for( AgeObjectWritable obj : mod.getObjects() )
   {
    convertAttributes( obj );

    if( obj.getRelations() != null );
     for( AgeRelationWritable rel : obj.getRelations() )
      convertAttributes( rel );
     
     System.out.println(
     "Total: " + totalCount + "\n" +  
     "Single: " + singleCount + "\n" +  
     "Dual: " + dualCount + "\n" +  
     "Packed: " + packedLength + "\n" +  
     "Packed avarage len: " + (((singleCount+dualCount) == 0)?0:(packedLength/(singleCount+dualCount))) + "\n" +  
     "Packed count %: " + (singleCount+dualCount)*100/totalCount
       );
   }
   
   modRW.write(mod, outfile);
   }
  catch(Exception e)
  {
   System.out.println("Can'n convert file: "+infile.getAbsolutePath()+" "+e.getMessage());
   
   e.printStackTrace();
  }

 }

 private static void convertAttributes(AttributedWritable host)
 {
  if( host.getAttributes() == null || host.getAttributes().size() == 0 )
   return;

  List<AgeAttributeWritable> attrList = new ArrayList<AgeAttributeWritable>( host.getAttributes() );
 
   
  for( int i=0; i < attrList.size(); i++ )
  {
   AgeAttributeWritable attr = attrList.get(i);
   
   if( attr instanceof AgeStringAttributeImpl )
   {
    totalCount++;
    
    AgeAttributeWritable newAttr = ModelFactoryImpl.getInstance().createAgeStringAttribute(attr.getClassReference(), host);
    
    newAttr.setValue(attr.getValue());
    newAttr.finalizeValue();
    
    Object val = newAttr.getValue();
    
    if( val instanceof SingleBandString )
    {
     singleCount++;
     packedLength+=((SingleBandString)val).length();
    }
    else if( val instanceof DualBandString )
    {
     dualCount++;
     packedLength+=((DualBandString)val).length();
    }
    
    if( attr.getAttributes() != null )
     for( AgeAttributeWritable subAttr : attr.getAttributes() )
      newAttr.addAttribute(subAttr);
    
    attrList.set(i, newAttr);
   }
   
   convertAttributes( attr );
  }
  
  host.setAttributes(attrList);
 }
 
}
