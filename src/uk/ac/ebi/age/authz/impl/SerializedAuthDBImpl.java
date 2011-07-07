package uk.ac.ebi.age.authz.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;

import uk.ac.ebi.age.authz.ACR;
import uk.ac.ebi.age.authz.AuthDB;
import uk.ac.ebi.age.authz.Classifier;
import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.authz.PermissionForGroupACR;
import uk.ac.ebi.age.authz.PermissionForUserACR;
import uk.ac.ebi.age.authz.PermissionProfile;
import uk.ac.ebi.age.authz.ProfileForGroupACR;
import uk.ac.ebi.age.authz.ProfileForUserACR;
import uk.ac.ebi.age.authz.Tag;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.age.authz.exception.AuthException;
import uk.ac.ebi.age.authz.exception.ClassifierExistsException;
import uk.ac.ebi.age.authz.exception.ClassifierNotFoundException;
import uk.ac.ebi.age.authz.exception.DBInitException;
import uk.ac.ebi.age.authz.exception.GroupCycleException;
import uk.ac.ebi.age.authz.exception.GroupExistsException;
import uk.ac.ebi.age.authz.exception.GroupNotFoundException;
import uk.ac.ebi.age.authz.exception.PermissionNotFound;
import uk.ac.ebi.age.authz.exception.ProfileCycleException;
import uk.ac.ebi.age.authz.exception.ProfileExistsException;
import uk.ac.ebi.age.authz.exception.ProfileNotFoundException;
import uk.ac.ebi.age.authz.exception.TagException;
import uk.ac.ebi.age.authz.exception.TagNotFoundException;
import uk.ac.ebi.age.authz.exception.UserExistsException;
import uk.ac.ebi.age.authz.exception.UserNotFoundException;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.transaction.InvalidStateException;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionException;
import uk.ac.ebi.age.transaction.TransactionalDB;
import uk.ac.ebi.age.transaction.UndefinedStateException;

import com.pri.util.collection.CollectionsUnion;
import com.pri.util.collection.ListFragment;

public class SerializedAuthDBImpl implements AuthDB
{
 private static final String serialFileName = "authdb.ser";
 
 private class RLock implements ReadLock
 {
  boolean active = true;
  TransactionalDB db;
  
  RLock( TransactionalDB db )
  {
   this.db=db;
  }
  
  void setActive( boolean a )
  {
   active = a;
  }

  public boolean isActive()
  {
   return active;
  }

  @Override
  public void release()
  {
   db.releaseLock(this);
  }
 }
 
 private class Tran extends RLock implements Transaction
 {
  Tran( TransactionalDB db )
  {
   super(db);
  }

 }
 
 private List<UserBean> userList;
 private List<GroupBean> groupList;
 private List<ProfileBean> profileList;
 private List<ClassifierBean> classifierList;
 
 private Map<String, UserBean> userMap;
 private Map<String,GroupBean> groupMap;
 private Map<String,ProfileBean> profileMap;
 private Map<String,ClassifierBean> classifierMap;

 private static class DataPacket implements Serializable
 {
  private static final long serialVersionUID = 1L;
  
  List<UserBean> userList;
  List<GroupBean> groupList;
  List<ProfileBean> profileList;
  List<ClassifierBean> classifierList;
  
  Map<String, UserBean> userMap;
  Map<String,GroupBean> groupMap;
  Map<String,ProfileBean> profileMap;
  Map<String,ClassifierBean> classifierMap;
 }
 
 private DataPacket dataPacket;
 
 private ReadWriteLock lock = new ReentrantReadWriteLock();
 
 private String relPath;
 private FileResourceManager txManager;
 private String serialFile;
 
 public SerializedAuthDBImpl(FileResourceManager frm, String authRelPath) throws DBInitException
 {
  txManager=frm;
  relPath=authRelPath;
 
  serialFile = authRelPath+"/"+serialFileName;
  
  if( new File(frm.getStoreDir(),serialFile).exists() )
  {
   try
   {
    readData();
   }
   catch(IOException e)
   {
    throw new DBInitException(e);
   }
  }
  else
  {
   dataPacket = new DataPacket();

   userList = dataPacket.userList = new ArrayList<UserBean>();
   groupList = dataPacket.groupList = new ArrayList<GroupBean>();
   profileList = dataPacket.profileList = new ArrayList<ProfileBean>();
   classifierList = dataPacket.classifierList = new ArrayList<ClassifierBean>();

   userMap = dataPacket.userMap = new HashMap<String, UserBean>();
   groupMap = dataPacket.groupMap = new HashMap<String, GroupBean>();
   profileMap = dataPacket.profileMap = new HashMap<String, ProfileBean>();
   classifierMap = dataPacket.classifierMap = new HashMap<String, ClassifierBean>();

   UserBean ub = new UserBean();
   ub.setName(anonymousUser);
   ub.setName("Built-in anonymous user");

   userList.add(ub);
   userMap.put(ub.getId(), ub);

   GroupBean gb = new GroupBean();
   gb.setId(ownerGroup);
   gb.setDescription("Built-in OWNER group");

   groupList.add(gb);
   groupMap.put(gb.getId(), gb);

   gb = new GroupBean();
   gb.setId(everyoneGroup);
   gb.setDescription("Built-in EVERYONE group");

   groupList.add(gb);
   groupMap.put(gb.getId(), gb);

   gb = new GroupBean();
   gb.setId(usersGroup);
   gb.setDescription("Built-in authenticated users group");

   groupList.add(gb);
   groupMap.put(gb.getId(), gb);

   String txId;

   try
   {
    txId = txManager.generatedUniqueTxId();
    txManager.startTransaction(txId);
    OutputStream outputStream = txManager.writeResource(txId, serialFile);
    
    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
    
    oos.writeObject(dataPacket);
    
    oos.close();
    
    txManager.commitTransaction(txId);
   }
   catch(Exception e)
   {
    throw new DBInitException(e);
   }

  }
 }
 
 private void readData() throws IOException
 {
  FileInputStream fis = new FileInputStream(serialFile);
  ObjectInputStream ois = new ObjectInputStream( fis );
  
  try
  {
   dataPacket = (DataPacket)ois.readObject();
  }
  catch(ClassNotFoundException e)
  {
   e.printStackTrace();
  }
  
  userList=dataPacket.userList;
  groupList=dataPacket.groupList;
  profileList=dataPacket.profileList;
  classifierList=dataPacket.classifierList;
  
  userMap=dataPacket.userMap;
  groupMap=dataPacket.groupMap;
  profileMap=dataPacket.profileMap;
  classifierMap=dataPacket.classifierMap;

  fis.close();
  
 }
 
/* 
 public SerializedAuthDBImpl(FileResourceManager frm, String authRelPath)
 {
  txManager=frm;
  relPath=authRelPath;
  
  groupList = new HashMap<String,GroupBean>(20);
  
  for( int i=1; i <= 13; i++ )
  {
   GroupBean u = new GroupBean();
   
   u.setId("Group"+i);
   u.setDescription("Test Group №"+i);
   
   groupList.put(u.getId(), u);

  }

  userList = new HashMap<String,UserBean>(200);
  for( int i=1; i <= 13; i++ )
  {
   UserBean u = new UserBean();
   
   u.setId("User"+i);
   u.setName("Test User №"+i);
   
   int n = Random.randInt(1, 5);
   for( int j=0; j < n; j++ )
   {
    GroupBean grp = groupList.get( Random.randInt(0, groupList.size()-1) );
    u.addGroup( grp );
    grp.addUser( u );
   }
   
   userList.put(u.getId(),u);
  }

  profileList = new HashMap<String,ProfileBean>();
  
  for( int i=1; i <=3; i++ )
  {
   ProfileBean pb = new ProfileBean();
   
   pb.setId("P"+i);
   pb.setDescription("Profile No."+i);
   
   PermissionBean prmb = new PermissionBean();
   prmb.setAction(SystemAction.READ);
   prmb.setAllow(true);

   pb.addPermission( prmb );
   
   prmb = new PermissionBean();
   prmb.setAction(SystemAction.CHANGE);
   prmb.setAllow(true);

   pb.addPermission( prmb );
   
   prmb = new PermissionBean();
   prmb.setAction(SystemAction.DELETE);
   prmb.setAllow(false);

   pb.addPermission( prmb );

   profileList.put(pb.getId(),pb);
  }

  
  ClassifierBean cb = new ClassifierBean();
  
  cb.setId("Test clssf");
  cb.setDescription("Test clssifier No1");
  
  TagBean a = new TagBean();
  a.setId("A");
  a.setDescription("Test tag A");
  
  TagBean b = new TagBean();
  b.setId("B");
  b.setDescription("Test tag B");
  
  TagBean a1 = new TagBean();
  a1.setId("A1");
  a1.setDescription("Test tag A1");
  a1.setParent(a);
  
  TagBean a2 = new TagBean();
  a2.setId("A2");
  a2.setDescription("Test tag A2");
  a2.setParent(a);
  
  cb.addTag(a);
  cb.addTag(a1);
  cb.addTag(a2);
  cb.addTag(b);
  
  classifierList = new HashMap<String, ClassifierBean>();
  
  classifierList.put(cb.getId(),cb);
 }
*/

 @Override
 public ReadLock getReadLock()
 {
  lock.readLock().lock();
  return new RLock( this );
 }

 @Override
 public Transaction startTransaction()
 {
  lock.writeLock().lock();
  return new Tran( this );
 }

 @Override
 public void commitTransaction(Transaction t) throws TransactionException
 {
  ((RLock)t).setActive(false);
  
  try
  {
   sync();
  }
  catch(Exception e)
  {
   throw new TransactionException("Transaction IO exception", e);
  }
  finally
  {
   lock.writeLock().unlock();
  }

 }

 private void sync() throws ResourceManagerException, IOException
 {
  String txId;

  txId = txManager.generatedUniqueTxId();

  txManager.startTransaction(txId);

  txManager.moveResource(txId, serialFile, serialFile + "." + System.currentTimeMillis(), true);

  OutputStream outputStream = txManager.writeResource(txId, serialFile);

  ObjectOutputStream oos = new ObjectOutputStream(outputStream);

  oos.writeObject(dataPacket);
  
  oos.close();

  txManager.commitTransaction(txId);
 }
 
 @Override
 public void rollbackTransaction(Transaction t) throws TransactionException
 {
  ((RLock)t).setActive(false);

  try
  {
   readData();
  }
  catch(IOException e)
  {
   throw new UndefinedStateException("System is in undefined state due to IO error",e);
  }
  finally
  {
   lock.writeLock().unlock();
  }
  
 }

 @Override
 public void releaseLock(ReadLock lck )
 {
  lock.readLock().unlock();
  ((RLock)lck).setActive(false);
 }
 
 private void checkState( ReadLock lck )
 {
  if( ! ((RLock)lck).isActive() )
   throw new InvalidStateException();
 }
 
 private ClassifierBean getClassifier( String id )
 {
  return classifierMap.get(id);
 }

 @Override
 public ClassifierBean getClassifier( ReadLock lck, String id)
 {
  checkState(lck);

  return getClassifier(id);
 }


 private TagBean getTag( String clsfId, String tagId) throws TagException
 {
  ClassifierBean c = getClassifier(clsfId);
  
  if( c == null )
   throw new ClassifierNotFoundException();
   
  return c.getTag(tagId);
 }

 @Override
 public TagBean getTag( ReadLock lck, String clsfId, String tagId) throws TagException
 {
  checkState(lck);
   
  return getTag(clsfId, tagId);
 }

 private UserBean getUser( String id)
 {
  return userMap.get(id);
 }
 
 @Override
 public UserBean getUser( ReadLock lck, String id)
 {
  checkState(lck);
  
  return getUser( id );
 }

 private GroupBean getUserGroup( String id )
 {
  return groupMap.get(id);
 }
 
 @Override
 public GroupBean getUserGroup( ReadLock lck, String id)
 {
  return getUserGroup(id);
 }

 private ProfileBean getProfile( String id)
 {
  return profileMap.get(id);
 }
 
 @Override
 public ProfileBean getProfile( ReadLock lck, String id)
 {
  checkState(lck);

  return getProfile(id);
 }
 
 @Override
 public List<? extends User> getUsers( ReadLock lck, int begin, int end)
 {
  checkState(lck);

  int to = end!=-1 && end <= userList.size() ?end:userList.size();
  
  return userList.subList(begin, to);
 }

 @Override
 public ListFragment<User> getUsers( ReadLock lck, String idPat, String namePat, int begin, int end)
 {
  checkState(lck);

  int pos=0;
  
  int to = end!=-1 && end <= userList.size() ?end:userList.size();

  
  ListFragment<User> res = new ListFragment<User>();
  
  List<User> sel = new ArrayList<User>();
  
  res.setList(sel);
  
  for( User u : userList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getName().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos < to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public int getUsersTotal(ReadLock lck)
 {
  checkState(lck);

  return userList.size();
 }

 @Override
 public void updateUser( Transaction lck, String userId, String userName, String userPass) throws AuthException
 {
  checkState(lck);

  for( UserBean u : userList )
  {
   if( u.getId().equals(userId) )
   {
    if( userName != null )
     u.setName(userName);

    if( userPass != null )
     u.setPass(userPass);
   
    return;
   }
  }
  
  throw new UserNotFoundException();
 }

 @Override
 public void addUser( Transaction lck, String userId, String userName, String userPass) throws AuthException
 {
  checkState(lck);

  if( getUser(userId) != null )
   throw new UserExistsException();
  
  UserBean u = new UserBean();
  
  u.setId(userId);
  u.setName(userName);
  u.setPass(userPass);
  
  userList.add( u );
 }

 @Override
 public void deleteUser( Transaction lck, String userId) throws AuthException
 {
  checkState(lck);

  Iterator<UserBean> iter = userList.iterator();
  
  while( iter.hasNext() )
  {
   UserBean u = iter.next();
   
   if( u.getId().equals(userId) )
   {
    iter.remove();   
    return;
   }
  }
  
  throw new UserNotFoundException();
 }

 @Override
 public List< ? extends UserGroup> getGroups(ReadLock lck, int begin, int end)
 {
  checkState(lck);

  int to = end!=-1 && end <= groupList.size() ?end:groupList.size();
  
  return groupList.subList(begin, to);
 }

 @Override
 public ListFragment<UserGroup> getGroups(ReadLock lck, String idPat, String namePat, int begin, int end)
 {
  checkState(lck);

  int pos=0;
  
  int to = end!=-1 && end <= groupList.size() ?end:groupList.size();

  
  ListFragment<UserGroup> res = new ListFragment<UserGroup>();
  
  List<UserGroup> sel = new ArrayList<UserGroup>();
  
  res.setList(sel);
  
  for( UserGroup u : groupList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getDescription().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos < to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public int getGroupsTotal( ReadLock lck )
 {
  checkState(lck);
  return groupList.size();
 }

 @Override
 public void deleteGroup(Transaction lck, String grpId) throws AuthException
 {
  checkState(lck);
  Iterator<GroupBean> iter = groupList.iterator();
  
  while( iter.hasNext() )
  {
   GroupBean u = iter.next();
   
   if( u.getId().equals(grpId) )
   {
    iter.remove();   
    return;
   }
  }
  
  throw new GroupNotFoundException();
 }

 @Override
 public void addGroup(Transaction lck, String grpId, String grpDesc) throws AuthException
 {
  checkState(lck);

  if( getUserGroup(grpId) != null  )
   throw new GroupExistsException();
  
  GroupBean u = new GroupBean();
  
  u.setId(grpId);
  u.setDescription(grpDesc);
  
  groupList.add( u );
 }

 @Override
 public void updateGroup(Transaction lck, String grpId, String grpDesc) throws AuthException
 {
  checkState(lck);

  GroupBean u = getUserGroup(grpId);
  
  if( u == null  )
   throw new GroupNotFoundException();

  u.setDescription(grpDesc);
  
 }

 @Override
 public Collection< ? extends UserGroup> getGroupsOfUser(ReadLock lck, String userId) throws AuthException
 {
  checkState(lck);

  User u = getUser(userId);
  
  if( u == null  )
   throw new UserNotFoundException();
  
  return u.getGroups();
 }

 @Override
 public void removeUserFromGroup(Transaction lck, String grpId, String userId) throws AuthException
 {
  checkState(lck);

  GroupBean gb = getUserGroup(grpId);
  
  if( gb == null )
   throw new GroupNotFoundException();
  
  UserBean ub = null;
  
  for( UserBean u : gb.getUsers() )
  {
   if( u.getId().equals(userId) )
   {
    ub = u;
    break;
   }
  }
  
  if( ub == null )
   throw new UserNotFoundException();
  
  gb.removeUser( ub );
  ub.removeGroup( gb );
 }

 @Override
 public void removeGroupFromGroup(Transaction lck, String grpId, String partId) throws AuthException
 {
  checkState(lck);

  GroupBean gb = getUserGroup(grpId);
  
  if( gb == null )
   throw new GroupNotFoundException();
  
  UserGroup gp = null;
  
  for( UserGroup g : gb.getGroups() )
  {
   if( g.getId().equals(partId) )
   {
    gp = g;
    break;
   }
  }
  
  if( gp == null )
   throw new GroupNotFoundException();
  
  gb.removeGroup( gp );
 }

 
 @Override
 public void addUserToGroup(Transaction lck, String grpId, String userId) throws AuthException
 {
  checkState(lck);

  GroupBean gb = getUserGroup(grpId);
  
  if( gb == null )
   throw new GroupNotFoundException();
  
  UserBean ub = null;
  
  for( UserBean u : userList )
  {
   if( u.getId().equals(userId) )
   {
    ub = u;
    break;
   }
  }
  
  if( ub == null )
   throw new UserNotFoundException();
  
  gb.addUser( ub );
  ub.addGroup( gb );
 }

 @Override
 public Collection< ? extends User> getUsersOfGroup(ReadLock lck, String groupId) throws AuthException
 {
  checkState(lck);

  GroupBean gb = getUserGroup(groupId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  return gb.getUsers();
 }

 @Override
 public Collection< ? extends UserGroup> getGroupsOfGroup(ReadLock lck, String groupId) throws AuthException
 {
  checkState(lck);

  GroupBean gb = getUserGroup(groupId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  return gb.getGroups();
 }

 @Override
 public void addGroupToGroup(Transaction lck, String grpId, String partId) throws AuthException
 {
  checkState(lck);

  GroupBean gb = getUserGroup(grpId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  GroupBean pb = getUserGroup(partId);
  
  if( pb == null )
   throw new GroupNotFoundException();
 
  if( grpId.equals(partId) || gb.isPartOf(pb) )
   throw new GroupCycleException();
  
  
  gb.addGroup( pb );
 }

 @Override
 public void addProfile(Transaction lck, String profId, String dsc) throws AuthException
 {
  checkState(lck);

  if( getProfile(profId) != null )
   throw new ProfileExistsException();
  
  ProfileBean pb = new ProfileBean();
  
  pb.setId(profId);
  pb.setDescription(dsc);
  
  profileList.add( pb );
 }

 @Override
 public void updateProfile(Transaction lck, String profId, String dsc) throws AuthException
 {
  checkState(lck);

  ProfileBean pf =getProfile(profId);
  
  if( pf == null )
   throw new ProfileNotFoundException();
  
  pf.setDescription(dsc);
 }

 @Override
 public void deleteProfile(Transaction lck, String profId) throws AuthException
 {
  checkState(lck);

  Iterator<ProfileBean> iter = profileList.iterator();
  
  while( iter.hasNext() )
  {
   ProfileBean u = iter.next();
   
   if( u.getId().equals(profId) )
   {
    iter.remove();   
    return;
   }
  }
  
  throw new ProfileNotFoundException();
 }

 @Override
 public List< ? extends PermissionProfile> getProfiles(ReadLock lck, int begin, int end)
 {
  checkState(lck);

  int to = end!=-1 && end <= profileList.size() ?end:profileList.size();
  
  return profileList.subList(begin, to);
 }

 @Override
 public ListFragment<PermissionProfile> getProfiles(ReadLock lck, String idPat, String namePat, int begin, int end)
 {
  checkState(lck);

  int pos=0;
  
  int to = end!=-1 && end <= userList.size() ?end:userList.size();

  
  ListFragment<PermissionProfile> res = new ListFragment<PermissionProfile>();
  
  List<PermissionProfile> sel = new ArrayList<PermissionProfile>();
  
  res.setList(sel);
  
  for( PermissionProfile u : profileList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getDescription().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos < to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public int getProfilesTotal(ReadLock lck)
 {
  checkState(lck);

  return profileList.size();
 }

 @Override
 public void addPermissionToProfile(Transaction lck, String profId, SystemAction actn, boolean allow) throws AuthException
 {
  checkState(lck);

  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();

  
  if( prof.getPermissions() != null )
  {
   for( Permission p : prof.getPermissions() )
   {
    if( p.getAction() == actn && p.isAllow() == allow )
     return;
   }
  }
  
  PermissionBean pb = new PermissionBean();
  pb.setAction(actn);
  pb.setAllow(allow);
  
  prof.addPermission(pb);
 }

 @Override
 public Collection< ? extends Permission> getPermissionsOfProfile(ReadLock lck, String profId) throws AuthException
 {
  checkState(lck);

  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();


  return prof.getPermissions();
 }

 @Override
 public Collection< ? extends PermissionProfile> getProfilesOfProfile(ReadLock lck, String profId) throws AuthException
 {
  checkState(lck);

  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();


  return prof.getProfiles();
 }

 
 @Override
 public void removePermissionFromProfile(Transaction lck, String profId, SystemAction actn, boolean allow) throws AuthException
 {
  checkState(lck);

  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();


  Permission perm = null;
  for( Permission pm : prof.getPermissions() )
  {
   if( pm.getAction() == actn && pm.isAllow() == allow )
   {
    perm=pm;
    break;
   }
  }
  
  if( perm == null )
   throw new PermissionNotFound();
 
  prof.removePermission( perm );
 }
 
 @Override
 public void removeProfileFromProfile(Transaction lck, String profId, String toRemProf) throws AuthException
 {
  checkState(lck);

  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();

  PermissionProfile rmProf = getProfile(toRemProf);
  
  if( rmProf == null )
   throw new ProfileNotFoundException();
 
  prof.removeProfile( rmProf );
 }


 @Override
 public void addProfileToProfile(Transaction lck, String profId, String toAdd) throws AuthException
 {
  checkState(lck);

  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();

 
  ProfileBean npb = getProfile(toAdd);
  
  if( npb == null )
   throw new ProfileNotFoundException();

  if( profId.equals(toAdd) || prof.isPartOf(npb) )
   throw new ProfileCycleException();
  
  
  prof.addProfile( npb );
 }

 @Override
 public void deleteClassifier(Transaction lck, String csfId) throws TagException
 {
  checkState(lck);

  Iterator<ClassifierBean> iter = classifierList.iterator();
  
  while( iter.hasNext() )
  {
   ClassifierBean u = iter.next();
   
   if( u.getId().equals(csfId) )
   {
    iter.remove();   
    return;
   }
  }
  
  throw new ClassifierNotFoundException();
 }

 @Override
 public void addClassifier(Transaction lck, String csfId, String csfDesc) throws TagException
 {
  checkState(lck);

  if( getClassifier(csfId) != null )
   throw new ClassifierExistsException();
  
  ClassifierBean cb = new ClassifierBean();
  
  cb.setId( csfId );
  cb.setDescription(csfDesc);
  
  classifierList.add(cb);
 }

 @Override
 public void updateClassifier(Transaction lck, String csfId, String csfDesc) throws TagException
 {
  checkState(lck);

  ClassifierBean cb = getClassifier(csfId);
  
  if( cb == null )
   throw new ClassifierNotFoundException();

  cb.setDescription(csfDesc);
  
 }

 @Override
 public List< ? extends Classifier> getClassifiers( ReadLock lck, int begin, int end)
 {
  checkState(lck);

  return classifierList;
 }

 @Override
 public int getClassifiersTotal( ReadLock lck )
 {
  checkState(lck);

  return classifierList.size();
 }

 @Override
 public ListFragment<Classifier> getClassifiers( ReadLock lck, String idPat, String namePat, int begin, int end)
 {
  checkState(lck);

  int pos=0;
  
  int to = end!=-1 && end <= classifierList.size() ?end:classifierList.size();

  
  ListFragment<Classifier> res = new ListFragment<Classifier>();
  
  List<Classifier> sel = new ArrayList<Classifier>();
  
  res.setList(sel);
  
  for( Classifier u : classifierList )
  {
   if( idPat != null && u.getId().indexOf(idPat) == -1 )
    continue;

   if( namePat != null && u.getDescription().indexOf(namePat) == -1 )
    continue;

   if( pos >= begin && pos < to )
    sel.add(u);
  
   pos++;
  }
  
  res.setTotalLength(pos);
  
  return res;
 }

 @Override
 public void removeTagFromClassifier(Transaction lck, String clsId, String tagId) throws TagException
 {
  checkState(lck);

  ClassifierBean cb = getClassifier(clsId);
  
  if( cb == null )
   throw new ClassifierNotFoundException(); 
  
  Iterator<? extends Tag> tgItr = cb.getTags().iterator();
  
  while( tgItr.hasNext() )
  {
   Tag t = tgItr.next();
   
   if( t.getId().equals(tagId) )
   {
    tgItr.remove();
    continue;
   }
   
   Tag pt = t.getParent();
   
   while( pt != null )
   {
    if( pt.getId().equals(tagId) )
    {
     tgItr.remove();
     break;
    }
    
    pt = pt.getParent();
   }
   
  }
 }

 @Override
 public void addTagToClassifier(Transaction lck, String clsId, String tagId, String description, String parentTagId) throws TagException
 {
  checkState(lck);

  ClassifierBean clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  Tag pTag = null;
  
  if( parentTagId != null )
  {
   pTag = clsb.getTag(parentTagId);
   
   if( pTag == null )
    throw new TagNotFoundException();
  }
  

  if( clsb.getTag(tagId) != null )
   throw new ClassifierExistsException();
  
  TagBean tb = new TagBean();
  
  tb.setId(tagId);
  tb.setDescription(description);
  tb.setParent(pTag);
 
  clsb.addTag( tb );
 }

 @Override
 public void updateTag(Transaction lck, String clsId, String tagId, String desc, String parentTagId) throws TagException
 {
  checkState(lck);

  ClassifierBean clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  Tag pTag = null;
  
  if( parentTagId != null )
  {
   pTag = clsb.getTag(parentTagId);
   
   if( pTag == null )
    throw new TagNotFoundException();
  }
  
  TagBean tb = clsb.getTag(tagId);

  if( tb == null )
   throw new ClassifierNotFoundException();
  
  tb.setDescription(desc);
  tb.setParent(pTag);
 }

 @Override
 public Collection< ? extends Tag> getTagsOfClassifier( ReadLock lck, String clsId) throws TagException
 {
  checkState(lck);

  ClassifierBean clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  return clsb.getTags();
 }

 @Override
 public Collection< ? extends Tag> getTagsOfClassifier( ReadLock lck, String clsId, final String parentTagId) throws TagException
 {
  checkState(lck);

  final ClassifierBean clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  return new AbstractCollection<TagBean>()
  {

   @Override
   public Iterator<TagBean> iterator()
   {
    return new Iterator<TagBean>()
    {
     private TagBean next;
     private Iterator<TagBean> iter = clsb.getTags().iterator();

     @Override
     public boolean hasNext()
     {
      if( next != null )
       return true;
      
      if( ! iter.hasNext() )
       return false;
      
      do
      {
       next = iter.next();
       
       if( parentTagId == null )
       {
        if( next.getParent() == null )
         return true;
       }
       else if( parentTagId.equals(next.getParent()) )
        return true;
       
      }while( iter.hasNext() );
      
      return false;
     }

     @Override
     public TagBean next()
     {
      if( ! hasNext() )
       return null;
      
      TagBean nxt = next;
      next = null;
      
      return nxt;
     }

     @Override
     public void remove()
     {
     }
    };
   }

   @Override
   public int size()
   {
    // TODO Auto-generated method stub
    return 0;
   }
  };
 }

 @Override
 public boolean removeProfileForGroupACR(Transaction lck, String clsfId, String tagId, String subjId, String profileId) throws TagException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  Collection<? extends ProfileForGroupACR> acrs = tb.getProfileForGroupACRs();
  
  Iterator<? extends ProfileForGroupACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   ProfileForGroupACR acr = iter.next();
   
   if(acr.getPermissionUnit().getId().equals(profileId) && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }
  
  return false;
 }

 @Override
 public boolean removeProfileForUserACR(Transaction lck, String clsfId, String tagId, String subjId, String profileId) throws TagException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  Collection<? extends ProfileForUserACR> acrs = tb.getProfileForUserACRs();
  
  Iterator<? extends ProfileForUserACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   ProfileForUserACR acr = iter.next();
   
   if(acr.getPermissionUnit().getId().equals(profileId) && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
 }

 @Override
 public boolean removePermissionForUserACR(Transaction lck, String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  Collection<? extends PermissionForUserACR> acrs = tb.getPermissionForUserACRs();
  
  Iterator<? extends PermissionForUserACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   PermissionForUserACR acr = iter.next();
   
   if(acr.getPermissionUnit().getAction() == act && acr.getPermissionUnit().isAllow() == allow && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
 }

 @Override
 public boolean removePermissionForGroupACR(Transaction lck, String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  Collection<? extends PermissionForGroupACR> acrs = tb.getPermissionForGroupACRs();
  
  Iterator<? extends PermissionForGroupACR> iter = acrs.iterator();
  
  while( iter.hasNext() )
  {
   PermissionForGroupACR acr = iter.next();
   
   if(acr.getPermissionUnit().getAction() == act && acr.getPermissionUnit().isAllow() == allow && acr.getSubject().getId().equals(subjId) )
   {
    iter.remove();
    return true;
   }
  }

  return false;
 }

 @Override
 public void addProfileForGroupACR(Transaction lck, String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();

  ProfileForGroupACRBean acr = new ProfileForGroupACRBean();
  
  ProfileBean pb = getProfile(profileId);
  
  if( pb == null )
   throw new ProfileNotFoundException();
  
  GroupBean gb = getUserGroup(subjId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  tb.addProfileForGroupACR( acr );
 }

 @Override
 public void addProfileForUserACR(Transaction lck, String clsfId, String tagId, String subjId, String profileId) throws TagException, AuthException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();

  ProfileForUserACRBean acr = new ProfileForUserACRBean();
  
  ProfileBean pb = getProfile(profileId);
  
  if( pb == null )
   throw new ProfileNotFoundException();
  
  UserBean gb = getUser(subjId);
  
  if( gb == null )
   throw new UserNotFoundException();

  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  tb.addProfileForUserACR( acr );
 }

 @Override
 public void addActionForUserACR(Transaction lck, String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();

  PermissionForUserACRBean acr = new PermissionForUserACRBean();
  
  UserBean gb = getUser(subjId);
  
  if( gb == null )
   throw new UserNotFoundException();

  PermissionBean pb = new PermissionBean();
  pb.setAction(act);
  pb.setAllow(allow);
  
  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  tb.addPermissionForUserACR( acr );
 }

 @Override
 public void addActionForGroupACR(Transaction lck, String clsfId, String tagId, String subjId, SystemAction act, boolean allow) throws TagException, AuthException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();

  PermissionForGroupACRBean acr = new PermissionForGroupACRBean();
  
  GroupBean gb = getUserGroup(subjId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  PermissionBean pb = new PermissionBean();
  pb.setAction(act);
  pb.setAllow(allow);
  
  acr.setPermissionUnit(pb);
  acr.setSubject(gb);
  
  tb.addPermissionForGroupACR( acr );
 }

 @SuppressWarnings("unchecked")
 @Override
 public Collection<? extends ACR> getACL( ReadLock lck, String clsfId, String tagId) throws TagException
 {
  checkState(lck);

  TagBean tb = getTag(clsfId, tagId);
  
  if( tb == null )
   throw new TagNotFoundException();
  
  return new CollectionsUnion<ACR>( new Collection[] {
    tb.getPermissionForUserACRs(),
    tb.getPermissionForGroupACRs(),
    tb.getProfileForUserACRs(),
    tb.getProfileForGroupACRs()});
 }

}
