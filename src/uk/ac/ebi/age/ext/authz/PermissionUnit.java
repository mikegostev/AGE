package uk.ac.ebi.age.ext.authz;

public interface PermissionUnit
{
 boolean isAllowed( SystemAction act );
 boolean isDenied( SystemAction act );
}
