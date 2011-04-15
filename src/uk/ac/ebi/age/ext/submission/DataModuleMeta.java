package uk.ac.ebi.age.ext.submission;

import java.io.Serializable;

public class DataModuleMeta implements Serializable
{
 private static final long serialVersionUID = 1L;

 private String id;
 private String description;
 private long ctime;
 private long mtime;
 private String submitter;
 private String modifier;
 
 private transient String text;
 private transient Object aux;
 
 private Status status;

 public void setId(String stringId)
 {
  id = stringId;
 }

 public String getDescription()
 {
  return description;
 }

 public void setDescription(String description)
 {
  this.description = description;
 }

 public String getId()
 {
  return id;
 }

 public void setModificationTime(long time)
 {
  mtime = time;
 }
 
 public long getModificationTime()
 {
  return mtime;
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

 public String getText()
 {
  return text;
 }

 public void setText(String text)
 {
  this.text = text;
 }


 public void setAux(Object aux)
 {
  this.aux = aux;
 }

 public Object getAux()
 {
  return aux;
 }

 public void setSubmissionTime(long time)
 {
  ctime = time;
 }
 
 public long getSubmissionTime()
 {
  return ctime;
 }
 
 public Status getStatus()
 {
  return status;
 }

 public void setStatus(Status blkSts)
 {
  status = blkSts;
 }

}
