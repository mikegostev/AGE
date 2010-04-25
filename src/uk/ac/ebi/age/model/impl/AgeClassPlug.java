package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.util.Plug;

public class AgeClassPlug implements Serializable, Plug
{
 private String className;
 private transient AgeClass ageClass;
 
 public AgeClassPlug(AgeClass cls)
 {
  className = cls.getName();
  ageClass = cls;
 }

 public void unplug()
 {
  ageClass = null;
 }
 
 public boolean plug()
 {
  SemanticModel sm = SemanticManager.getInstance().getMasterModel();
  
  ageClass = sm.getAgeClass(className);
  
  if( ageClass != null )
   return true;
  
  return false;
 }
 
 public AgeClass getAgeClass()
 {
  if( ageClass == null )
   plug();
  
  return ageClass;
 }
}
