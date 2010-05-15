package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;

abstract class AgeAttributeImpl extends AgeSemanticElementImpl implements AgeAttributeWritable, Serializable
{
 private static final long serialVersionUID = 1L;

 
 private AgeAttributeClassPlug attrClassPlug;
 private int order;
 private Collection<AgeAttributeWritable> qualifiers;

 protected AgeAttributeImpl()
 {
  super(null);
 }
 
 public AgeAttributeImpl(AgeAttributeClass attrClass, SemanticModel sm)
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
 
 public void setOrder( int ord )
 {
  order=ord;
 }
 
 public int getOrder()
 {
  return order;
 }
 
 @Override
 public Collection<AgeAttributeWritable> getQualifiers()
 {
  return qualifiers;
 }
 
 public void addQualifier(AgeAttributeWritable q )
 {
  if(qualifiers == null )
   qualifiers = new ArrayList<AgeAttributeWritable>(5);

  
  qualifiers.add(q);
 }
}
