package uk.ac.ebi.age.ext.authz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class User implements PermissionSubject, Serializable
{
 private String name;
 private Collection<UserGroup> groups = new ArrayList<UserGroup>();
 private transient TagResolver tReslv;
 
 public boolean checkPermission( SystemAction act, Collection<String> tags )
 {
  boolean allow = false;
  
  for( String tn : tags )
  {
   AuthTag t = tReslv.getTagByName(tn);
   
   if( t == null )
    continue;
   
   if( t.isDenied( act, this ) )
    return false;
   
   if( t.isAllowed( act, this ) )
    allow = true;
  }
  
  return allow;
 }
 
 @Override
 public boolean isCompatible( User u )
 {
  return name.equals( u.getName() );
 }

 public String getName()
 {
  return name;
 }

 public void setName(String name)
 {
  this.name = name;
 }
 
 public void setTagResolver( TagResolver tr )
 {
  tReslv=tr;
 }
}
