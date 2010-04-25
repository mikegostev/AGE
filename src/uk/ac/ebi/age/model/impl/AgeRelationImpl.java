package uk.ac.ebi.age.model.impl;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

class AgeRelationImpl extends AgeSemanticElementImpl implements AgeRelationWritable
{
 private AgeRelationClassPlug relClassPlug;
 private AgeObjectWritable target;
 private int order;
 private boolean inferred=false;
 
 public AgeRelationImpl(AgeObjectWritable targetObj, AgeRelationClass relClass, SemanticModel semanticModel)
 {
  super(semanticModel);
  relClassPlug= new AgeRelationClassPlug(relClass);
  target=targetObj;
 }

 public AgeRelationClass getRelationClass()
 {
  return relClassPlug.getAgeRelationClass();
 }

 public AgeObjectWritable getTargetObject()
 {
  return target;
 }

 public int getOrder()
 {
  return order;
 }

 public void setOrder(int ord)
 {
  order=ord;
 }

 public void setInferred( boolean inf )
 {
  inferred = inf;
 }

 
 public boolean isInferred()
 {
  return inferred;
 }

}
