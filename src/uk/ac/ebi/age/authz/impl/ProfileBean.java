package uk.ac.ebi.age.authz.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.authz.PermissionProfile;

public class ProfileBean implements PermissionProfile
{
 private String id;
 private String description;
 private Collection<PermissionBean> permissions = new HashSet<PermissionBean>();
 private Set<ProfileBean> profiles = new HashSet<ProfileBean>();

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

 public Collection<? extends Permission> getPermissions()
 {
  return permissions;
 }

 public void removePermission(Permission perm)
 {
  permissions.remove(perm);
 }
 
 public void addPermission(PermissionBean perm)
 {
  permissions.add(perm);
 }

 public void addProfile(ProfileBean npb)
 {
  profiles.add(npb);
 }
 
 public Collection<? extends PermissionProfile> getProfiles()
 {
  return profiles;
 }

 public void removeProfile(PermissionProfile p)
 {
  profiles.remove(p);
 }

 public boolean isPartOf(PermissionProfile pb)
 {
  if( pb.getProfiles() == null )
   return false;
  
  for( PermissionProfile gb : pb.getProfiles() )
  {
   if( equals(gb) )
    return true;
   
   if( isPartOf(gb) )
    return true;
  }
  
  return false;
 }
}
