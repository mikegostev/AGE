package uk.ac.ebi.age.model;

import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;

public interface ContextSemanticModel extends SemanticModel
{
 SubmissionContext getContext();

 AgeClass getDefinedAgeClass(String name);
 AgeClass getCustomAgeClass(String name);

 AgeRelationClass getCustomAgeRelationClass(String name);
 AgeAttributeClass getCustomAgeAttributeClass(String name, AgeClass blkCls);

 AgeClassWritable getOrCreateCustomAgeClass(String name, String pfx, AgeClass parent);
 AgeAttributeClassWritable getOrCreateCustomAgeAttributeClass(String name, DataType type, AgeClass owner, AgeAttributeClass superClass);
 AgeRelationClassWritable getOrCreateCustomAgeRelationClass(String name, AgeClass range, AgeClass owner, AgeRelationClass superClass);

 AgeClassProperty getDefinedAgeClassProperty( String name );

 SubmissionWritable createSubmission();

 SemanticModel getMasterModel();
 void setMasterModel(SemanticModel newModel);

}
