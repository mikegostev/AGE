package uk.ac.ebi.age.authz.impl;

import uk.ac.ebi.age.authz.PermissionForUserACR;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class PermissionForUserACRBean implements PermissionForUserACR
{
 private UserBean subject;
 private PermissionBean perm;

 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  if( ! subject.isUserCompatible(user) )
   return Permit.UNDEFINED;
  
  return perm.checkPermission(act);
 }

 @Override
 public UserBean getSubject()
 {
  return subject;
 }

 @Override
 public PermissionBean getPermissionUnit()
 {
  return perm;
 }
 
 public void setSubject( UserBean ub )
 {
  subject=ub;
 }

 public void setPermissionUnit( PermissionBean pb )
 {
  perm=pb;
 }


}
