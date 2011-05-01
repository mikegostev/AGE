package uk.ac.ebi.age.ext.submission.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.age.ext.submission.AttachmentDiff;
import uk.ac.ebi.age.ext.submission.DataModuleDiff;
import uk.ac.ebi.age.ext.submission.SubmissionDiff;

public class SubmissionDiffImpl implements Serializable, SubmissionDiff
{
 private static final long serialVersionUID = 1L;

 private long modificationTime;
 private String modifier;
 private String id;
 
 private boolean descriptionChanged;
 
 private List<DataModuleDiff> moduleDiffs;
 private List<AttachmentDiff> attachmentDiffs;
 
 
 SubmissionDiffImpl()
 {}
 
 @Override
 public void setId(String id2)
 {
  id = id2;
 }

 @Override
 public String getId()
 {
  return id;
 }

 @Override
 public long getModificationTime()
 {
  return modificationTime;
 }

 @Override
 public void setModificationTime(long modificationTime)
 {
  this.modificationTime = modificationTime;
 }

 @Override
 public String getModifier()
 {
  return modifier;
 }

 @Override
 public void setModifier(String modifier)
 {
  this.modifier = modifier;
 }

 @Override
 public boolean isDescriptionChanged()
 {
  return descriptionChanged;
 }

 @Override
 public void setDescriptionChanged(boolean descriptionChanged)
 {
  this.descriptionChanged = descriptionChanged;
 }

 @Override
 public void addDataModuleDiff(DataModuleDiff mdif)
 {
  if( moduleDiffs == null )
   moduleDiffs = new ArrayList<DataModuleDiff>();
  
  moduleDiffs.add(mdif);
 }
 
 @Override
 public void addAttachmentDiff(AttachmentDiff adif)
 {
  if( attachmentDiffs == null )
   attachmentDiffs = new ArrayList<AttachmentDiff>();
  
  attachmentDiffs.add(adif);
 }

}
