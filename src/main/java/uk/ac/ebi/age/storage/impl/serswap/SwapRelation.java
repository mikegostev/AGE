package uk.ac.ebi.age.storage.impl.serswap;

import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.impl.v3.AttributedObject;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

public class SwapRelation extends AttributedObject implements AgeRelationWritable
{

 private static final long serialVersionUID = 1L;

 private RelationClassRef relClassRef;
 private AgeObjectWritable target;
 private AgeRelationWritable invRelation;
 private boolean inferred=false;
 
 public SwapRelation(RelationClassRef cref, AgeObjectWritable targetObj)
 {
  relClassRef= cref;
  target=targetObj;
 }

 @Override
 public AgeRelationClass getAgeElClass()
 {
  return relClassRef.getAgeRelationClass();
 }

 @Override
 public AgeObjectWritable getTargetObject()
 {
  if( target instanceof AgeObjectProxy )
   return target;
  
  AgeObjectProxy pxObj = ((SwapDataModule)target.getDataModule()).getModuleRef().getObjectProxy(target.getId());
  
  target = pxObj;
  
  return pxObj;
 }

 @Override
 public AgeObjectWritable getSourceObject()
 {
  return invRelation.getTargetObject();
 }

 @Override
 public int getOrder()
 {
  return relClassRef.getOrder();
 }

 @Override
 public void setInferred( boolean inf )
 {
  inferred = inf;
 }

 
 @Override
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

 @Override
 public AgeRelationWritable createClone( AgeObjectWritable src )
 {
  SwapRelation clone = new SwapRelation(relClassRef, getTargetObject());
  
  cloneAttributes(clone);
  clone.setInferred( isInferred() );
  
  return clone;
 }

 @Override
 public AgeRelationWritable getInverseRelation()
 {
  return invRelation;
 }

 @Override
 public void setInverseRelation(AgeRelationWritable invRl)
 {
  invRelation = invRl;
 }

 @Override
 public ContextSemanticModel getSemanticModel()
 {
  return getSourceObject().getSemanticModel();
 }
}
