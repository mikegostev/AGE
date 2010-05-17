package uk.ac.ebi.age.model;

public interface MaxCardinalityRestriction extends AgeRestriction
{
 AgeRelationClass getAgeRelationClass();
 AgeRestriction getFiller();
 
 int getCardinality();
}
