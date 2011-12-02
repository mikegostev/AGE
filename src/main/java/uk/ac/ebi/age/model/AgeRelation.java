package uk.ac.ebi.age.model;


public interface AgeRelation extends AgeObjectProperty, Attributed
{
 AgeObject getSourceObject();
 AgeObject getTargetObject();

 AgeRelationClass getAgeElClass();
 <IR extends AgeRelation> IR getInverseRelation(); 
 
 boolean isInferred();
}
