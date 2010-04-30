package uk.ac.ebi.age.model;

import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;

public interface SemanticModel
{
 AgeClassWritable createAgeClass(String name, String id, String pfx);

 AgeRelationClassWritable createAgeRelationClass(String name, String id);

 AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type);

 
 AgeAttributeWritable createAgeAttribute(AgeObject ageObject, AgeAttributeClass attr);
 AgeAttributeWritable createAgeAttribute(AgeObject ageObjectImpl, AgeAttributeClass attrClass, String param);

 AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String val, AgeRelationClass relClass);

 AgeRelationWritable createAgeRelation(AgeObjectWritable targetObj, AgeRelationClass relClass);

 AgeObjectWritable createAgeObject(String id, AgeClass cls);

 
 AgeClass getDefinedAgeClass(String name);

 AgeRelationClass getDefinedAgeRelationClass(String name);

 AgeClassProperty getDefinedAgeClassProperty(String name);

// AgeRelationClass getAttributeAttachmentClass();
 
 AgeAttributeClass getDefinedAgeAttributeClass(String attrClass);

 
 ModelFactory getModelFactory();


// AgeClass getAgeClass(String clsName);
//
// AgeRelationClass getAgeRelationClass(String relClsClass);
//
// AgeAttributeClass getAgeAttributeClass(String attrClsName);


}
