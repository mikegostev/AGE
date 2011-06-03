package uk.ac.ebi.age.authz.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;

public class UserBean implements User
{
 private String id;
 private String name;
 private String pass;
 private Set<UserGroup> groups = new HashSet<UserGroup>();

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

 public String getPass()
 {
  return pass;
 }

 public void setPass(String pass)
 {
  this.pass = pass;
 }

 public Collection< ? extends UserGroup> getGroups()
 {
  return groups;
 }

 public void addGroup( UserGroup grp )
 {
  groups.add(grp);
 }

 public void removeGroup(GroupBean gb)
 {
  groups.remove(gb);
 }
 

}
