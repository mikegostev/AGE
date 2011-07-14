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
 
 private Collection<ProfileForGroupACRBean> acrPf4G = null;
 private Collection<ProfileForUserACRBean> acrPf4U = null;
 private Collection<PermissionForGroupACRBean> acrPm4G = null;
 private Collection<PermissionForUserACRBean> acrPm4U = null;

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
 public boolean hasAccessRules()
 {
  return   ( acrPf4G != null && acrPf4G.size() > 0 )
        || ( acrPf4U != null && acrPf4U.size() > 0 )
        || ( acrPm4U != null && acrPm4U.size() > 0 )
        || ( acrPm4G != null && acrPm4G.size() > 0 );
 }
 
 @Override
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
  if(acrPf4G == null)
   acrPf4G = new ArrayList<ProfileForGroupACRBean>();
 
  acrPf4G.add(acr);
 }

 public void addProfileForUserACR(ProfileForUserACRBean acr)
 {
  if(acrPf4U == null)
   acrPf4U = new ArrayList<ProfileForUserACRBean>();
  
  acrPf4U.add(acr);
 }

 public void addPermissionForUserACR(PermissionForUserACRBean acr)
 {
  if(acrPm4U == null)
   acrPm4U = new ArrayList<PermissionForUserACRBean>();
  
  acrPm4U.add(acr);
 }

 public void addPermissionForGroupACR(PermissionForGroupACRBean acr)
 {
  if(acrPm4G == null)
   acrPm4G = new ArrayList<PermissionForGroupACRBean>();
  
  acrPm4G.add(acr);
 }

 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  boolean allow = false;

  if(acrPf4G != null)
  {
   for(ProfileForGroupACRBean b : acrPf4G)
   {
    Permit p = b.checkPermission(act, user);
    if(p == Permit.DENY)
     return Permit.DENY;
    else if(p == Permit.ALLOW)
     allow = true;
   }
  }

  if(acrPf4U != null)
  {
   for(ACR b : acrPf4U)
   {
    Permit p = b.checkPermission(act, user);
    if(p == Permit.DENY)
     return Permit.DENY;
    else if(p == Permit.ALLOW)
     allow = true;
   }
  }

  if(acrPm4U != null)
  {
   for(ACR b : acrPm4U)
   {
    Permit p = b.checkPermission(act, user);
    if(p == Permit.DENY)
     return Permit.DENY;
    else if(p == Permit.ALLOW)
     allow = true;
   }
  }

  if(acrPm4G != null)
  {
   for(ACR b : acrPm4G)
   {
    Permit p = b.checkPermission(act, user);
    if(p == Permit.DENY)
     return Permit.DENY;
    else if(p == Permit.ALLOW)
     allow = true;
   }
  }

  return allow ? Permit.ALLOW : Permit.UNDEFINED;
 }

}
