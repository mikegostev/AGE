package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

class AgeRelationImpl extends AttributedObject implements AgeRelationWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private AgeRelationClassPlug relClassPlug;
 private AgeObjectWritable target;
 private int order;
 private boolean inferred=false;
 
 public AgeRelationImpl(AgeObjectWritable targetObj, AgeRelationClass relClass, SemanticModel semanticModel)
 {
  super(semanticModel);
  relClassPlug= semanticModel.getAgeRelationClassPlug(relClass);
  target=targetObj;
 }

 public AgeRelationClass getAgeElClass()
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


 @Override
 public String getId()
 {
  return null;
 }

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }
}
