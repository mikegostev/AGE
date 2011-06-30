package uk.ac.ebi.age.authz.exception;

public class PermissionNotFound extends AuthException
{
 public PermissionNotFound()
 {
  super("Permission not found");
 }
}
