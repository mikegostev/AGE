package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.util.Plug;

public class AgeRelationClassPlug implements Serializable, Plug
{
 private String className;
 private transient AgeRelationClass ageRelationClass;
 
 public AgeRelationClassPlug(AgeRelationClass relClass)
 {
  ageRelationClass=relClass;
  className = relClass.getName();
 }

 public void unplug()
 {
  ageRelationClass = null;
 }
 
 public boolean plug()
 {
  SemanticModel sm = SemanticManager.getInstance().getMasterModel();
  
  ageRelationClass = sm.getAgeRelationClass(className);
  
  if( ageRelationClass != null )
   return true;
  
  return false;
 }
 
 public AgeRelationClass getAgeRelationClass()
 {
  if( ageRelationClass == null )
   plug();
  
  return ageRelationClass;
 }
}
