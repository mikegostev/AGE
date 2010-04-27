package uk.ac.ebi.age.model.impl;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;

public class CustomAgeAttributeClassImpl extends AgeAbstractClassImpl implements AgeAttributeClass, Serializable
{
 private DataType dataType;
 private String name;

 private AgeClass owner;

 
 public CustomAgeAttributeClassImpl(String name2, DataType type, SemanticModel sm, AgeClass owner2)
 {
  super(sm);
  dataType=type;
  this.name=name2;
  owner = owner2;
 }

 public DataType getDataType()
 {
  return dataType;
 }

 public String getName()
 {
  return name;
 }
 
 
 public Collection<AgeAttributeClass> getSuperClasses()
 {
  return null;
 }
 
 
 public Collection<AgeAttributeClass> getSubClasses()
 {
  return null;
 }

 public AgeClass getOwningClass()
 {
  return owner;
 }

 public boolean isCustom()
 {
  return true;
 }

}

