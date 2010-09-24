package uk.ac.ebi.age.model;

import java.util.Collection;

public interface AttributeAttachmentRule
{
 RestrictionType getRestrictionType();

 AgeAttributeClass getAttributeClass();

 int getCardinality();

 Cardinality getCardinalityType();

 QualifiersCondition getQualifiersCondition();

 boolean isQualifiersUnique();

 boolean isSubclassesIncluded();

 RestrictionType getType();

 boolean isValueUnique();

 Collection<QualifierRule> getQualifiers();


}
