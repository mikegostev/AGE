package uk.ac.ebi.age.authz.impl;

import java.io.Serializable;

import uk.ac.ebi.age.authz.BuiltInUsers;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.mg.collection.Named;

public class AuthenticatedGroupBean extends GroupBean  implements UserGroup, Named<String>, Serializable
{

 private static final long serialVersionUID = 1L;


 public boolean isPartOf(UserGroup pb)
 {
  return false;
 }

 @Override
 public boolean isUserCompatible(User u)
 {
  return ! u.getId().equals(BuiltInUsers.ANONYMOUS.getName());
 }

}
