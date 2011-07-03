package uk.ac.ebi.age.authz;

import java.util.Collection;

public interface PermissionProfile extends PermissionUnit
{

 String getId();

 String getDescription();

 Collection<? extends Permission> getPermissions();
 Collection<? extends PermissionProfile> getProfiles();

}
