package uk.ac.ebi.age.authz.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.age.authz.AuthDB;
import uk.ac.ebi.age.authz.AuthDBSession;
import uk.ac.ebi.age.authz.AuthException;
import uk.ac.ebi.age.authz.GroupCycleException;
import uk.ac.ebi.age.authz.GroupNotFoundException;
import uk.ac.ebi.age.authz.User;
import uk.ac.ebi.age.authz.UserGroup;
import uk.ac.ebi.age.authz.UserNotFoundException;

import com.pri.util.Random;
import com.pri.util.collection.ListFragment;

public class H2AuthDBImpl implements AuthDB
{
 private List<UserBean> userList;
 private List<GroupBean> groupList;
 
 public H2AuthDBImpl()
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
  for( int i=1; i <= 130; i++ )
  {
   UserBean u = new UserBean();
   
   u.setId("User"+i);
   u.setName("Test User №"+i);
   
   int n = Random.randInt(1, 8);
   for( int j=0; j < n; j++ )
   {
    GroupBean grp = groupList.get( Random.randInt(0, groupList.size()-1) );
    u.addGroup( grp );
    grp.addUser( u );
   }
   
   userList.add(u);
  }

 }

 @Override
 public AuthDBSession createSession()
 {
  // TODO Auto-generated method stub
  throw new dev.NotImplementedYetException();
  //return null;
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
  for( UserBean u : userList )
  {
   if( userId.equals(u.getId()) )
    throw new AuthException();
  }
  
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
  for( GroupBean u : groupList )
  {
   if( grpId.equals(u.getId()) )
    throw new AuthException();
  }
  
  GroupBean u = new GroupBean();
  
  u.setId(grpId);
  u.setDescription(grpDesc);
  
  groupList.add( u );
 }

 @Override
 public void updateGroup(String grpId, String grpDesc) throws AuthException
 {
  for( GroupBean u : groupList )
  {
   if( u.getId().equals(grpId) )
   {
    if( grpDesc != null )
     u.setDescription(grpDesc);

    return;
   }
  }
  
  throw new GroupNotFoundException();
 }

 @Override
 public Collection< ? extends UserGroup> getGroupsOfUser(String userId) throws AuthException
 {
  for( UserBean u : userList )
  {
   if( userId.equals(u.getId()) )
    return u.getGroups();
  }
  
  throw new AuthException();
 }

 @Override
 public void removeUserFromGroup(String grpId, String userId) throws AuthException
 {
  GroupBean gb = null;
  
  for( GroupBean g : groupList )
  {
   if( grpId.equals(g.getId()) )
   {
    gb = g;
    break;
   }
  }
  
  if( gb == null )
   throw new AuthException();
  
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
   throw new AuthException();
  
  gb.removeUser( ub );
  ub.removeGroup( gb );
 }

 @Override
 public void removeGroupFromGroup(String grpId, String partId) throws AuthException
 {
  GroupBean gb = null;
  
  for( GroupBean g : groupList )
  {
   if( grpId.equals(g.getId()) )
   {
    gb = g;
    break;
   }
  }
  
  if( gb == null )
   throw new AuthException();
  
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
   throw new AuthException();
  
  gb.removeGroup( gp );
 }

 
 @Override
 public void addUserToGroup(String grpId, String userId) throws AuthException
 {
  GroupBean gb = null;
  
  for( GroupBean g : groupList )
  {
   if( grpId.equals(g.getId()) )
   {
    gb = g;
    break;
   }
  }
  
  if( gb == null )
   throw new AuthException();
  
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
   throw new AuthException();
  
  gb.addUser( ub );
  ub.addGroup( gb );
 }

 @Override
 public Collection< ? extends User> getUsersOfGroup(String groupId) throws AuthException
 {
  for( GroupBean g : groupList )
  {
   if( groupId.equals(g.getId()) )
    return g.getUsers();
  }
  
  throw new AuthException();
 }

 @Override
 public Collection< ? extends UserGroup> getGroupsOfGroup(String groupId) throws AuthException
 {
  for( GroupBean g : groupList )
  {
   if( groupId.equals(g.getId()) )
    return g.getGroups();
  }
  
  throw new AuthException();
 }

 @Override
 public void addGroupToGroup(String grpId, String partId) throws AuthException
 {
  GroupBean gb = null;
  
  for( GroupBean g : groupList )
  {
   if( grpId.equals(g.getId()) )
   {
    gb = g;
    break;
   }
  }
  
  GroupBean pb = null;
  
  for( GroupBean g : groupList )
  {
   if( partId.equals(g.getId()) )
   {
    pb = g;
    break;
   }
  }
  
  if( pb == null )
   throw new AuthException();

  if( grpId.equals(partId) || gb.isPartOf(pb) )
   throw new GroupCycleException();
  
  
  gb.addGroup( pb );
 }

}
