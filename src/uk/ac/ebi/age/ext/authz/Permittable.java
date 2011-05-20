package uk.ac.ebi.age.ext.authz;

public interface Permittable
{
 boolean checkPermission( SystemAction act, User user );
}
