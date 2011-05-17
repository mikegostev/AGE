package uk.ac.ebi.age.ext.authz;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserGroup implements PermissionSubject, Serializable
{
 private String name;
 private Map<String,PermissionSubject> subjects = new HashMap<String,PermissionSubject>();

 public String getName()
 {
  return name;
 }
 
 public void setName(String name)
 {
  this.name = name;
 }

 @Override
 public boolean isCompatible(User u)
 {
  return subjects.containsKey( u.getName() );
 } 
}
