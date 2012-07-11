package uk.ac.ebi.age.model;


public interface AgeRelation extends AgeObjectProperty, Attributed
{
 AgeRelationClass getAgeElClass();
 RelationClassRef getClassReference();
 
 AgeObject getSourceObject();
 AgeObject getTargetObject();

 String getTargetObjectId();

 AgeRelation getInverseRelation();
 
 boolean isInferred();
}
