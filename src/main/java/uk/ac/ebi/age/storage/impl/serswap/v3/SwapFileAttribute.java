package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.impl.v4.AgeFileAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class SwapFileAttribute extends AgeFileAttributeImpl
{
 private static final long serialVersionUID = 3L;

 public SwapFileAttribute(AttributeClassRef attrClass, AttributedWritable host, ResolveScope scope)
 {
  super(attrClass, host, scope);
 }

 @Override
 public AttributedWritable getAttributedHost()
 {
  AttributedWritable host = super.getAttributedHost();
  
  if( host instanceof AgeObjectProxy)
   return super.getAttributedHost();
  
  AgeObjectProxy pxo = ((SwapDataModuleImpl)((AgeObject)host).getDataModule()).getModuleRef().getObjectProxy( host.getId() );
  
  setAttributedHost(pxo);
  
  return pxo;
 }
 
 @Override
 public AgeAttributeWritable createClone( AttributedWritable host )
 {
  SwapFileAttribute clone  = new SwapFileAttribute(getClassRef(), host, getTargetResolveScope());
  
  clone.setFileId( getFileId() );
  clone.setResolvedGlobal(isResolvedGlobal());
  
  cloneAttributes( clone );

  return clone;
 }
}
