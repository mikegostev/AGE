package uk.ac.ebi.age.model;

import uk.ac.ebi.age.model.writable.SubmissionWritable;

public interface ContextSemanticModel extends SemanticModel
{

 SubmissionContext getContext();

 AgeClass getDefinedAgeClass(String name);
 AgeClass getCustomAgeClass(String name);

 AgeRelationClass getCustomAgeRelationClass(String name);
 AgeAttributeClass getCustomAgeAttributeClass(String name, AgeClass blkCls);

 AgeAttributeClass createCustomAgeAttributeClass(String name, DataType type, AgeClass blkCls);

 AgeClassProperty getDefinedAgeClassProperty( String name );

 SubmissionWritable createSubmission();

 void setMasterModel(SemanticModel newModel);

}
