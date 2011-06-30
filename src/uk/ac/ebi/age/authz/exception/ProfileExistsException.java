package uk.ac.ebi.age.authz.exception;

public class ProfileExistsException extends AuthException
{
 public ProfileExistsException()
 {
  super("Profile exists");
 }
}
