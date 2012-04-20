package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.ModuleKey;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.storage.AgeStorage;

public interface DataModuleWritable extends DataModule
{
 void setId( String id );
 void setClusterId( String clstId );
 void setModuleKey(ModuleKey id);

 void setStorage(AgeStorage ageStorage);
 
 void addObject( AgeObjectWritable obj );

 AgeObjectWritable getObject( String id );
 Collection<AgeObjectWritable> getObjects();
 
 void setMasterModel(SemanticModel newModel);
 
 Collection<? extends AgeExternalRelationWritable> getExternalRelations();
 Collection<? extends AgeExternalObjectAttributeWritable> getExternalObjectAttributes();

 Collection<? extends AttributedWritable> getAttributed( AttributedSelector sel );

 
 void registerExternalRelation( AgeExternalRelationWritable rel );
 
 void pack();
}
