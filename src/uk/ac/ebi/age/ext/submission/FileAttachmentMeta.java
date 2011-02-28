package uk.ac.ebi.age.ext.submission;

import java.io.Serializable;


public class FileAttachmentMeta implements Serializable
{
 private static final long serialVersionUID = 470378715679227484L;

 private String id;
 private String originalId;
 private String description;
 private boolean global;
 private transient Object aux;

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

 public String getOriginalId()
 {
  return originalId;
 }

 public void setOriginalId(String originalId)
 {
  this.originalId = originalId;
 }

 public Object getAux()
 {
  return aux;
 }

 public void setAux(Object aux)
 {
  this.aux = aux;
 }
}
