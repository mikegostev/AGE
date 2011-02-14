package uk.ac.ebi.age.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.ebi.age.log.impl.BufferLogger;
import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.impl.AgeTab2AgeConverterImpl;
import uk.ac.ebi.age.storage.RelationResolveException;
import uk.ac.ebi.age.storage.exeption.ModuleStoreException;
import uk.ac.ebi.age.storage.impl.ser.SerializedStorage;

import com.pri.util.stream.StreamPump;

public class Test
{
 static final String ontologyFile = "file:///d:/workspaceGL/eclipse/ESD/semantic/test.owl";
// static final String ontologyFile = "file:///d:/workspaceGL/eclipse/ESD/semantic/ESD_model_hp_SMALL2.owl";
// static final String ontologyFile = "file:///d:/workspaceGL/eclipse/ESD/semantic/school.owl";

 /** /ESD/semantic/ESD_model_hp_SMALL2.owl
  * @param args
  */
 public static void main(String[] args)
 {
  
  try
  {
   SemanticManager smngr = SemanticManager.getInstance();
   
//   smngr.initModel(ontologyFile);
   
   ByteArrayOutputStream bais = new ByteArrayOutputStream();
   FileInputStream fis = new FileInputStream( new File("/d:/workspaceGL/eclipse/ESD/semantic/test.csv") );
   
   StreamPump.doPump(fis, bais);
   
   String text = new String(bais.toByteArray());
 
   AgeTabModule sbm =  AgeTabSyntaxParser.getInstance().parse(text);
   
   BufferLogger logBuf = new BufferLogger();
  
   DataModuleWritable dblock = new AgeTab2AgeConverterImpl().convert(sbm, smngr.getContextModel(new DefContext()), logBuf.getRootNode() );
   
   if( dblock == null )
   {
    System.out.println("Convertion failed");
    return;
   }
   
//   AgeStorageGrafImpl str = new AgeStorageGrafImpl();
   SerializedStorage str = new SerializedStorage();
   
   str.storeDataModule(dblock);
   
   System.out.println("Done");
   
   str.shutdown();
  }
//  catch(ModelException e)
//  {
//   // TODO Auto-generated catch block
//   e.printStackTrace();
//  }
  catch(FileNotFoundException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch bloc
   e.printStackTrace();
  }
  catch(RelationResolveException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(ParserException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(ModuleStoreException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }

 static class DefContext implements SubmissionContext
 {

  public boolean isCustomAttributeClassAllowed()
  {
   return true;
  }

  public boolean isCustomClassAllowed()
  {
   return true;
  }

  public boolean isCustomRelationClassAllowed()
  {
   return true;
  }

  @Override
  public boolean isCustomQualifierAllowed()
  {
   return true;
  }
  
 }
 
}

