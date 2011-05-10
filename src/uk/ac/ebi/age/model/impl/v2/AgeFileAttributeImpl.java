package uk.ac.ebi.age.model.impl.v2;

import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;

public class AgeFileAttributeImpl extends AgeStringAttributeImpl implements AgeFileAttributeWritable
{
 private static final long serialVersionUID = 2L;

 private transient String fileSysRef;

 public AgeFileAttributeImpl(AttributeClassRef attrClass, SemanticModel sm)
 {
  super(attrClass, sm);
 }

 @Override
 public String getFileId()
 {
  return (String)super.getValue();
 }

 @Override
 public String getFileSysRef()
 {
  return fileSysRef;
 }

 @Override
 public void setFileId(String fRef)
 {
  super.setValue(fRef);
 }

 @Override
 public void setFileSysRef(String fId)
 {
  fileSysRef = fId;
 }

}
