package uk.ac.ebi.age.ext.authz;

public class AccessRule
{
 private PermissionUnit permissionUnit;
 private PermissionSubject permissionSubject;
 
 public boolean ckeckPermission( SystemAction act, User user )
 {
  return permissionSubject.isCompatible(user) && permissionUnit.isAllowed(act) ; 
 }
}
