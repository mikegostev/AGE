package uk.ac.ebi.age.authz;

import uk.ac.ebi.age.ext.authz.SystemAction;

public interface Permission
{
 SystemAction getAction();
 boolean isAllow();
 String getDescription();
}
