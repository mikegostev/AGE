package uk.ac.ebi.age.authz.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.authz.UserGroup;

public class GroupBean implements UserGroup
{
 private String id;
 private String description;
 private Set<UserBean> users = new HashSet<UserBean>();
 private Set<GroupBean> groups = new HashSet<GroupBean>();

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

 public void addUser(UserBean u)
 {
  users.add(u);
 }

 public Collection<UserBean> getUsers()
 {
  return users;
 }

 public void removeUser(UserBean ub)
 {
  users.remove(ub);
 }

 public Collection< ? extends UserGroup> getGroups()
 {
  return groups;
 }

}
