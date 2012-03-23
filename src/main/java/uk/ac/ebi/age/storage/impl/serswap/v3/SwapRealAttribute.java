package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.impl.v3.AgeRealAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class SwapRealAttribute extends AgeRealAttributeImpl
{

 private static final long serialVersionUID = 3L;

 protected SwapRealAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  super(attrClass, host);
 }

 @Override
 public AttributedWritable getHostObject()
 {
  AttributedWritable host = super.getHostObject();
  
  if( host instanceof AgeObjectProxy)
   return super.getHostObject();
  
  AgeObjectProxy pxo = ((SwapDataModule)((AgeObject)host).getDataModule()).getModuleRef().getObjectProxy( host.getId() );
  
  setHostObject(pxo);
  
  return pxo;
 }
 
 @Override
 public AgeAttributeWritable createClone( AttributedWritable host )
 {
  SwapRealAttribute clone  = new SwapRealAttribute(getClassRef(), host);
  
  clone.setDoubleValue(getValueAsDouble());
  
  cloneAttributes( clone );

  return clone;
 }
}
