package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.service.IdGenerator;

class CustomAgeAttributeClassImpl extends AgeAbstractClassImpl implements AgeAttributeClassWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private DataType dataType;
 private String name;
 private String id;

 private AgeClass owner;

 
 public CustomAgeAttributeClassImpl(String name2, DataType type, SemanticModel sm, AgeClass owner2)
 {
  super(sm);
  dataType=type;
  this.name=name2;
  owner = owner2;
  
  id = "AgeAttributeClass"+IdGenerator.getInstance().getStringId();
 }

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
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


 @Override
 public boolean isAbstract()
 {
  return false;
 }

 @Override
 public void addSubClass(AgeAttributeClass sbcls)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addSuperClass(AgeAttributeClass sbcls)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setDataType(DataType typ)
 {
  dataType=typ;
 }

 @Override
 public void setAbstract(boolean b)
 {
 }

 @Override
 public void addAlias(String ali)
 {
 }

}

