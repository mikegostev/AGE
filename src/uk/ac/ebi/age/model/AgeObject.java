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
 
 AgeClass getAgeElClass();
 
 Collection<? extends AgeAttribute> getAttributes();
 Collection<? extends AgeRelation> getRelations();
 <T extends AgeRelation> Map<AgeRelationClass, Collection<T>> getRelationsMap();


 AgeAttribute getAttribute(AgeAttributeClass attrCls);

 int getOrder();

}
