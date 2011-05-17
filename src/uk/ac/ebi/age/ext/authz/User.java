package uk.ac.ebi.age.ext.authz;

import java.io.Serializable;

public class User implements PermissionSubject, Serializable
{
 String name;

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
}
