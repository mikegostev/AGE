package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.impl.v3.AgeStringAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class SwapStringAttribute extends AgeStringAttributeImpl
{

 private static final long serialVersionUID = 3L;

 protected SwapStringAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  super(attrClass, host);
 }
 
 @Override
 public AgeObjectProxy getAttributedHost()
 {
  AttributedWritable host = super.getAttributedHost();
  
  if( host instanceof AgeObjectProxy)
   return (AgeObjectProxy)host;
  
  AgeObjectProxy pxo = ((SwapDataModuleImpl)((AgeObject)host).getDataModule()).getModuleRef().getObjectProxy( host.getId() );
  
  setAttributedHost(pxo);
  
  return pxo;
 }
 
 @Override
 public AgeObjectProxy getMasterObject()
 {
  AgeObjectWritable host = super.getMasterObject();
  
  if( host instanceof AgeObjectProxy)
   return (AgeObjectProxy)host;
  
  AgeObjectProxy pxo = ((SwapDataModuleImpl)((AgeObject)host).getDataModule()).getModuleRef().getObjectProxy( host.getId() );
  
  setAttributedHost(pxo);
  
  return pxo;
 }
 
 @Override
 public AgeAttributeWritable createClone( AttributedWritable host )
 {
  SwapStringAttribute clone  = new SwapStringAttribute(getClassRef(), host);
  
  clone.setValue(getValue());
  
  cloneAttributes( clone );

  return clone;
 }
}
