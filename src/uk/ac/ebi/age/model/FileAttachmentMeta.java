package uk.ac.ebi.age.model;

import java.io.File;

public class FileAttachmentMeta
{
 private String id;
 private String description;
 private File   file;

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
}
