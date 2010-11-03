package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

class AgeExternalRelationImpl extends AttributedObject implements AgeExternalRelationWritable, Serializable
{
 private static final long serialVersionUID = 1L;

 private AgeRelationClassPlug relClassPlug; 
 private String objId;
 private int order;
 private AgeObject sourceObject;
 private transient AgeObject target;
 private boolean infered;

 public AgeExternalRelationImpl(AgeRelationClass relClass, AgeObject srcOb, String id, SemanticModel sm)
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

 public AgeObject getTargetObject()
 {
  return target;
 }
 
 public AgeObject getSourceObject()
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
 public void setTargetObject(AgeObject obj)
 {
  objId=obj.getId();
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
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }
 
 @Override
 public AgeRelationWritable createClone()
 {
  AgeExternalRelationImpl clone = new AgeExternalRelationImpl(getAgeElClass(), getSourceObject(), getTargetObjectId(), getSemanticModel());
  clone.setOrder( getOrder() );
  clone.infered = infered;
  
  cloneAttributes(clone);
  
  return clone;
 }
}

