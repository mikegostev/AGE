package uk.ac.ebi.age.model;

public interface AgeRelation extends AgeObjectProperty
{
 AgeObject getTargetObject();
 AgeRelationClass getAgeElClass();
 
 int getOrder();
 
 boolean isInferred();
}
