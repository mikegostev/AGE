package uk.ac.ebi.age.util.ageconv;

import java.io.File;

import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.storage.DataModuleReaderWriter;
import uk.ac.ebi.age.storage.impl.SerializedDataModuleReaderWriter;

public class FixGroupId
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
//   System.out.println("Processing file: "+infile.getAbsolutePath());
   
   DataModuleWritable mod = modRW.read(infile);
   
   for( AgeObjectWritable obj : mod.getObjects() )
   {
    if( obj.getClassReference().getHeading().equals("Group") )
    {
     String id = obj.getId();
     
     String accession = null;
     
     for( AgeAttributeWritable attr : obj.getAttributes() )
     {
      if( attr.getClassReference().getHeading().equals("Group Accession") )
      {
       accession = attr.getValue().toString();
       break;
      }
     }
     
     if( ! id.equals( accession ) )
     {
      System.out.print("---");
      
      System.out.println(" Group: "+id+" Accession: "+accession+" File: "+infile.getAbsolutePath());
     }
     
    }
   }
   
//   modRW.write(mod, outfile);
  }
  catch(Exception e)
  {
   System.out.println("Can'n convert file: "+infile.getAbsolutePath()+" "+e.getMessage());
   
   e.printStackTrace();
  }

 }


}
