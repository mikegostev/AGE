package uk.ac.ebi.age.ext.authz;

import java.util.ArrayList;
import java.util.Collection;

public class PermissionProfile implements PermissionUnit
{
 private Collection<PermissionUnit> permissions = new ArrayList<PermissionUnit>();

 @Override
 public boolean isAllowed(SystemAction act)
 {
  boolean allowed = false;
  
  for( PermissionUnit pu : permissions )
  {
   if( pu.isDenied(act) )
    return false;
  
   if( pu.isAllowed( act ) )
    allowed = true;
  }
  
  return allowed;
 }

 @Override
 public boolean isDenied(SystemAction act)
 {
  for( PermissionUnit pu : permissions )
   if( pu.isDenied(act) )
    return true;
   
  return false;
 }
}
