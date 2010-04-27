package uk.ac.ebi.age.model.impl;

import java.util.Collection;
import java.util.LinkedList;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;

public class AgeAttributeClassImpl extends AgeAbstractClassImpl implements AgeAttributeClass
{
 private DataType dataType;
 private String name;

 private Collection<AgeAttributeClass> subClasses = new LinkedList<AgeAttributeClass>();
 private Collection<AgeAttributeClass> superClasses = new LinkedList<AgeAttributeClass>();
 private boolean custom;
 private AgeClass owner;

 
 public AgeAttributeClassImpl(String name, DataType type, SemanticModel sm)
 {
  super(sm);
  dataType=type;
  this.name=name;
 }

// public AgeAttribute createAttribute()
// {
//  getSemanticModel().
//  throw new dev.NotImplementedYetException();
// }

// public boolean validateValue(String val)
// {
//  // TODO Auto-generated method stub
//  throw new dev.NotImplementedYetException();
// }

 public DataType getDataType()
 {
  return dataType;
 }

 public void setDataType(DataType dataType)
 {
  this.dataType = dataType;
 }

 public String getName()
 {
  return name;
 }
 
 public void addSuperClass( AgeAttributeClass cl )
 {
  superClasses.add(cl);
 }

 public void addSubClass( AgeAttributeClass cl )
 {
  subClasses.add(cl);
 }

 
 public Collection<AgeAttributeClass> getSuperClasses()
 {
  return superClasses;
 }
 
 
 public Collection<AgeAttributeClass> getSubClasses()
 {
  return subClasses;
 }

 public AgeClass getOwningClass()
 {
  return owner;
 }

 public boolean isCustom()
 {
  return custom;
 }

 public void setCustom(boolean b)
 {
  custom=b;
 }

 public void setOwningClass(AgeClass cls)
 {
  owner=cls;
 }

}

