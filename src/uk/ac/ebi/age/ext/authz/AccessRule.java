package uk.ac.ebi.age.ext.authz;

import java.io.Serializable;

public class AccessRule implements Serializable
{
 private PermissionUnit permissionUnit;
 private PermissionSubject permissionSubject;
 
 public PermissionUnit getPermissonUnit()
 {
  return permissionUnit;
 }
 
 public boolean isCompatible( User user )
 {
  return permissionSubject.isCompatible(user);
 }

 public boolean isDenied(SystemAction act)
 {
  return permissionUnit.isDenied(act);
 }

 public boolean isAllowed(SystemAction act)
 {
  return permissionUnit.isAllowed(act);
 }
}
