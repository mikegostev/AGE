package uk.ac.ebi.age.authz.impl;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.mg.collection.IndexList;
import uk.ac.ebi.mg.collection.Named;

import com.pri.util.NaturalStringComparator;

public class GroupBean implements UserGroup, Named<String>, Serializable
{

 private static final long serialVersionUID = 1L;

 private String id;
 private String description;
 private IndexList<String,UserBean> users = new IndexList<String,UserBean>( NaturalStringComparator.getInstance() );
 private IndexList<String,GroupBean> groups = new IndexList<String,GroupBean>( NaturalStringComparator.getInstance() );

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

 public void addGroup(GroupBean g)
 {
  groups.add(g);
 }

 public Collection<UserBean> getUsers()
 {
  return users;
 }

 public void removeUser(UserBean ub)
 {
  users.remove(ub);
 }
 
 public void removeGroup(UserGroup gp)
 {
  groups.remove(gp);
 }

 public Collection< ? extends UserGroup> getGroups()
 {
  return groups;
 }

 public boolean isPartOf(UserGroup pb)
 {
  if( pb.getGroups() == null )
   return false;
  
  for( UserGroup gb : pb.getGroups() )
  {
   if( equals(gb) )
    return true;
   
   if( isPartOf(gb) )
    return true;
  }
  
  return false;
 }

 @Override
 public boolean isUserCompatible(User u)
 {
  for( User mu : users )
   if( u == mu )
    return true;
  
  for( GroupBean gb : groups )
   if( gb.isUserCompatible(u) )
    return true;
  
  return false;
 }

 public UserBean getUser(String userId)
 {
  return users.getByKey(userId);
 }

 public UserGroup getGroup(String partId)
 {
  return groups.getByKey(partId);
 }



}
