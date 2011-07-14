package uk.ac.ebi.age.authz;

import java.util.Collection;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.entity.ID;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.ext.authz.TagRef;

public interface PermissionManager
{

 Permit checkSystemPermission(SystemAction act);

 Permit checkPermission(SystemAction act, ID objId);

 Permit checkPermission(SystemAction act, String objOwner, ID objId);

 Permit checkSystemPermission(SystemAction act, String user);

 Collection<TagRef> getEffectiveTags(ID objId);

 Collection<TagRef> getAllowTags(SystemAction act, String user);
 Collection<TagRef> getDenyTags(SystemAction act, String user);

}