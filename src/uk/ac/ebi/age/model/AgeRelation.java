package uk.ac.ebi.age.model;

public interface AgeRelation extends Attributed
{
 AgeObject getTargetObject();
 AgeRelationClass getAgeElClass();
 
 int getOrder();
 
 boolean isInferred();
}
