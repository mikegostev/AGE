package uk.ac.ebi.age.authz;

import java.util.Collection;
import java.util.List;

import com.pri.util.collection.ListFragment;

public interface AuthDB
{
 AuthDBSession createSession();

 List< ? extends User> getUsers(int begin, int end);
 ListFragment<User> getUsers(String idPat, String namePat, int begin, int end);

 int getUsersTotal();

 void updateUser(String userId, String userName, String userPass) throws AuthException;

 void addUser(String userId, String userName, String userPass) throws AuthException;

 void deleteUser(String userId) throws AuthException;

 
 List< ? extends UserGroup> getGroups(int begin, int end);
 ListFragment<UserGroup> getGroups(String idPat, String namePat, int begin, int end);

 int getGroupsTotal();

 void deleteGroup(String grpId) throws AuthException;

 void addGroup(String grpId, String grpDesc) throws AuthException;

 void updateGroup(String grpId, String grpDesc) throws AuthException;

 Collection< ? extends UserGroup> getGroupsOfUser(String userId) throws AuthException;

 void removeUserFromGroup(String grpId, String userId) throws AuthException;
}
