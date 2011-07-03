package uk.ac.ebi.age.authz;

import java.util.Collection;

public interface User extends Subject
{
 String getId();
 String getName();

 Collection< ? extends UserGroup> getGroups();
}