package uk.ac.ebi.age.authz.impl;

import uk.ac.ebi.age.authz.ProfileForUserACR;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class ProfileForUserACRBean implements ProfileForUserACR
{
 private UserBean subject;
 private ProfileBean profile;

 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  if( ! subject.isUserCompatible(user) )
   return Permit.UNDEFINED;
  
  return profile.checkPermission(act);
 }

 @Override
 public UserBean getSubject()
 {
  return subject;
 }

 @Override
 public ProfileBean getPermissionUnit()
 {
  return profile;
 }
 
 public void setSubject( UserBean ub )
 {
  subject=ub;
 }

 public void setPermissionUnit( ProfileBean pb )
 {
  profile=pb;
 }


}
