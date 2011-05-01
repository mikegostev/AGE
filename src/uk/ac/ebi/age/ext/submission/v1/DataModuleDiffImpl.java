package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;

import uk.ac.ebi.age.ext.submission.DataModuleDiff;
import uk.ac.ebi.age.ext.submission.Status;

public class DataModuleDiffImpl implements Serializable, DataModuleDiff
{

 private static final long serialVersionUID = 1L;

 private String id;
 private Status status;
 
 private boolean metaChanged;
 private boolean dataChanged;
 
 
 DataModuleDiffImpl()
 {}
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#getId()
  */
 @Override
 public String getId()
 {
  return id;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#setId(java.lang.String)
  */
 @Override
 public void setId(String id)
 {
  this.id = id;
 }
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#getStatus()
  */
 @Override
 public Status getStatus()
 {
  return status;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#setStatus(uk.ac.ebi.age.ext.submission.Status)
  */
 @Override
 public void setStatus(Status status)
 {
  this.status = status;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#isMetaChanged()
  */
 @Override
 public boolean isMetaChanged()
 {
  return metaChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#setMetaChanged(boolean)
  */
 @Override
 public void setMetaChanged(boolean metaChanged)
 {
  this.metaChanged = metaChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#isDataChanged()
  */
 @Override
 public boolean isDataChanged()
 {
  return dataChanged;
 }

 /* (non-Javadoc)
  * @see uk.ac.ebi.age.ext.submission.v1.DataModuleDiff#setDataChanged(boolean)
  */
 @Override
 public void setDataChanged(boolean dataChanged)
 {
  this.dataChanged = dataChanged;
 }
}
