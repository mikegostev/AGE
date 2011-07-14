package uk.ac.ebi.age.test;

import java.util.Collection;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.PermissionManager;
import uk.ac.ebi.age.entity.ID;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;

public class ExtObjAttrTest
{
 public static void main( String[] argv )
 {
  SemanticModel mod = SemanticManager.createModelInstance();
  
  SemanticManager.getInstance().setMasterModel(mod);
  
  ContextSemanticModel cMod = SemanticManager.getInstance().getContextModel();
  
  DataModuleWritable dm = mod.getModelFactory().createDataModule(cMod);
  
  AgeClassWritable cls = mod.createAgeClass("MyClass", "clsId", "MC", mod.getRootAgeClass());
 
  AgeAttributeClassWritable atClsS = mod.createAgeAttributeClass("MyText","MyText",DataType.STRING, mod.getRootAgeAttributeClass());
  
  AgeAttributeClassPlug cPlug = mod.getAgeAttributeClassPlug(atClsS);
  AttributeClassRef classRefS = mod.getModelFactory().createAttributeClassRef(cPlug, 0,"MyText");

  
  AgeAttributeClassWritable atClsO = mod.createAgeAttributeClass("MyObj","MyObj",DataType.OBJECT, mod.getRootAgeAttributeClass());
  
  cPlug = mod.getAgeAttributeClassPlug(atClsO);
  AttributeClassRef classRefO = mod.getModelFactory().createAttributeClassRef(cPlug, 0, "MyObj");

  for( int i=0; i<5; i++ )
  {
   AgeObjectWritable obj = mod.createAgeObject("obj"+i, cls);
   
   obj.createAgeAttribute(classRefS).setValue("Val");
   
   if( i == 1 )
   {
    obj.createExternalObjectAttribute("ObjRef"+i, classRefO);
   }
   else if( i == 2 )
   {
    obj.createExternalObjectAttribute("ObjRef"+i+"+1L1", classRefO);
    obj.createAgeAttribute(classRefS).setValue("Val2");
    AttributedWritable attr = obj.createExternalObjectAttribute("ObjRef"+i+"+2L1", classRefO);
    obj.createAgeAttribute(classRefS).setValue("Val3");
    obj.createExternalObjectAttribute("ObjRef"+i+"+3L1", classRefO);

    attr.createAgeAttribute(classRefS).setValue("Val2+");
    attr.createExternalObjectAttribute("ObjRef"+i+"+1L2", classRefO);
    attr.createExternalObjectAttribute("ObjRef"+i+"+2L2", classRefO);
    
    AgeAttributeWritable sat = attr.createAgeAttribute(classRefS);
    sat.setValue("Val3+");
    attr=sat;
    
    attr.createAgeAttribute(classRefS).setValue("Val2++");
    attr.createExternalObjectAttribute("ObjRef"+i+"+1L3", classRefO);
    attr.createAgeAttribute(classRefS).setValue("Val3++");
    attr.createExternalObjectAttribute("ObjRef"+i+"+2L3", classRefO);
   }
   
   dm.addObject(obj);
  }
  
  for( AgeExternalObjectAttributeWritable extO : dm.getExternalObjectAttributes() )
  {
   System.out.println( extO.getTargetObjectId() );
  }
  
 }

 static class DefPM implements PermissionManager
 {

  @Override
  public Permit checkSystemPermission(SystemAction act)
  {
   return Permit.ALLOW;
  }

  @Override
  public Permit checkPermission(SystemAction act, ID objId)
  {
   return Permit.ALLOW;
  }

  @Override
  public Permit checkPermission(SystemAction act, String objOwner, ID objId)
  {
   return Permit.ALLOW;
  }

  @Override
  public Permit checkSystemPermission(SystemAction act, String user)
  {
   return Permit.ALLOW;
  }

  @Override
  public Collection<TagRef> getEffectiveTags(ID objId)
  {
   // TODO Auto-generated method stub
   return null;
  }

  @Override
  public Collection<TagRef> getAllowTags(SystemAction act, String user)
  {
   // TODO Auto-generated method stub
   return null;
  }

  @Override
  public Collection<TagRef> getDenyTags(SystemAction act, String user)
  {
   // TODO Auto-generated method stub
   return null;
  }
  
 }
}

