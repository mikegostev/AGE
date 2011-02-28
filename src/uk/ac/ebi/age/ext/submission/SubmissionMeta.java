package uk.ac.ebi.age.ext.submission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SubmissionMeta implements Serializable
{
 private static final long serialVersionUID = 1L;

 private String id;
 
// private String clusterId;
 
 private String description;
 private String submitter;
 private String modifier;
 
 private long submissionTime;
 private long modificationTime;
 
 private List<DataModuleMeta> mods = new ArrayList<DataModuleMeta>(3);
 private List<FileAttachmentMeta> atts ;
 
 public String getSubmitter()
 {
  return submitter;
 }

 public void setSubmitter(String submitter)
 {
  this.submitter = submitter;
 }

 public String getModifier()
 {
  return modifier;
 }

 public void setModifier(String modifier)
 {
  this.modifier = modifier;
 }

 public long getSubmissionTime()
 {
  return submissionTime;
 }

 public void setSubmissionTime(long submissionTime)
 {
  this.submissionTime = submissionTime;
 }

 public long getModificationTime()
 {
  return modificationTime;
 }

 public void setModificationTime(long modificationTime)
 {
  this.modificationTime = modificationTime;
 }


 public String getDescription()
 {
  return description;
 }

 public void setDescription(String string)
 {
  description = string;
 }

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }

 public void addDataModule(DataModuleMeta dmMeta)
 {
  mods.add(dmMeta);
 }

 public List<DataModuleMeta> getDataModules()
 {
  return mods;
 }

 public void addAttachment(FileAttachmentMeta fAtMeta)
 {
  if( atts == null )
   atts = new ArrayList<FileAttachmentMeta>(3);

  atts.add( fAtMeta );
 }

 public List<FileAttachmentMeta> getAttachments()
 {
  return atts;
 }

// public String getClusterId()
// {
//  return clusterId;
// }
//
// public void setClusterId(String clusterId)
// {
//  this.clusterId = clusterId;
// }
}
