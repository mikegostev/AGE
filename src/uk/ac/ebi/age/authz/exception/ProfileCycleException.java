package uk.ac.ebi.age.authz.exception;

public class ProfileCycleException extends AuthException
{
 public ProfileCycleException()
 {
  super("Profiles loop");
 }
}
