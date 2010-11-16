package uk.ac.ebi.age.storage;

import java.util.Collection;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;
import uk.ac.ebi.age.storage.exeption.SubmissionStoreException;

public interface AgeStorageAdm extends AgeStorage
{
 String storeSubmission(SubmissionWritable sbm) throws RelationResolveException, SubmissionStoreException;
 
 boolean updateSemanticModel( SemanticModel sm, LogNode log ); // throws ModelStoreException;

 void init( String initStr) throws StorageInstantiationException;
 void shutdown();

 void lockWrite();
 void unlockWrite();

 void addRelations(String key, Collection<AgeRelationWritable> value);

 SubmissionWritable getSubmission(String name);


}
