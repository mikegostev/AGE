package uk.ac.ebi.age.model;

import java.util.Collection;
import java.util.Map;

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
 Map<AgeAttributeClass, Collection<AgeAttribute> > getAttributeMap();

 Object getAttributeValue( AgeAttributeClass cls );
 
 int getOrder();

 Submission getSubmission();

}
