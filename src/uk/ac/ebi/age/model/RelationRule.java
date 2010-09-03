package uk.ac.ebi.age.model;

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
}
