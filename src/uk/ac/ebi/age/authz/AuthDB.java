package uk.ac.ebi.age.authz;

import java.util.List;

import com.pri.util.collection.ListFragment;

public interface AuthDB
{
 AuthDBSession createSession();

 List<User> getUsers(int begin, int end);
 ListFragment<User> getUsers(String idPat, String namePat, int begin, int end);

 int getUsersTotal();
 }
