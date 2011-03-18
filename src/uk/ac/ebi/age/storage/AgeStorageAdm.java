package uk.ac.ebi.age.storage;

import java.io.File;
import java.util.Collection;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.storage.exeption.AttachmentIOException;
import uk.ac.ebi.age.storage.exeption.ModuleStoreException;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;

public interface AgeStorageAdm extends AgeStorage
{
// void storeDataModule(DataModuleWritable sbm) throws RelationResolveException, ModuleStoreException;
 void update(Collection<DataModuleWritable> modListToIns, Collection<String> modListToDel) throws RelationResolveException, ModuleStoreException;
// void removeDataModule(Collection<String> ids);

 boolean updateSemanticModel( SemanticModel sm, LogNode log ); // throws ModelStoreException;

 void init( String initStr) throws StorageInstantiationException;
 void shutdown();

 void lockWrite();
 void unlockWrite();

 void addRelations(String key, Collection<AgeRelationWritable> value);
 void removeRelations(String id, Collection<AgeRelationWritable> value);

 DataModuleWritable getDataModule(String name);
 Collection<? extends DataModuleWritable> getDataModules();

 void setMaster(boolean master);

 String makeGlobalFileID(String id);
 String makeLocalFileID(String id, String clustID);
 
 boolean deleteAttachment(String id);
 File storeAttachment(String id, File aux) throws AttachmentIOException;
 void renameAttachment(String id, String id2) throws AttachmentIOException;

}
