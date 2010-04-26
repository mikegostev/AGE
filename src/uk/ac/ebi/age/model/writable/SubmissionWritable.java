package uk.ac.ebi.age.model.writable;

import java.util.Collection;

import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.Submission;

public interface SubmissionWritable extends Submission
{
 void setId( String id );
 void setDescription( String des );
 
 
// void addClass(AgeClass cls);
 void addObject( AgeObjectWritable obj );

 Collection<AgeObjectWritable> getObjects();
 
 void setMasterModel(SemanticModel newModel);
}
