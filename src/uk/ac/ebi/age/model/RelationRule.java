package uk.ac.ebi.age.model;

import java.util.Collection;

public interface RelationRule
{
 RestrictionType getRestrictionType();

 int getCardinality();

 Cardinality getCardinalityType();

 boolean isQualifiersUnique();

 boolean isRelationSubclassesIncluded();

 RestrictionType getType();

 boolean isSubclassesIncluded();

 QualifiersCondition getQualifiersCondition();

 AgeRelationClass getRelationClass();

 AgeClass getTargetClass();

 Collection<QualifierRule> getQualifiers();

 int getRuleId();
}
