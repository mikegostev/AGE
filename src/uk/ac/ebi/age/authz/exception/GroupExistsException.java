package uk.ac.ebi.age.authz.exception;

public class GroupExistsException extends AuthException
{
 public GroupExistsException()
 {
  super("Group already exists");
 }
}
