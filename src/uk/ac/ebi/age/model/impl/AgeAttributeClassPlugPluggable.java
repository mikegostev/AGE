package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.util.Plug;

public class AgeAttributeClassPlugPluggable implements Plug, Serializable, AgeAttributeClassPlug
{
 private String className;
 private transient AgeAttributeClass ageAttributeClass;
 private SemanticModel model;
 
 public AgeAttributeClassPlugPluggable(AgeAttributeClass attrClass, SemanticModel mdl)
 {
  ageAttributeClass=attrClass;
  className = attrClass.getName();
  model=mdl;
 }

 public void unplug()
 {
  ageAttributeClass = null;
 }
 
 public boolean plug()
 {
  ageAttributeClass = model.getDefinedAgeAttributeClass(className);
  
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
