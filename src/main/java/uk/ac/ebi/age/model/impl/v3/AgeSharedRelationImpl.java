package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

import com.pri.util.collection.Collections;

public class AgeSharedRelationImpl implements AgeRelation, Serializable
{
 private static final long serialVersionUID = 3L;

 private RelationClassRef relClassRef;
 private AgeObjectWritable target;
 private AgeRelationWritable invRelation;

 
 @Override
 public List< ? extends AgeAttributeWritable> getAttributes()
 {
  return Collections.emptyList();
 }

 @Override
 public int getOrder()
 {
  return relClassRef.getOrder();
 }

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }

 @Override
 public AgeAttribute getAttribute(AgeAttributeClass cls)
 {
  return null;
 }

 @Override
 public List< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls)
 {
  return Collections.emptyList();
 }

 @Override
 public Collection< ? extends AgeAttributeClass> getAttributeClasses()
 {
  return Collections.emptyList();
 }

 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return getSourceObject().getSemanticModel();
 }


 @Override
 public AgeObjectWritable getSourceObject()
 {
  return invRelation.getTargetObject();
 }

 @Override
 public AgeObjectWritable getTargetObject()
 {
  return target;
 }

 @Override
 public AgeRelationClass getAgeElClass()
 {
  return relClassRef.getAgeRelationClass();
 }
 
 @Override
 public RelationClassRef getClassReference()
 {
  return relClassRef;
 }

 @Override
 public AgeRelationWritable getInverseRelation()
 {
  return invRelation;
 }

 @Override
 public boolean isInferred()
 {
  return true;
 }

 @Override
 public String getTargetObjectId()
 {
  return getTargetObject().getId();
 }

 
}
