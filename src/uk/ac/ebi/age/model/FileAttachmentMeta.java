package uk.ac.ebi.age.model;

import java.io.File;

public class FileAttachmentMeta
{
 private String id;
 private String originalId;
 private String description;
 private File   file;
 private boolean global;

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

 public File getFile()
 {
  return file;
 }

 public void setFile(File file)
 {
  this.file = file;
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
}
