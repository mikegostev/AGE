package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

class AgeRelationImpl extends AgeSemanticElementImpl implements AgeRelationWritable, Serializable
{
 private static final long serialVersionUID = 1L;
 
 private AgeRelationClassPlug relClassPlug;
 private AgeObjectWritable target;
 private int order;
 private boolean inferred=false;
 private Collection<AgeAttributeWritable> qualifiers;
 
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
