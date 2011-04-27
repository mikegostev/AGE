package uk.ac.ebi.age.ext.submission;

import java.io.Serializable;


public class FileAttachmentMeta implements Serializable
{
 private static final long serialVersionUID = 1L;

 private String id;
// private String originalId;
 private String description;
 private boolean global;
 private transient Object aux;

 private long submissionTime;
 private long modificationTime;
 
 private String submitter;
 private String modifier;

 private String systemId;

 private long fileVersion;
 
 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }

 public String getDescription()
 {
  return description;
 }

 public void setDescription(String description)
 {
  this.description = description;
 }

 public void setGlobal(boolean b)
 {
  global=b;
 }

 public boolean isGlobal()
 {
  return global;
 }

// public String getOriginalId()
// {
//  return originalId;
// }
//
// public void setOriginalId(String originalId)
// {
//  this.originalId = originalId;
// }

 public Object getAux()
 {
  return aux;
 }

 public void setAux(Object aux)
 {
  this.aux = aux;
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

 public void setSystemId(String gid)
 {
  systemId = gid;
 }

 public String getSystemId()
 {
  return systemId;
 }

 public long getFileVersion()
 {
  return fileVersion;
 }

 public void setFileVersion(long fileVersion)
 {
  this.fileVersion = fileVersion;
 }


}
