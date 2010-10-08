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

 AgeClassWritable createCustomAgeClass(String name, String pfx);
 AgeAttributeClassWritable createCustomAgeAttributeClass(String name, DataType type, AgeClass blkCls);
 AgeRelationClassWritable createCustomAgeRelationClass(String name, AgeClass range, AgeClass owner);

 AgeClassProperty getDefinedAgeClassProperty( String name );

 SubmissionWritable createSubmission();

 void setMasterModel(SemanticModel newModel);

}
