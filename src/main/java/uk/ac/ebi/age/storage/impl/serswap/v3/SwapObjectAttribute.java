package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.impl.v3.AgeObjectAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class SwapObjectAttribute extends AgeObjectAttributeImpl
{

 private static final long serialVersionUID = 3L;

 public SwapObjectAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  super(attrClass, host);
 }
 
 @Override
 public AgeObjectWritable getValue()
 {
  AgeObjectWritable obj = super.getValue();
  
  if( obj.getClass() == AgeObjectProxy.class )
   return obj;
  
  AgeObjectProxy pxObj = ((SwapDataModule)obj.getDataModule()).getModuleRef().getObjectProxy(obj.getId());
  
  setValue( pxObj );
  
  return pxObj;
 }
}
