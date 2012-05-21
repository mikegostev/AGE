package uk.ac.ebi.age.util.ageconv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.storage.DataModuleReaderWriter;
import uk.ac.ebi.age.storage.impl.SerializedDataModuleReaderWriter;

public class FixGroupIdByList
{
 private static DataModuleReaderWriter modRW = new SerializedDataModuleReaderWriter();

 static List<String> ids  = new ArrayList<String>()
   {{
    add("xx56xx/xx5645/2GAGA_DKmodule1");
    add("xx43xx/xx4361/13GAE_CNGEOD_CN1682GAE_CNGEOD_CN1682_DKmodule1");
    add("xx37xx/xx37d8/9GMS_CNMMRRCGMS_CNMMRRC_DKmodule1");
    add("xxc0xx/xxc0aa/12GAE_CNMEXP_CN385GAE_CNMEXP_CN385_DKmodule1");
    add("xxcbxx/xxcbdf/11GAE_CNTOXM_CN13GAE_CNTOXM_CN13_DKmodule1");
    add("xxbaxx/xxba3f/11GAE_CNTOXM_CN10GAE_CNTOXM_CN10_DKmodule1");
    add("xx78xx/xx78df/6GMS_CNEMGMS_CNEM_DKmodule1");
    add("xx83xx/xx8396/7GMS_CNJAXGMS_CNJAX_DKmodule1");
    add("xxf3xx/xxf3e1/13GAE_CNMEXP_CN1221GAE_CNMEXP_CN1221_DKmodule1");
   }};
 


   
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
  
  for( String f : ids )
  {
   File infile = new File( indir, f );
   
   try
   {
    DataModuleWritable mod = modRW.read(infile);
   
    boolean modified = false;
    
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
       
       modified = true;
       
       obj.setId(accession);
       obj.setIdScope(IdScope.GLOBAL);
      }
      
     }
    }
    
    if( modified )
    {
     File outfiFile = new File( outdir, f );
     
     outfiFile.getParentFile().mkdirs();
     
     FileUtils.copyFile(infile, outfiFile);
     
     modRW.write(mod, infile);
     
    }
   
   }
   catch(IOException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   catch(ClassNotFoundException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }

   
  }
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
