package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

abstract class AgeAttributeImpl extends AttributedObject implements AgeAttributeWritable, Serializable
{
 private static final long serialVersionUID = 1L;

 
 private AgeAttributeClassPlug attrClassPlug;
 private int order;

 protected AgeAttributeImpl()
 {
  super(null);
 }
 
 @Override
 public AttributeClassRef getClassRef()
 {
  return new _ClassRef();
 }
 
 public AgeAttributeImpl(AgeAttributeClass attrClass, ContextSemanticModel sm)
 {
  super(sm);
  
  attrClassPlug= sm.getAgeAttributeClassPlug(attrClass);
 }

 public AgeAttributeClass getAgeAttributeClass()
 {
  return attrClassPlug.getAgeAttributeClass();
 }


 public void finalizeValue()
 {
 }
 
 
 public AgeAttributeClass getAgeElClass()
 {
  return attrClassPlug.getAgeAttributeClass();
 }
 

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }
 
 public void setOrder( int ord )
 {
  order=ord;
 }
 
 public int getOrder()
 {
  return order;
 }

 private class _ClassRef implements AttributeClassRef
 {

  @Override
  public AgeAttributeClass getAttributeClass()
  {
   return attrClassPlug.getAgeAttributeClass();
  }

  @Override
  public int getOrder()
  {
   return order;
  }

  @Override
  public String getHeading()
  {
   return attrClassPlug.getAgeAttributeClass().getName();
  }
 }
}
