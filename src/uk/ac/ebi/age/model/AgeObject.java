package uk.ac.ebi.age.model;

import java.util.Collection;

/**
 @model
*/

public interface AgeObject extends AgeAbstractObject
{
 /** @model */
 String getId();
 String getOriginalId();

 AgeClass getAgeElClass();
 
 Collection<? extends AgeAttribute> getAttributes();
 Collection<? extends AgeRelation> getRelations();

 Collection<? extends AgeRelation> getRelations( AgeRelationClass cls );
 Collection<? extends AgeAttribute> getAttributes( AgeAttributeClass cls );
 Collection<String> getAttributeClassesIds();
 Collection<? extends AgeAttribute> getAttributesByClassId(String cid);
 Collection<? extends AgeAttribute> getAttributesByClass(AgeAttributeClass cid);
 
 Object getAttributeValue( AgeAttributeClass cls );
 
 int getOrder();

 Submission getSubmission();
 Collection< ? extends AgeAttributeClass> getAttributeClasses();
 

}
