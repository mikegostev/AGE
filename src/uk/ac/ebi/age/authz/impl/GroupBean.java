package uk.ac.ebi.age.authz.impl;

import uk.ac.ebi.age.authz.UserGroup;

public class GroupBean implements UserGroup
{
 private String id;
 private String description;

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



}
