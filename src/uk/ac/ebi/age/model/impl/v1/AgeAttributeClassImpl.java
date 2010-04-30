package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;

class AgeAttributeClassImpl extends AgeAbstractClassImpl implements AgeAttributeClassWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private DataType dataType;
 private String name;
 private String id;


 private Collection<AgeAttributeClass> subClasses = new LinkedList<AgeAttributeClass>();
 private Collection<AgeAttributeClass> superClasses = new LinkedList<AgeAttributeClass>();

 protected AgeAttributeClassImpl()
 {
  super(null);
 }
 
 public AgeAttributeClassImpl(String name, String id, DataType type, SemanticModel sm)
 {
  super(sm);
  dataType=type;
  this.name=name;
  this.id=id;
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
  return null;
 }

 public boolean isCustom()
 {
  return false;
 }

 public String getId()
 {
  return id;
 }

}

