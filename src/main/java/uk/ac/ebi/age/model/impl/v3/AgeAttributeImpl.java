package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

abstract public class AgeAttributeImpl extends AttributedObject implements AgeAttributeWritable, Serializable
{
 private static final long serialVersionUID = 3L;

 private AttributeClassRef classReference;
 private AttributedWritable hostObject;

 
 protected AgeAttributeImpl()
 {}
 

 public AgeAttributeImpl(AttributeClassRef attrClassR, AttributedWritable host)
 {
  classReference = attrClassR;
  hostObject = host;
 }


 @Override
 public void finalizeValue()
 {
 }
 
 @Override
 public void setAttributedHost( AttributedWritable ho )
 {
  hostObject=ho;
 }
 
 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return hostObject.getSemanticModel();
 }

 @Override
 public AgeAttributeClass getAgeElClass()
 {
  return classReference.getAgeElClass();
 }
 
 @Override
 public AttributeClassRef getClassReference()
 {
  return classReference;
 }


 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }
 
 
 @Override
 public int getOrder()
 {
  return classReference.getOrder( );
 }
 
 @Override
 public AttributedWritable getAttributedHost()
 {
  return hostObject;
 }
 
 @Override
 public AgeObjectWritable getMasterObject()
 {
  AttributedWritable host = getAttributedHost();
  
  if( host instanceof AgeObjectWritable )
   return (AgeObjectWritable)host;
  
  if( host instanceof AgeRelationWritable )
   return ((AgeRelationWritable)host).getSourceObject();
  
  if( host instanceof AgeAttributeWritable )
   return ((AgeAttributeWritable)host).getMasterObject();
  
  return null;
 }
 
 @Override
 public String toString()
 {
  Object val = getValue();
  
  return getAgeElClass().getName()+"="+(val!=null?val.toString():"null");
 }
}
