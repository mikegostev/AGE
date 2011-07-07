package uk.ac.ebi.age.authz;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.ext.authz.SystemAction;

public interface Tag
{
 String getId();
 String getDescription();
 Tag getParent();
 
 Permit checkPermission( SystemAction act, User user );

}
