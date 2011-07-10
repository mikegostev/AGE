package uk.ac.ebi.age.authz.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.authz.PermissionProfile;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.mg.collection.Named;

public class ProfileBean implements PermissionProfile, Named<String>, Serializable
{

 private static final long serialVersionUID = 1L;


 private String id;
 private String description;
 private Collection<PermissionBean> permissions = new HashSet<PermissionBean>();
 private Set<ProfileBean> profiles = new HashSet<ProfileBean>();

 @Override
 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }

 @Override
 public String getDescription()
 {
  return description;
 }

 public void setDescription(String description)
 {
  this.description = description;
 }

 @Override
 public Collection<PermissionBean> getPermissions()
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

 @Override
 public Permit checkPermission( SystemAction act )
 {
  boolean allw = false;
  
  for( Permission p : permissions )
  {
   if( act == p.getAction() )
   {
    if( p.isAllow() )
     allw = true;
    else
     return Permit.DENY;
   }
  }
   
  for( ProfileBean pp : profiles )
  {
   Permit r = pp.checkPermission(act);
   
   if( r == Permit.DENY )
    return Permit.DENY;
   else if( r == Permit.ALLOW )
    allw = true;
  }
  
  return allw?Permit.ALLOW:Permit.UNDEFINED;
  
 }

}
