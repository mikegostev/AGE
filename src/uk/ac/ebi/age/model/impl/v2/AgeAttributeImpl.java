package uk.ac.ebi.age.model.impl.v2;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.impl.v1.AttributedObject;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

abstract class AgeAttributeImpl extends AttributedObject implements AgeAttributeWritable, Serializable
{
 private static final long serialVersionUID = 1L;

 private AttributeClassRef classReference;
 
 protected AgeAttributeImpl()
 {
  super(null);
 }
 
 @Override
 public AttributeClassRef getClassRef()
 {
  return classReference;
 }
 
 public AgeAttributeImpl(AttributeClassRef attrClassR, ContextSemanticModel sm)
 {
  super(sm);
  
  classReference = attrClassR;
 }


 public void finalizeValue()
 {
 }
 
 
 public AgeAttributeClass getAgeElClass()
 {
  return classReference.getAttributeClass();
 }
 

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }
 
 
 public int getOrder()
 {
  return classReference.getOrder( );
 }

}
