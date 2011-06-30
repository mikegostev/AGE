package uk.ac.ebi.age.authz.exception;

public class GroupCycleException extends AuthException
{
 public GroupCycleException()
 {
  super("Group participation loop");
 }
}
