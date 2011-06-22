package uk.ac.ebi.age.authz;

import java.util.Collection;

public interface UserGroup
{

 String getId();

 String getDescription();

 public Collection< ? extends UserGroup> getGroups();
}
