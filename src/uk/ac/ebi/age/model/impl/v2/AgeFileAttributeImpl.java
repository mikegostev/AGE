package uk.ac.ebi.age.model.impl.v2;

import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;

public class AgeFileAttributeImpl extends AgeStringAttributeImpl implements AgeFileAttributeWritable
{
 private static final long serialVersionUID = 2L;

 private transient String fileId;

 public AgeFileAttributeImpl(AttributeClassRef attrClass, SemanticModel sm)
 {
  super(attrClass, sm);
 }

 @Override
 public String getFileReference()
 {
  return (String)super.getValue();
 }

 @Override
 public String getFileID()
 {
  return fileId;
 }

 @Override
 public void setFileReference(String fRef)
 {
  super.setValue(fRef);
 }

 @Override
 public void setFileId(String fId)
 {
  fileId = fId;
 }

}
