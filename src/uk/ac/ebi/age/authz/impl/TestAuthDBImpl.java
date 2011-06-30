package uk.ac.ebi.age.authz.impl;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.age.authz.AuthDB;
import uk.ac.ebi.age.authz.AuthDBSession;
import uk.ac.ebi.age.authz.Permission;
import uk.ac.ebi.age.authz.PermissionProfile;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.age.authz.exception.AuthException;
import uk.ac.ebi.age.authz.exception.GroupCycleException;
import uk.ac.ebi.age.authz.exception.GroupExistsException;
import uk.ac.ebi.age.authz.exception.GroupNotFoundException;
import uk.ac.ebi.age.authz.exception.PermissionNotFound;
import uk.ac.ebi.age.authz.exception.ProfileCycleException;
import uk.ac.ebi.age.authz.exception.ProfileExistsException;
import uk.ac.ebi.age.authz.exception.ProfileNotFoundException;
import uk.ac.ebi.age.authz.exception.UserExistsException;
import uk.ac.ebi.age.authz.exception.UserNotFoundException;
import uk.ac.ebi.age.classif.Classifier;
import uk.ac.ebi.age.classif.ClassifierDB;
import uk.ac.ebi.age.classif.Tag;
import uk.ac.ebi.age.classif.exception.ClassifierExistsException;
import uk.ac.ebi.age.classif.exception.ClassifierNotFoundException;
import uk.ac.ebi.age.classif.exception.TagException;
import uk.ac.ebi.age.classif.exception.TagNotFoundException;
import uk.ac.ebi.age.classif.impl.ClassifierBean;
import uk.ac.ebi.age.classif.impl.TagBean;
import uk.ac.ebi.age.ext.authz.SystemAction;

import com.pri.util.Random;
import com.pri.util.collection.ListFragment;

public class TestAuthDBImpl implements AuthDB, ClassifierDB
{
 private List<UserBean> userList;
 private List<GroupBean> groupList;
 private List<ProfileBean> profileList;
 private List<ClassifierBean> classifierList = new ArrayList<ClassifierBean>();
 
 public TestAuthDBImpl()
 {
  groupList = new ArrayList<GroupBean>(20);
  
  for( int i=1; i <= 13; i++ )
  {
   GroupBean u = new GroupBean();
   
   u.setId("Group"+i);
   u.setDescription("Test Group №"+i);
   
   groupList.add(u);

  }

  userList = new ArrayList<UserBean>(200);
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
   
   userList.add(u);
  }

  profileList = new ArrayList<ProfileBean>();
  
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

   profileList.add(pb);
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
  
  
  classifierList.add(cb);
 }

 @Override
 public AuthDBSession createSession()
 {
  // TODO Auto-generated method stub
  throw new dev.NotImplementedYetException();
  //return null;
 }

 @Override
 public ClassifierBean getClassifier(String id)
 {
  for( ClassifierBean u : classifierList )
  {
   if( id.equals(u.getId()) )
    return u;
  }
  
  return null;
 }

 @Override
 public Tag getTag(String clsfId, String tagId) throws TagException
 {
  Classifier c = getClassifier(clsfId);
  
  if( c == null )
   throw new ClassifierNotFoundException();
   
  return c.getTag(tagId);
 }

 @Override
 public User getUser(String id)
 {
  for( UserBean u : userList )
  {
   if( id.equals(u.getId()) )
    return u;
  }
  
  return null;
 }

 @Override
 public GroupBean getUserGroup(String id)
 {
  for( GroupBean u : groupList )
  {
   if( id.equals(u.getId()) )
    return u;
  }
  
  return null;
 }

 @Override
 public ProfileBean getProfile(String id)
 {
  for( ProfileBean u : profileList )
  {
   if( id.equals(u.getId()) )
    return u;
  }
  
  return null;
 }
 
 @Override
 public List<? extends User> getUsers(int begin, int end)
 {
  int to = end!=-1 && end <= userList.size() ?end:userList.size();
  
  return userList.subList(begin, to);
 }

 @Override
 public ListFragment<User> getUsers(String idPat, String namePat, int begin, int end)
 {
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
 public int getUsersTotal()
 {
  return userList.size();
 }

 @Override
 public void updateUser(String userId, String userName, String userPass) throws AuthException
 {
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
 public void addUser(String userId, String userName, String userPass) throws AuthException
 {
  if( getUser(userId) != null )
   throw new UserExistsException();
  
  UserBean u = new UserBean();
  
  u.setId(userId);
  u.setName(userName);
  u.setPass(userPass);
  
  userList.add( u );
 }

 @Override
 public void deleteUser(String userId) throws AuthException
 {
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
 public List< ? extends UserGroup> getGroups(int begin, int end)
 {
  int to = end!=-1 && end <= groupList.size() ?end:groupList.size();
  
  return groupList.subList(begin, to);
 }

 @Override
 public ListFragment<UserGroup> getGroups(String idPat, String namePat, int begin, int end)
 {
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
 public int getGroupsTotal()
 {
  return groupList.size();
 }

 @Override
 public void deleteGroup(String grpId) throws AuthException
 {
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
 public void addGroup(String grpId, String grpDesc) throws AuthException
 {

  if( getUserGroup(grpId) != null  )
   throw new GroupExistsException();
  
  GroupBean u = new GroupBean();
  
  u.setId(grpId);
  u.setDescription(grpDesc);
  
  groupList.add( u );
 }

 @Override
 public void updateGroup(String grpId, String grpDesc) throws AuthException
 {
  GroupBean u = getUserGroup(grpId);
  
  if( u == null  )
   throw new GroupNotFoundException();

  u.setDescription(grpDesc);
  
 }

 @Override
 public Collection< ? extends UserGroup> getGroupsOfUser(String userId) throws AuthException
 {
  User u = getUser(userId);
  
  if( u == null  )
   throw new UserNotFoundException();
  
  return u.getGroups();
 }

 @Override
 public void removeUserFromGroup(String grpId, String userId) throws AuthException
 {
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
 public void removeGroupFromGroup(String grpId, String partId) throws AuthException
 {
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
 public void addUserToGroup(String grpId, String userId) throws AuthException
 {
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
 public Collection< ? extends User> getUsersOfGroup(String groupId) throws AuthException
 {
  GroupBean gb = getUserGroup(groupId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  return gb.getUsers();
 }

 @Override
 public Collection< ? extends UserGroup> getGroupsOfGroup(String groupId) throws AuthException
 {
  GroupBean gb = getUserGroup(groupId);
  
  if( gb == null )
   throw new GroupNotFoundException();

  return gb.getGroups();
 }

 @Override
 public void addGroupToGroup(String grpId, String partId) throws AuthException
 {
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
 public void addProfile(String profId, String dsc) throws AuthException
 {
  if( getProfile(profId) != null )
   throw new ProfileExistsException();
  
  ProfileBean pb = new ProfileBean();
  
  pb.setId(profId);
  pb.setDescription(dsc);
  
  profileList.add( pb );
 }

 @Override
 public void updateProfile(String profId, String dsc) throws AuthException
 {
  ProfileBean pf =getProfile(profId);
  
  if( pf == null )
   throw new ProfileNotFoundException();
  
  pf.setDescription(dsc);
 }

 @Override
 public void deleteProfile(String profId) throws AuthException
 {
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
 public List< ? extends PermissionProfile> getProfiles(int begin, int end)
 {
  int to = end!=-1 && end <= profileList.size() ?end:profileList.size();
  
  return profileList.subList(begin, to);
 }

 @Override
 public ListFragment<PermissionProfile> getProfiles(String idPat, String namePat, int begin, int end)
 {
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
 public int getProfilesTotal()
 {
  return profileList.size();
 }

 @Override
 public void addPermissionToProfile(String profId, SystemAction actn, boolean allow) throws AuthException
 {
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
 public Collection< ? extends Permission> getPermissionsOfProfile(String profId) throws AuthException
 {
  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();


  return prof.getPermissions();
 }

 @Override
 public Collection< ? extends PermissionProfile> getProfilesOfProfile(String profId) throws AuthException
 {
  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();


  return prof.getProfiles();
 }

 
 @Override
 public void removePermissionFromProfile(String profId, SystemAction actn, boolean allow) throws AuthException
 {
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
 public void removeProfileFromProfile(String profId, String toRemProf) throws AuthException
 {
  ProfileBean prof =getProfile(profId);
  
  if( prof == null )
   throw new ProfileNotFoundException();

  PermissionProfile rmProf = getProfile(toRemProf);
  
  if( rmProf == null )
   throw new ProfileNotFoundException();
 
  prof.removeProfile( rmProf );
 }


 @Override
 public void addProfileToProfile(String profId, String toAdd) throws AuthException
 {
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
 public void deleteClassifier(String csfId) throws TagException
 {
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
 public void addClassifier(String csfId, String csfDesc) throws TagException
 {
  if( getClassifier(csfId) != null )
   throw new ClassifierExistsException();
  
  ClassifierBean cb = new ClassifierBean();
  
  cb.setId( csfId );
  cb.setDescription(csfDesc);
  
  classifierList.add(cb);
 }

 @Override
 public void updateClassifier(String csfId, String csfDesc) throws TagException
 {
  ClassifierBean cb = getClassifier(csfId);
  
  if( cb == null )
   throw new ClassifierNotFoundException();

  cb.setDescription(csfDesc);
  
 }

 @Override
 public List< ? extends Classifier> getClassifiers(int begin, int end)
 {
  return classifierList;
 }

 @Override
 public int getClassifiersTotal()
 {
  return classifierList.size();
 }

 @Override
 public ListFragment<Classifier> getClassifiers(String idPat, String namePat, int begin, int end)
 {
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
 public void removeTagFromClassifier(String clsId, String tagId) throws TagException
 {
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
 public void addTagToClassifier(String clsId, String tagId, String description, String parentTagId) throws TagException
 {
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
 public void updateTag(String clsId, String tagId, String desc, String parentTagId) throws TagException
 {
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
 public Collection< ? extends Tag> getTagsOfClassifier(String clsId) throws TagException
 {
  ClassifierBean clsb = getClassifier(clsId);
  
  if( clsb == null )
   throw new ClassifierNotFoundException();
  
  return clsb.getTags();
 }

 @Override
 public Collection< ? extends Tag> getTagsOfClassifier(String clsId, final String parentTagId) throws TagException
 {
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



}
