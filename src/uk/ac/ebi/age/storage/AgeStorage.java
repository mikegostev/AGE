package uk.ac.ebi.age.storage;

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.storage.index.AgeIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;

public interface AgeStorage
{
 Collection<AgeObject> executeQuery( AgeQuery qury );
 
 List<AgeObject> queryTextIndex(AgeIndex idx, String query);
 
// AgeIndex createTextIndex(AgeQuery qury, TextValueExtractor cb );
 AgeIndex createTextIndex(AgeQuery qury, Collection<TextFieldExtractor> cb );

 SemanticModel getSemanticModel();
 
 void shutdown();

 AgeObject getObjectById(String grpID);
 boolean hasObject(String id);

 void addDataChangeListener(DataChangeListener dataChangeListener);
}
