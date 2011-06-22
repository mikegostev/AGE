package uk.ac.ebi.age.authz;

public class GroupCycleException extends AuthException
{
 public GroupCycleException()
 {
  super("Group participation loop");
 }
}
