package uk.ac.ebi.age.authz;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.authz.exception.AuthException;
import uk.ac.ebi.age.ext.authz.SystemAction;

import com.pri.util.collection.ListFragment;

public interface AuthDB
{
 AuthDBSession createSession();

 User getUser( String id );
 List< ? extends User> getUsers(int begin, int end);
 ListFragment<User> getUsers(String idPat, String namePat, int begin, int end);

 int getUsersTotal();

 void updateUser(String userId, String userName, String userPass) throws AuthException;

 void addUser(String userId, String userName, String userPass) throws AuthException;

 void deleteUser(String userId) throws AuthException;

 
 UserGroup getUserGroup( String id );
 List< ? extends UserGroup> getGroups(int begin, int end);
 ListFragment<UserGroup> getGroups(String idPat, String namePat, int begin, int end);

 int getGroupsTotal();

 void deleteGroup(String grpId) throws AuthException;

 void addGroup(String grpId, String grpDesc) throws AuthException;

 void updateGroup(String grpId, String grpDesc) throws AuthException;

 Collection< ? extends UserGroup> getGroupsOfUser(String userId) throws AuthException;

 void removeUserFromGroup(String grpId, String userId) throws AuthException;
 void removeGroupFromGroup(String grpId, String partId) throws AuthException;

 void addUserToGroup(String userId, String grpId) throws AuthException;
 void addGroupToGroup(String grpId, String partId) throws AuthException;

 Collection< ? extends User> getUsersOfGroup(String groupId) throws AuthException;
 Collection< ? extends UserGroup> getGroupsOfGroup(String groupId) throws AuthException;

 void addProfile(String profId, String dsc) throws AuthException;
 void updateProfile(String profId, String dsc) throws AuthException;
 void deleteProfile(String profId) throws AuthException;
 
 PermissionProfile getProfile( String id );
 List< ? extends PermissionProfile> getProfiles(int begin, int end);
 ListFragment<PermissionProfile> getProfiles(String idPat, String namePat, int begin, int end);
 int getProfilesTotal();

 void addPermissionToProfile(String profId, SystemAction actn, boolean allow) throws AuthException;
 void addProfileToProfile(String profId, String string) throws AuthException;
 Collection< ? extends Permission> getPermissionsOfProfile(String profId) throws AuthException;
 Collection< ? extends PermissionProfile> getProfilesOfProfile(String profId) throws AuthException;
 void removePermissionFromProfile(String profId, SystemAction actn, boolean allow) throws AuthException;
 void removeProfileFromProfile(String profId, String toRemProf) throws AuthException;



}
