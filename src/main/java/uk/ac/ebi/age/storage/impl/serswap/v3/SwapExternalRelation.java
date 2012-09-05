package uk.ac.ebi.age.storage.impl.serswap.v3;

import java.lang.ref.SoftReference;

import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.impl.v3.AgeExternalRelationImpl;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.storage.impl.serswap.SerializedSwapStorage;

public class SwapExternalRelation extends AgeExternalRelationImpl
{
 private static final long serialVersionUID = 3L;

 private SoftReference<AgeExternalRelationWritable> softInvRel;
 
 protected SwapExternalRelation(RelationClassRef cRef, AgeObjectWritable srcOb, String id, ResolveScope scp )
 {
  super(cRef, srcOb, id, scp);
 }

 @Override
 public AgeObjectProxy getSourceObject()
 {
  AgeObjectWritable src = super.getSourceObject();
  
  if( src instanceof AgeObjectProxy )
   return (AgeObjectProxy)src;
  
  AgeObjectProxy sObjPx = ((SwapDataModuleImpl)src.getDataModule()).getModuleRef().getObjectProxy(src.getId());
  
  super.setSourceObject(sObjPx);
  
  return sObjPx;
 }

 
 @Override
 public AgeObjectWritable getTargetObject()
 {
  AgeObjectWritable tgObj =  super.getTargetObject();
  
  if( tgObj != null )
   return tgObj;
  
  SerializedSwapStorage stor = getSourceObject().getStorage();
  
 
  if( getTargetResolveScope() == ResolveScope.GLOBAL  )
   tgObj = stor.getGlobalObject( getTargetObjectId() );
  else
  {
   tgObj = stor.getClusterObject(getSourceObject().getModuleKey().getClusterId(), getTargetObjectId());
   
   if( tgObj == null && getTargetResolveScope() == ResolveScope.CASCADE_CLUSTER )
    tgObj = stor.getGlobalObject( getTargetObjectId() );
    
  }
  
  setTargetObject(tgObj);

  return tgObj;
 }
 
 @Override
 public AgeExternalRelationWritable getInverseRelation()
 {
  if( super.getInverseRelation() != null )
   return super.getInverseRelation();
  
  
  if( softInvRel != null )
  {
   AgeExternalRelationWritable invRel = softInvRel.get();
   
   if(  invRel != null )
    return invRel;
  }
  
  for( AgeRelationWritable rel : getTargetObject().getRelations() )
  {
   if( rel instanceof AgeExternalRelationWritable && rel.getTargetObject() == getSourceObject() && rel.getAgeElClass().equals( getAgeElClass().getInverseRelationClass() ) )
   {
    if( rel instanceof SwapImplicitInvExtRelation )
     super.setInverseRelation( rel );
    else
     softInvRel = new SoftReference<AgeExternalRelationWritable>( (AgeExternalRelationWritable)rel );
    
    return (AgeExternalRelationWritable)rel;
   }
  }
  
  return null;
 }
 
}
