package uk.ac.ebi.age.model.impl;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;

public class AgeExternalRelationImpl extends AgeSemanticElementImpl implements AgeExternalRelationWritable
{
 private AgeRelationClass relClass; 
 private String objId;
 private int order;
 
 public AgeExternalRelationImpl(AgeRelationClass targetClass, String id, SemanticModel sm)
 {
  super(sm);
  
  this.relClass = targetClass;
  objId=id;
 }

 public AgeRelationClass getRelationClass()
 {
  return relClass;
 }

 public AgeObjectWritable getTargetObject()
 {
  return null;
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

