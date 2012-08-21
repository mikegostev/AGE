package uk.ac.ebi.age.model.impl.v3;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

import com.pri.util.collection.Collections;

public class InferredInverseRelation implements AgeRelationWritable, Serializable
{

 private static final long serialVersionUID = 3L;

 private AgeRelationWritable directRel;

 protected InferredInverseRelation( AgeRelationWritable dr )
 {
  directRel = dr;
 }
 
 @Override
 public AgeRelationClass getAgeElClass()
 {
  return directRel.getAgeElClass().getInverseRelationClass();
 }
 
 @Override
 public RelationClassRef getClassReference()
 {
  return null;
 }

 @Override
 public boolean isInferred()
 {
  return true;
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributes()
 {
  return Collections.emptyList();
 }

 @Override
 public int getOrder()
 {
  return 0;
 }

 @Override
 public AttributedClass getAttributedClass()
 {
  return getAgeElClass();
 }

 @Override
 public AgeAttributeWritable getAttribute(AgeAttributeClass cls)
 {
  return null;
 }

 @Override
 public Collection< ? extends AgeAttributeWritable> getAttributesByClass(AgeAttributeClass cls, boolean wSubCls)
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
  return directRel.getSemanticModel();
 }

 @Override
 public String getId()
 {
  // TODO Auto-generated method stub
  return "!!!inv-"+directRel.getId();
 }

 @Override
 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef attrClass, String val, ResolveScope scope)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void addAttribute(AgeAttributeWritable attr)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void removeAttribute(AgeAttributeWritable attr)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void reset()
 {
 }

 @Override
 public void sortAttributes()
 {
 }

 @Override
 public void pack()
 {
 }

 @Override
 public void setInferred(boolean inf)
 {
  if( !inf )
   throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationWritable createClone(AgeObjectWritable host)
 {
  return new InferredInverseRelation(directRel);
 }

 @Override
 public AgeObjectWritable getSourceObject()
 {
  return directRel.getTargetObject();
 }

 @Override
 public AgeObjectWritable getTargetObject()
 {
  return directRel.getSourceObject();
 }

 @Override
 public AgeRelationWritable getInverseRelation()
 {
  return directRel;
 }

 @Override
 public void setInverseRelation(AgeRelationWritable invRl)
 {
  directRel = invRl;
 }

 @Override
 public void setAttributes(List<AgeAttributeWritable> attrs)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public String getTargetObjectId()
 {
  return getTargetObject().getId();
 }
}
