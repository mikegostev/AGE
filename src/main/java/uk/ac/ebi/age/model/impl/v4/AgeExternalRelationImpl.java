package uk.ac.ebi.age.model.impl.v4;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.impl.v3.AttributedObject;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

public class AgeExternalRelationImpl extends AttributedObject implements AgeExternalRelationWritable, Serializable
{
 private static final long serialVersionUID = 4L;

 private RelationClassRef relClassRef; 
 private String objId;
 private AgeObjectWritable sourceObject;
 private transient AgeExternalRelationWritable invRelation;
 private transient AgeObjectWritable target;
 private boolean infered;
 private ResolveScope tgtScope;

 protected AgeExternalRelationImpl(RelationClassRef cRef, AgeObjectWritable srcOb, String id, ResolveScope scp)
 {
  relClassRef=cRef;

  objId=id;
  sourceObject=srcOb;
  tgtScope=scp;
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
 public AgeObjectWritable getTargetObject()
 {
  return target;
 }
 
 @Override
 public AgeObjectWritable getSourceObject()
 {
  return sourceObject;
 }

 
 @Override
 public String getTargetObjectId()
 {
  return objId;
 }

 @Override
 public int getOrder()
 {
  return relClassRef.getOrder();
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
 public AgeRelationWritable createClone( AgeObjectWritable src )
 {
  AgeExternalRelationImpl clone = new AgeExternalRelationImpl(relClassRef, src, getTargetObjectId(), tgtScope);
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

 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return sourceObject.getSemanticModel();
 }

 @Override
 public ResolveScope getTargetResolveScope()
 {
  return tgtScope;
 }


 @Override
 public void setTargetResolveScope(ResolveScope scp)
 {
  tgtScope = scp;
 }
}

