package uk.ac.ebi.age.authz.impl;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.ext.authz.SystemAction;

public class PermissionBean implements Permission
{
 private SystemAction action;
 private boolean allow;
 


 @Override
 public String getDescription()
 {
  return action.getDescription();
 }


 public SystemAction getAction()
 {
  return action;
 }



 public void setAction(SystemAction action)
 {
  this.action = action;
 }



 public boolean isAllow()
 {
  return allow;
 }



 public void setAllow(boolean allow)
 {
  this.allow = allow;
 }

 @Override
 public Permit checkPermission(SystemAction act)
 {
  if( act != action )
   return Permit.UNDEFINED;
  
  return allow?Permit.ALLOW:Permit.DENY;
 }

}
