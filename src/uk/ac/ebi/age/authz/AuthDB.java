package uk.ac.ebi.age.authz;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.authz.exception.AuthException;
import uk.ac.ebi.age.authz.exception.TagException;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionalDB;

import com.pri.util.collection.ListFragment;

public interface AuthDB extends TransactionalDB
{
 public final static String anonymousUser="$anonymous";
 public final static String ownerGroup="$owner";
 public final static String everyoneGroup="$everyone";
 public final static String usersGroup="$users";

 User getUser( ReadLock lock, String id );
 List< ? extends User> getUsers( ReadLock lock, int begin, int end);
 ListFragment<User> getUsers( ReadLock lock, String idPat, String namePat, int begin, int end);

 int getUsersTotal( ReadLock lock );

 void updateUser( Transaction trn, String userId, String userName, String userPass) throws AuthException;

 void addUser( Transaction trn, String userId, String userName, String userPass) throws AuthException;

 void deleteUser( Transaction trn, String userId) throws AuthException;

 
 UserGroup getUserGroup( ReadLock lock, String id );
 List< ? extends UserGroup> getGroups( ReadLock lock,int begin, int end);
 ListFragment<UserGroup> getGroups( ReadLock lock,String idPat, String namePat, int begin, int end);

 int getGroupsTotal( ReadLock lock);

 void deleteGroup( Transaction trn, String grpId) throws AuthException;

 void addGroup( Transaction trn, String grpId, String grpDesc) throws AuthException;

 void updateGroup( Transaction trn, String grpId, String grpDesc) throws AuthException;

 Collection< ? extends UserGroup> getGroupsOfUser( ReadLock lock, String userId) throws AuthException;

 void removeUserFromGroup( Transaction trn, String grpId, String userId) throws AuthException;
 void removeGroupFromGroup( Transaction trn, String grpId, String partId) throws AuthException;

 void addUserToGroup( Transaction trn, String userId, String grpId) throws AuthException;
 void addGroupToGroup( Transaction trn, String grpId, String partId) throws AuthException;

 Collection< ? extends User> getUsersOfGroup( ReadLock lock, String groupId) throws AuthException;
 Collection< ? extends UserGroup> getGroupsOfGroup( ReadLock lock, String groupId) throws AuthException;

 void addProfile( Transaction trn, String profId, String dsc) throws AuthException;
 void updateProfile( Transaction trn, String profId, String dsc) throws AuthException;
 void deleteProfile( Transaction trn, String profId) throws AuthException;
 
 PermissionProfile getProfile( ReadLock lock, String id );
 List< ? extends PermissionProfile> getProfiles( ReadLock lock, int begin, int end);
 ListFragment<PermissionProfile> getProfiles( ReadLock lock, String idPat, String namePat, int begin, int end);
 int getProfilesTotal( ReadLock lock );

 void addPermissionToProfile( Transaction trn, String profId, SystemAction actn, boolean allow) throws AuthException;
 void addProfileToProfile( Transaction trn, String profId, String string) throws AuthException;
 Collection< ? extends Permission> getPermissionsOfProfile( ReadLock lock, String profId) throws AuthException;
 Collection< ? extends PermissionProfile> getProfilesOfProfile( ReadLock lock, String profId) throws AuthException;
 void removePermissionFromProfile( Transaction trn, String profId, SystemAction actn, boolean allow) throws AuthException;
 void removeProfileFromProfile( Transaction trn, String profId, String toRemProf) throws AuthException;


 
 void deleteClassifier( Transaction trn,String csfId) throws TagException;

 void addClassifier( Transaction trn,String csfId, String csfDesc) throws TagException;

 void updateClassifier( Transaction trn,String csfId, String csfDesc) throws TagException;

 Classifier getClassifier( ReadLock lock,  String id );
 List< ? extends Classifier> getClassifiers( ReadLock lock, int begin, int end);

 int getClassifiersTotal( ReadLock lock );

 ListFragment<Classifier> getClassifiers( ReadLock lock, String string, String string2, int begin, int end);

 void removeTagFromClassifier( Transaction trn,String clsId, String tagId) throws TagException;

 void addTagToClassifier( Transaction trn,String clsId, String tagId, String description, String parentTagId) throws TagException;

 void updateTag( Transaction trn,String clsId, String tagId, String desc, String parentTagId) throws TagException;

 Tag getTag( ReadLock lock,  String clsfId, String tagId ) throws TagException;

 Collection< ? extends Tag> getTagsOfClassifier( ReadLock lock, String clsId, String parentTagId) throws TagException;
 Collection< ? extends Tag> getTagsOfClassifier( ReadLock lock, String clsId) throws TagException;

 boolean removeProfileForGroupACR( Transaction trn,String clsfId, String tagId, String subjId, String profileId) throws TagException;
 boolean removeProfileForUserACR( Transaction trn,String clsfId, String tagId, String subjId, String profileId) throws TagException;
 boolean removePermissionForUserACR( Transaction trn,String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException;
 boolean removePermissionForGroupACR( Transaction trn,String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException;

 void addProfileForGroupACR( Transaction trn,String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthException;
 void addProfileForUserACR( Transaction trn,String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthException;
 void addActionForUserACR( Transaction trn,String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthException;
 void addActionForGroupACR( Transaction trn,String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthException;

 Collection< ? extends ACR> getACL( ReadLock lock, String clsfId, String tagId) throws TagException;

}
