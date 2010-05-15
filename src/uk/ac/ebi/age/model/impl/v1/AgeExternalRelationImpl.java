package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;

class AgeExternalRelationImpl extends AgeSemanticElementImpl implements AgeExternalRelationWritable, Serializable
{
 private static final long serialVersionUID = 1L;

 private AgeRelationClassPlug relClassPlug; 
 private String objId;
 private int order;
 private AgeObjectWritable sourceObject;
 private transient AgeObjectWritable target;
 private boolean infered;
 private Collection<AgeAttributeWritable> qualifiers;

 public AgeExternalRelationImpl(AgeRelationClass relClass, AgeObjectWritable srcOb, String id, SemanticModel sm)
 {
  super(sm);
  
  relClassPlug = sm.getAgeRelationClassPlug(relClass);

  objId=id;
  sourceObject=srcOb;
 }

 public AgeRelationClass getAgeElClass()
 {
  return relClassPlug.getAgeRelationClass();
 }

 public AgeObjectWritable getTargetObject()
 {
  return target;
 }
 
 public AgeObjectWritable getSourceObject()
 {
  return sourceObject;
 }

 public String getTargetObjectId()
 {
  return objId;
 }

 public int getOrder()
 {
  return order;
 }

 public void setOrder(int ord)
 {
  order=ord;
 }

 @Override
 public void setTargetObject(AgeObjectWritable obj)
 {
  target = obj;
 }

 @Override
 public String getId()
 {
  return null;
 }

 @Override
 public void setInferred(boolean inf)
 {
  infered=inf;
 }

 @Override
 public boolean isInferred()
 {
  return infered;
 }
 
 @Override
 public Collection<AgeAttributeWritable> getQualifiers()
 {
  return qualifiers;
 }
 
 @Override
 public void addQualifier(AgeAttributeWritable q )
 {
  if(qualifiers == null )
   qualifiers = new ArrayList<AgeAttributeWritable>(5);

  
  qualifiers.add(q);
 }
}

