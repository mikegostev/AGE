package uk.ac.ebi.age.ext.authz;

public interface PermissionSubject
{
 boolean isCompatible( User u );
 
}
