package uk.ac.ebi.age.authz.impl;

import uk.ac.ebi.age.authz.ProfileForGroupACR;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class ProfileForGroupACRBean implements ProfileForGroupACR
{
 private GroupBean group; 
 private ProfileBean profile; 
 
 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  if( ! group.isUserCompatible(user) )
   return Permit.UNDEFINED;
  
  return profile.checkPermission(act);
 }

 @Override
 public GroupBean getSubject()
 {
  return group;
 }

 @Override
 public ProfileBean getPermissionUnit()
 {
  return profile;
 }

 public void setSubject( GroupBean gb )
 {
  group=gb;
 }

 public void setPermissionUnit( ProfileBean pb )
 {
  profile=pb;
 }
}
