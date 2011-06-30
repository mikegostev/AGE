package uk.ac.ebi.age.authz;

import java.util.Collection;

public interface User
{

 String getId();

 String getName();

 Collection< ? extends UserGroup> getGroups();

}
