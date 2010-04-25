package uk.ac.ebi.age.model;

public interface AgeRelation
{
 AgeObject getTargetObject();
 AgeRelationClass getRelationClass();
 
 int getOrder();
}
