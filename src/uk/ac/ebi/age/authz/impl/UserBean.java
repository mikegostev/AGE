package uk.ac.ebi.age.authz.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.mg.collection.Named;

public class UserBean implements User, Named<String>, Serializable
{

 private static final long serialVersionUID = 1L;

 private String id;
 private String name;
 private String pass;
 private Set<GroupBean> groups = new HashSet<GroupBean>();

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

 public Collection<GroupBean> getGroups()
 {
  return groups;
 }

 public void addGroup( GroupBean grp )
 {
  groups.add(grp);
 }

 public void removeGroup(GroupBean gb)
 {
  groups.remove(gb);
 }

 @Override
 public boolean isUserCompatible(User u)
 {
  return u == this;
 }
 

}
