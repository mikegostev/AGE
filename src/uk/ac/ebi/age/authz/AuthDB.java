package uk.ac.ebi.age.authz;

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
}
