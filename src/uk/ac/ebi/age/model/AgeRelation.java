package uk.ac.ebi.age.model;

public interface AgeRelation
{
 AgeObject getTargetObject();
 AgeRelationClass getAgeElClass();
 
 int getOrder();
}
