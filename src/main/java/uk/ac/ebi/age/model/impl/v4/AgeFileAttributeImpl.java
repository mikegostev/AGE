package uk.ac.ebi.age.model.impl.v4;

import java.io.File;

import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.impl.v3.AgeStringAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.storage.AgeStorage;

public class AgeFileAttributeImpl extends AgeStringAttributeImpl implements AgeFileAttributeWritable
{
 private static final long serialVersionUID = 4L;

// private transient String fileSysRef;
 private ResolveScope scope;
 private boolean resolvedScope;

 public AgeFileAttributeImpl(AttributeClassRef attrClass, AttributedWritable host, ResolveScope scope)
 {
  super(attrClass, host);
 }

 @Override
 public String getFileId()
 {
  return (String)super.getValue();
 }


 @Override
 public void setFileId(String fRef)
 {
  super.setValue(fRef);
 }


 @Override
 public ResolveScope getTargetResolveScope()
 {
  return scope;
 }

 @Override
 public void setTargetResolveScope( ResolveScope rs )
 {
  scope=rs;
 }
 
 @Override
 public boolean isResolvedGlobal()
 {
  return resolvedScope;
 }

 @Override
 public void setResolvedGlobal(boolean resolvedScope)
 {
  this.resolvedScope = resolvedScope;
 }

 @Override
 public File getFile()
 {
  DataModule dm = getMasterObject().getDataModule();
  
  AgeStorage stor = dm.getStorage();
  
  if( isResolvedGlobal() )
   return stor.getAttachment( (String)getValue() );

  return stor.getAttachment((String)getValue(), dm.getClusterId() );
 }

}
