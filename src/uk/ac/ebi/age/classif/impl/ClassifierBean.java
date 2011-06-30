package uk.ac.ebi.age.classif.impl;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.classif.Classifier;

public class ClassifierBean implements Classifier
{
 private String id;
 private String description;
 private Collection<TagBean> tags = new ArrayList<TagBean>();
 
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

 public Collection<TagBean> getTags()
 {
  return tags;
 }

 @Override
 public TagBean getTag(String tagId)
 {
  for(TagBean t : tags)
  {
   if( t.getId().equals(tagId))
    return t;
  }
  
  return null;
 }

 public void addTag(TagBean tb)
 {
  tags.add(tb);
 }


}
