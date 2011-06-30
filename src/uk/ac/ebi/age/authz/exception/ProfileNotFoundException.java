package uk.ac.ebi.age.authz.exception;

public class ProfileNotFoundException extends AuthException
{
 public ProfileNotFoundException()
 {
  super("Profile not found");
 }
}
