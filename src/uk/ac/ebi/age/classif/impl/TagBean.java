package uk.ac.ebi.age.classif.impl;

import uk.ac.ebi.age.classif.Tag;

public class TagBean implements Tag
{
 private String id;
 private String description;
 private Tag    parent;

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

 public Tag getParent()
 {
  return parent;
 }

 public void setParent(Tag parent)
 {
  this.parent = parent;
 }

}
