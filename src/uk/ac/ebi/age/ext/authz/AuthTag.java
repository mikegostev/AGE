package uk.ac.ebi.age.ext.authz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class AuthTag implements Serializable
{
 private Collection<AccessRule> rules = new ArrayList<AccessRule>();


 public boolean isDenied(SystemAction act, User user)
 {
  for( AccessRule p : rules )
  {
   if( ! p.isCompatible(user) )
    continue;
   
   if( p.isDenied(act) )
    return true;
  }
  
  return false;
 }
 
 public boolean isAllowed(SystemAction act, User user)
 {
  boolean allowed = false;
  
  for( AccessRule p : rules )
  {
   if( ! p.isCompatible(user) )
    continue;

   if( p.isDenied(act) )
    return false;
  
   if( p.isAllowed( act ) )
    allowed = true;
  }
  
  return allowed;
 }

}
