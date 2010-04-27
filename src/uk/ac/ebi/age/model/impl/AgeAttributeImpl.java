package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

public abstract class AgeAttributeImpl extends AgeSemanticElementImpl implements AgeAttributeWritable, Serializable
{
 private AgeAttributeClassPlugPluggable attrClassPlug;
 private int order; 

 public AgeAttributeImpl(AgeObject obj, AgeAttributeClass attrClass, SemanticModel sm)
 {
  super(sm);
  
  attrClassPlug= new AgeAttributeClassPlugPluggable(attrClass,sm);
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
 
 public void setOrder( int ord )
 {
  order=ord;
 }
 
 public int getOrder()
 {
  return order;
 }
 
 public void resetModel()
 {
  attrClassPlug.unplug();
 }
}
