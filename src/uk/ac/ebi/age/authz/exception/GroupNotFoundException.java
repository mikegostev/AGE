package uk.ac.ebi.age.authz.exception;

public class GroupNotFoundException extends AuthException
{
 public GroupNotFoundException()
 {
  super("Group not found");
 }
}
