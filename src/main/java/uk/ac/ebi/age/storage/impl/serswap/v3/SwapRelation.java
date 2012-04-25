package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.impl.v3.AgeRelationImpl;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;

public class SwapRelation extends AgeRelationImpl
{

 private static final long serialVersionUID = 3L;


 protected SwapRelation(RelationClassRef cref, AgeObjectWritable targetObj)
 {
  super(cref, targetObj);
 }


 @Override
 public AgeObjectWritable getSourceObject()
 {
  AgeObjectWritable target = super.getSourceObject();
  
  if( target instanceof AgeObjectProxy )
   return target;
  
  AgeObjectProxy pxObj = ((SwapDataModuleImpl)target.getDataModule()).getModuleRef().getObjectProxy(target.getId());
  
  target = pxObj;
  
  return pxObj;
 }
 
 @Override
 public AgeObjectWritable getTargetObject()
 {
  AgeObjectWritable target = super.getTargetObject();
  
  if( target instanceof AgeObjectProxy )
   return target;
  
  AgeObjectProxy pxObj = ((SwapDataModuleImpl)target.getDataModule()).getModuleRef().getObjectProxy(target.getId());
  
  target = pxObj;
  
  return pxObj;
 }

}
