package uk.ac.ebi.age.ext.authz;

import java.io.Serializable;

public class AccessRule implements Serializable
{
 private PermissionUnit permissionUnit;
 private PermissionSubject permissionSubject;
 
 public boolean ckeckPermission( SystemAction act, User user )
 {
  return permissionSubject.isCompatible(user) && permissionUnit.isAllowed(act) ; 
 }
}
