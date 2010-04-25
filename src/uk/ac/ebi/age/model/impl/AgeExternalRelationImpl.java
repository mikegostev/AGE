package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;

public class AgeExternalRelationImpl extends AgeSemanticElementImpl implements AgeExternalRelationWritable, Serializable
{
 private AgeRelationClassPlug relClassPlug; 
 private String objId;
 private int order;
 private transient AgeObjectWritable target;
 
 public AgeExternalRelationImpl(AgeRelationClass relClass, String id, SemanticModel sm)
 {
  super(sm);
  
  relClassPlug = new AgeRelationClassPlugPluggable(relClass, sm);
  objId=id;
 }

 public AgeRelationClass getRelationClass()
 {
  return relClassPlug.getAgeRelationClass();
 }

 public AgeObjectWritable getTargetObject()
 {
  return target;
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

}

