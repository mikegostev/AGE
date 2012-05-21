package uk.ac.ebi.age.model;


public interface AgeRelation extends AgeObjectProperty, Attributed
{
 AgeRelationClass getAgeElClass();
 RelationClassRef getClassReference();
 
 AgeObject getSourceObject();
 AgeObject getTargetObject();

 AgeRelation getInverseRelation();
 
 boolean isInferred();
}
