package uk.ac.ebi.age.model.impl;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.SemanticModel;

class CustomAgeClassImpl extends AgeAbstractClassImpl implements AgeClass, Serializable 
{

 private String name;
 private String idPrefix;

 
 public CustomAgeClassImpl(String name, String pfx, SemanticModel sm)
 {
  super( sm );
  this.name=name;
  
  if( pfx == null )
   idPrefix = name.substring(0,1);
  else
   idPrefix=pfx;
  
 }

  public Collection<AgeRestriction> getRestrictions()
 {
  return null;
 }
 

 public Collection<AgeRestriction> getAllRestrictions()
 {
  return null;
 }

 
 public Collection<AgeClass> getSuperClasses()
 {
  return null;
 }
 
 public Collection<AgeClass> getSubClasses()
 {
  return null;
 }

 public String getName()
 {
  return name;
 }

 public boolean isCustom()
 {
  return true;
 }

 public Collection<AgeRestriction> getObjectRestrictions()
 {
  return null;
 }


 public Collection<AgeRestriction> getAllObjectRestrictions()
 {
  return null;
 }

 
 public Collection<AgeRestriction> getAttributeRestrictions()
 {
  return null;
 }
 
 public Collection<AgeRestriction> getAttributeAllRestrictions()
 {
  return null;
 }

 public String getIdPrefix()
 {
  return idPrefix;
 }

}
