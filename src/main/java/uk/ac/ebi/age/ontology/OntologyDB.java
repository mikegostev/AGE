package uk.ac.ebi.age.ontology;

import java.util.List;

import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionalDB;

import com.pri.util.collection.ListFragment;

public interface OntologyDB extends TransactionalDB
{

 Ontology getOntology(ReadLock lock, String id);

 void deleteOntology(Transaction trn, String ontoId);

 void addOntology(Transaction trn, String ontoId, String ontoDesc, String ontoURL);

 void updateOntology(Transaction trn, String ontoId, String ontoDesc, String ontoURL);

 List< ? extends Ontology> getOntologies(ReadLock lck, int begin, int end);

 int getOntologiesTotal(ReadLock lck);

 ListFragment<Ontology> getOntologies(ReadLock lck, String string, String string2, int begin, int end);
 
// User getUser( ReadLock lock, String id );
// User getUserByEmail(ReadLock lck, String email);
// List< ? extends User> getUsers( ReadLock lock, int begin, int end);
// ListFragment<User> getUsers( ReadLock lock, String idPat, String namePat, int begin, int end);
//
// int getUsersTotal( ReadLock lock );
//
// void updateUser( Transaction trn, String userId, String userName, String email) throws AuthDBException;
// void setUserPassword(Transaction trn, String userId, String userPass) throws AuthDBException;
// boolean checkUserPassword(ReadLock lck, String userId, String userPass) throws AuthDBException;
//
// void addUser( Transaction trn, String userId, String userName, String email, String userPass) throws AuthDBException;
//
// void deleteUser( Transaction trn, String userId) throws AuthDBException;
}
