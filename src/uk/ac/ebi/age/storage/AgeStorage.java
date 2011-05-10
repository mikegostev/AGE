package uk.ac.ebi.age.storage;

import java.io.File;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.index.AgeIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;

public interface AgeStorage
{
 Collection<AgeObject> executeQuery( AgeQuery qury );
 
 List<AgeObject> queryTextIndex(AgeIndex idx, String query);
 int queryTextIndexCount(AgeIndex idx, String query);
 
// AgeIndex createTextIndex(AgeQuery qury, TextValueExtractor cb );
 AgeIndex createTextIndex(AgeQuery qury, Collection<TextFieldExtractor> cb );

 SemanticModel getSemanticModel();
 
 void shutdown();

 public Collection<? extends AgeObject> getAllObjects();
 public AgeObject getGlobalObject(String objID);
 public AgeObject getClusterObject(String clustId, String objID);

// AgeObject getObjectById(String grpID);

// boolean hasObject(String id);
 boolean hasDataModule(String id);

 void addDataChangeListener(DataChangeListener dataChangeListener);
 
 File getAttachment(String id, String clustId, boolean global);
 File getAttachmentBySysRef(String sysid);
 
 DataModule getDataModule(String name);

 Collection<? extends DataModule> getDataModules();

 boolean isFileIdGlobal(String fileID);

}