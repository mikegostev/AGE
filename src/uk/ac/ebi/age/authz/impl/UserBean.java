package uk.ac.ebi.age.authz.impl;

import uk.ac.ebi.age.authz.User;

public class UserBean implements User
{
 private String id;
 private String name;

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }

 public String getName()
 {
  return name;
 }

 public void setName(String name)
 {
  this.name = name;
 }

}
