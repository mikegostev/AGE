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
 private long version;
 
 private boolean forUpdate;
 
 private transient String text;
 private transient Object aux;

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

 public void setVersion(long v)
 {
  version=v;
 }

 public long getVersion()
 {
  return version;
 }

 public boolean isForUpdate()
 {
  return forUpdate;
 }

 public void setForUpdate(boolean forUpdate)
 {
  this.forUpdate = forUpdate;
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

}
