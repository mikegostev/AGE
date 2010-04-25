package uk.ac.ebi.age.model.impl;

import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.util.Plug;

public class AgeAttributeClassPlug implements Plug
{
 private String className;
 private transient AgeAttributeClass ageAttributeClass;
 
 public AgeAttributeClassPlug(AgeAttributeClass attrClass)
 {
  ageAttributeClass=attrClass;
  className = attrClass.getName();
 }

 public void unplug()
 {
  ageAttributeClass = null;
 }
 
 public boolean plug()
 {
  SemanticModel sm = SemanticManager.getInstance().getMasterModel();
  
  ageAttributeClass = sm.getAgeAttributeClass(className);
  
  if( ageAttributeClass != null )
   return true;
  
  return false;
 }
 
 public AgeAttributeClass getAgeAttributeClass()
 {
  if( ageAttributeClass == null )
   plug();
  
  return ageAttributeClass;
 }
}
