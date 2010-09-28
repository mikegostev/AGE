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
 
 Collection<? extends AgeRelation> getRelations();

 Collection<? extends AgeRelation> getRelations( AgeRelationClass cls );
 
 Object getAttributeValue( AgeAttributeClass cls );
 
 int getOrder();

 Submission getSubmission();

}
