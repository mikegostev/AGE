package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.impl.v3.AgeExternalObjectAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.storage.impl.serswap.SerializedSwapStorage;

class SwapExternalObjectAttribute extends AgeExternalObjectAttributeImpl
{

 private static final long serialVersionUID = 3L;


 public SwapExternalObjectAttribute(AttributeClassRef atCls, String id, AttributedWritable host, boolean glb)
 {
  super(atCls, id, host, glb);
 }

 @Override
 public AgeObjectProxy getHostObject()
 {
  AttributedWritable host = super.getHostObject();
  
  if( host instanceof AgeObjectProxy)
   return (AgeObjectProxy)super.getHostObject();
  
  AgeObjectProxy pxo = ((SwapDataModuleImpl)((AgeObject)host).getDataModule()).getModuleRef().getObjectProxy( host.getId() );
  
  setHostObject(pxo);
  
  return pxo;
 }

 
 @Override
 public AgeObject getValue()
 {
  AgeObject val = super.getValue();
  
  if( val != null )
   return val;
  
  SerializedSwapStorage stor = getHostObject().getStorage();
  
  AgeObjectWritable tgt = null;
  
  if( isTargetGlobal() )
   tgt = stor.getGlobalObject( getTargetObjectId() );
  else
   tgt = stor.getClusterObject(getHostObject().getModuleKey().getClusterId(), getTargetObjectId());
  
  setTargetObject(tgt);

  return tgt;
 }


 @Override
 public AgeExternalObjectAttributeWritable createClone( AttributedWritable host )
 {
  AgeExternalObjectAttributeImpl clone  = new SwapExternalObjectAttribute(getClassRef(), getTargetObjectId(), host, isTargetGlobal());
  
  
  cloneAttributes( clone );

  return clone;
 }

}
