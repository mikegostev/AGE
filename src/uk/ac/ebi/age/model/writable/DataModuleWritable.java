package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.DataModule;

public interface DataModuleWritable extends DataModule
{
 void setId( String id );
 void setDescription( String des );
 
 
// void addClass(AgeClass cls);
 void addObject( AgeObjectWritable obj );

 Collection<AgeObjectWritable> getObjects();
 
 void setMasterModel(SemanticModel newModel);
}
