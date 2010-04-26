package uk.ac.ebi.age.model;

import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

public interface SemanticModel
{
 AgeClass createAgeClass(String name, String pfx);

 AgeRelationClass createAgeRelationClass(String name);

 AgeAttributeClass createAgeAttributeClass(String name, DataType type);

 
 AgeAttributeWritable createAgeAttribute(AgeObject ageObject, AgeAttributeClass attr);

 AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String val, AgeRelationClass relClass);

 AgeRelationWritable createAgeRelation(AgeObjectWritable targetObj, AgeRelationClass relClass);

 AgeObjectWritable createAgeObject(String id, AgeClass cls);

 
 AgeClass getDefinedAgeClass(String name);

 AgeRelationClass getDefinedAgeRelationClass(String name);

 AgeClassProperty getDefinedAgeClassProperty(String name);

 AgeRelationClass getAttributeAttachmentClass();
 
 AgeAttributeClass getDefinedAgeAttributeClass(String attrClass);

 
 ModelFactory getModelFactory();

// AgeClass getAgeClass(String clsName);
//
// AgeRelationClass getAgeRelationClass(String relClsClass);
//
// AgeAttributeClass getAgeAttributeClass(String attrClsName);


}
