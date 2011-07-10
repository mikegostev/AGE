package uk.ac.ebi.age.authz.impl;

import java.io.Serializable;

import uk.ac.ebi.age.authz.PermissionForGroupACR;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class PermissionForGroupACRBean implements PermissionForGroupACR, Serializable
{

 private static final long serialVersionUID = 1L;


 private GroupBean subject;
 private PermissionBean perm;

 @Override
 public Permit checkPermission(SystemAction act, User user)
 {
  if( ! subject.isUserCompatible(user) )
   return Permit.UNDEFINED;
  
  return perm.checkPermission(act);
 }

 @Override
 public UserGroup getSubject()
 {
  return subject;
 }

 @Override
 public PermissionBean getPermissionUnit()
 {
  return perm;
 }
 
 public void setSubject( GroupBean gb )
 {
  subject=gb;
 }

 public void setPermissionUnit( PermissionBean pb )
 {
  perm=pb;
 }

}
