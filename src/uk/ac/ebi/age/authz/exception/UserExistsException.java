package uk.ac.ebi.age.authz.exception;

public class UserExistsException extends AuthException
{
 public UserExistsException()
 {
  super("User exists");
 }
}
