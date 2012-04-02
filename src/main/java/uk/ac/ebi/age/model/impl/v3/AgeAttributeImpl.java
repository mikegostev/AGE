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
 
 @Override
 public AttributeClassRef getClassRef()
 {
  return classReference;
 }
 
 public AgeAttributeImpl(AttributeClassRef attrClassR, AttributedWritable host)
 {
  classReference = attrClassR;
  hostObject = host;
 }


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
 
 @Override
 public AttributedWritable getAttributedHost()
 {
  return hostObject;
 }
 
 @Override
 public AgeObjectWritable getMasterObject()
 {
  AttributedWritable host = getAttributedHost();
  
  while( host != null )
  {
   if( host instanceof AgeObjectWritable )
    return (AgeObjectWritable)host;
   
   if( host instanceof AgeRelationWritable )
    return ((AgeRelationWritable)host).getSourceObject();
   
   if( host instanceof AgeAttributeWritable )
    host = ((AgeAttributeWritable)host).getAttributedHost();

   return null;
  }
  
  return null;
 }
}
