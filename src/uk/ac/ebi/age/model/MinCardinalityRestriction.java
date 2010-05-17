package uk.ac.ebi.age.model;

public interface MinCardinalityRestriction extends AgeRestriction
{
 AgeRelationClass getAgeRelationClass();
 AgeRestriction getFiller();
 
 int getCardinality();
}
