package uk.ac.ebi.age.authz.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.age.authz.ACR;
import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.Tag;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class TagBean implements Tag, Serializable
{

 private static final long serialVersionUID = 1L;


 private String id;
 private String description;
 private Tag    parent;
 
 private Collection<ProfileForGroupACRBean> acrPf4G = new ArrayList<ProfileForGroupACRBean>();
 private Collection<ProfileForUserACRBean> acrPf4U = new ArrayList<ProfileForUserACRBean>();
 private Collection<PermissionForGroupACRBean> acrPm4G = new ArrayList<PermissionForGroupACRBean>();
 private Collection<PermissionForUserACRBean> acrPm4U = new ArrayList<PermissionForUserACRBean>();

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

 public Tag getParent()
 {
  return parent;
 }

 public void setParent(Tag parent)
 {
  this.parent = parent;
 }

 @Override
 public Collection<ProfileForGroupACRBean> getProfileForGroupACRs()
 {
  return acrPf4G;
 }

 @Override
 public Collection<ProfileForUserACRBean> getProfileForUserACRs()
 {
  return acrPf4U;
 }

 @Override
 public Collection<PermissionForUserACRBean> getPermissionForUserACRs()
 {
  return acrPm4U;
 }

 @Override
 public Collection<PermissionForGroupACRBean> getPermissionForGroupACRs()
 {
  return acrPm4G;
 }

 public void addProfileForGroupACR(ProfileForGroupACRBean acr)
 {
  acrPf4G.add(acr);
 }

 public void addProfileForUserACR(ProfileForUserACRBean acr)
 {
  acrPf4U.add(acr);
 }

 public void addPermissionForUserACR(PermissionForUserACRBean acr)
 {
  acrPm4U.add(acr);
 }

 public void addPermissionForGroupACR(PermissionForGroupACRBean acr)
 {
  acrPm4G.add(acr);
 }

 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  boolean allow = false;
  
  for( ProfileForGroupACRBean b : acrPf4G )
  {
   Permit p = b.checkPermission(act, user);
   if( p == Permit.DENY )
    return Permit.DENY;
   else if( p == Permit.ALLOW )
    allow = true;
  }
  
  for( ACR b : acrPf4U )
  {
   Permit p = b.checkPermission(act, user);
   if( p == Permit.DENY )
    return Permit.DENY;
   else if( p == Permit.ALLOW )
    allow = true;
  }

  for( ACR b : acrPm4U )
  {
   Permit p = b.checkPermission(act, user);
   if( p == Permit.DENY )
    return Permit.DENY;
   else if( p == Permit.ALLOW )
    allow = true;
  }

  for( ACR b : acrPm4G )
  {
   Permit p = b.checkPermission(act, user);
   if( p == Permit.DENY )
    return Permit.DENY;
   else if( p == Permit.ALLOW )
    allow = true;
  }
  
  return allow?Permit.ALLOW:Permit.UNDEFINED;
 }

}
