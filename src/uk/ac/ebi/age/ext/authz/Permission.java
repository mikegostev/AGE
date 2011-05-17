package uk.ac.ebi.age.ext.authz;

public class Permission implements PermissionUnit
{
 private SystemAction action;
 private boolean deny;
 @Override

 public boolean isAllowed(SystemAction act)
 {
  return action == act && !deny;
 }
 @Override

 public boolean isDenied(SystemAction act)
 {
  return action == act && !deny;
 }
}
