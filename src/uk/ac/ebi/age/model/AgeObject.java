package uk.ac.ebi.age.model;

import java.util.Collection;

/**
 @model
*/

public interface AgeObject extends AgeAbstractObject
{
 /** @model */
 String getId();
 
 AgeClass getAgeElClass();
 
 Collection<? extends AgeAttribute> getAttributes();
 Collection<? extends AgeRelation> getRelations();
 
 Collection<? extends AgeRelation> getRelations( AgeRelationClass cls );
 Collection<? extends AgeAttribute> getAttributes( AgeAttributeClass cls );

 Object getAttributeValue( AgeAttributeClass cls );
 
 int getOrder();

}
