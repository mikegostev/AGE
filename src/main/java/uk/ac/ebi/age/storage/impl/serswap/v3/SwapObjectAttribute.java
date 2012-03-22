package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.impl.v3.AgeObjectAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class SwapObjectAttribute extends AgeObjectAttributeImpl
{

 private static final long serialVersionUID = 3L;

 protected SwapObjectAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  super(attrClass, host);
 }
 
 @Override
 public AgeObjectWritable getValue()
 {
  AgeObjectWritable obj = super.getValue();
  
  if( obj instanceof AgeObjectProxy )
   return obj;
  
  AgeObjectProxy pxObj = ((SwapDataModule)obj.getDataModule()).getModuleRef().getObjectProxy(obj.getId());
  
  setValue( pxObj );
  
  return pxObj;
 }
 
 @Override
 public AttributedWritable getHostObject()
 {
  AttributedWritable host = super.getHostObject();
  
  if( host instanceof AgeObjectProxy)
   return host;
  
  AgeObjectProxy pxo = ((SwapDataModule)((AgeObject)host).getDataModule()).getModuleRef().getObjectProxy( host.getId() );
  
  setHostObject(pxo);
  
  return pxo;
 }
}
