package uk.ac.ebi.age.model.impl.v4;

import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.impl.v3.AgeStringAttributeImpl;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;

public class AgeFileAttributeImpl extends AgeStringAttributeImpl implements AgeFileAttributeWritable
{
 private static final long serialVersionUID = 4L;

 private transient String fileSysRef;
 private ResolveScope scope;

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
 public String getFileSysRef()
 {
  return fileSysRef;
 }

 @Override
 public void setFileSysRef(String fId)
 {
  fileSysRef = fId;
 }

 @Override
 public ResolveScope getTargetResolveScope()
 {
  return scope;
 }

}
