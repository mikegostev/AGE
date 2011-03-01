package uk.ac.ebi.age.test;

import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

public class ExtObjAttrTest
{
 public static void main( String[] argv )
 {
  SemanticModel mod = SemanticManager.createModelInstance();
  
  SemanticManager.getInstance().setMasterModel(mod);
  
  ContextSemanticModel cMod = SemanticManager.getInstance().getContextModel( new DefContext() );
  
  DataModuleWritable dm = mod.getModelFactory().createDataModule(cMod);
  
  AgeClassWritable cls = mod.createAgeClass("MyClass", "clsId", "MC", mod.getRootAgeClass());
 
  AgeAttributeClassWritable atClsS = mod.createAgeAttributeClass("MyText","MyText",DataType.STRING, mod.getRootAgeAttributeClass());
  
  AgeAttributeClassPlug cPlug = mod.getAgeAttributeClassPlug(atClsS);
  AttributeClassRef classRefS = mod.getModelFactory().createAttributeClassRef(cPlug, 0,"MyText");

  
  AgeAttributeClassWritable atClsO = mod.createAgeAttributeClass("MyObj","MyObj",DataType.OBJECT, mod.getRootAgeAttributeClass());
  
  for( int i=0; i<5; i++ )
  {
   AgeObjectWritable obj = mod.createAgeObject("obj"+i, cls);
   
   if( i == 1 )
   {
    obj.createExternalObjectAttribute("ObjRef"+i, classRefS);
   }
   
   dm.addObject(obj);
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

