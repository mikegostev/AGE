package uk.ac.ebi.age.authz.exception;

public class UserNotFoundException extends AuthException
{
 public UserNotFoundException()
 {
  super("User not found");
 }
}
