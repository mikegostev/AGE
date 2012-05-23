package uk.ac.ebi.age.model.impl.v3;

import java.io.File;

import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class AgeFileAttributeImpl extends AgeStringAttributeImpl implements AgeFileAttributeWritable
{
 private static final long serialVersionUID = 3L;

 public AgeFileAttributeImpl(AttributeClassRef attrClass, AttributedWritable host)
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
  return ResolveScope.CASCADE_CLUSTER;
 }


 @Override
 public boolean isResolvedGlobal()
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public File getFile()
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setResolvedGlobal(boolean glb)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public void setTargetResolveScope(ResolveScope rs)
 {
  throw new UnsupportedOperationException();
 }

}
