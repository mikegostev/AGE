package uk.ac.ebi.age.model;

import java.io.Serializable;

public class DataModuleMeta implements Serializable
{
 private static final long serialVersionUID = 1L;

 private String id;
 private String description;
 private long mtime;
 private String modifier;
 
 private transient String text;

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

}
