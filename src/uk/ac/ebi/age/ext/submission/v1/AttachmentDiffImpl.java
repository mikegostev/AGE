package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;

import uk.ac.ebi.age.ext.submission.AttachmentDiff;
import uk.ac.ebi.age.ext.submission.Status;

public class AttachmentDiffImpl implements Serializable, AttachmentDiff
{

 private static final long serialVersionUID = 1L;

 private String  id;
 private Status  status;

 private boolean metaChanged;
 private boolean dataChanged;
 private boolean visibilityChanged;

 AttachmentDiffImpl()
 {}
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#getId()
  */
 @Override
 public String getId()
 {
  return id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setId(java.lang.String)
  */
 @Override
 public void setId(String id)
 {
  this.id = id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#getStatus()
  */
 @Override
 public Status getStatus()
 {
  return status;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setStatus(uk.ac.ebi.age.ext.submission.Status)
  */
 @Override
 public void setStatus(Status status)
 {
  this.status = status;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#isMetaChanged()
  */
 @Override
 public boolean isMetaChanged()
 {
  return metaChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setMetaChanged(boolean)
  */
 @Override
 public void setMetaChanged(boolean metaChanged)
 {
  this.metaChanged = metaChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#isDataChanged()
  */
 @Override
 public boolean isDataChanged()
 {
  return dataChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setDataChanged(boolean)
  */
 @Override
 public void setDataChanged(boolean dataChanged)
 {
  this.dataChanged = dataChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#isVisibilityChanged()
  */
 @Override
 public boolean isVisibilityChanged()
 {
  return visibilityChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.AttachmentDiff#setVisibilityChanged(boolean)
  */
 @Override
 public void setVisibilityChanged(boolean visibilityChanged)
 {
  this.visibilityChanged = visibilityChanged;
 }

}
