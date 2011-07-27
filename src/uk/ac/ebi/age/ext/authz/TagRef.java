package uk.ac.ebi.age.ext.authz;

import java.io.Serializable;

public class TagRef implements Serializable
{

 private static final long serialVersionUID = 1L;

 private String classiferName;
 private String tagName;
 private String tagValue;
 
 public TagRef()
 {}

 public TagRef(String classiferName, String tagName)
 {
  this.classiferName = classiferName;
  this.tagName = tagName;
 }

 public String getClassiferName()
 {
  return classiferName;
 }
 
 public void setClassiferName(String classiferName)
 {
  this.classiferName = classiferName;
 }
 
 public String getTagName()
 {
  return tagName;
 }
 
 public void setTagName(String tagName)
 {
  this.tagName = tagName;
 }

 public String getTagValue()
 {
  return tagValue;
 }

 public void setTagValue(String tagValue)
 {
  this.tagValue = tagValue;
 }
}
