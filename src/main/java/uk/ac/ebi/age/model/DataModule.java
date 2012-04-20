package uk.ac.ebi.age.model;

import java.util.Collection;

import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.storage.AgeStorage;


public interface DataModule extends Entity
{
 public static interface AttributedSelector
 {
  boolean select( Attributed at );
 }

 AgeStorage getStorage();
 
 ModuleKey getModuleKey();
 
 String getId();
 String getClusterId();

 AgeObject getObject( String id );
 Collection<? extends AgeObject> getObjects();
 
 
 ContextSemanticModel getContextSemanticModel();
 
 Collection<? extends AgeExternalRelation> getExternalRelations();
 Collection<? extends AgeExternalObjectAttribute> getExternalObjectAttributes();
 Collection<AgeFileAttributeWritable> getFileAttributes();

 Collection<? extends Attributed> getAttributed( AttributedSelector sel );
}