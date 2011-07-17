package uk.ac.ebi.age.model.impl.v2;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.impl.v1.AttributedObject;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

class AgeExternalRelationImpl extends AttributedObject implements AgeExternalRelationWritable, Serializable
{
 private static final long serialVersionUID = 2L;

 private AgeRelationClassPlug relClassPlug; 
 private String objId;
 private int order;
 private AgeObjectWritable sourceObject;
 private transient AgeExternalRelationWritable invRelation;
 private transient AgeObjectWritable target;
 private boolean infered;

 public AgeExternalRelationImpl(AgeRelationClass relClass, AgeObjectWritable srcOb, String id, ContextSemanticModel sm)
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

 @Override
 public AgeExternalRelationWritable getInverseRelation()
 {
  return invRelation;
 }

 @Override
 public void setInverseRelation(AgeRelationWritable invRl)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setInverseRelation(AgeExternalRelationWritable invr)
 {
  invRelation=invr;
 }

 @Override
 public void setSourceObject(AgeObjectWritable ageObject)
 {
  sourceObject=ageObject;
 }
}

