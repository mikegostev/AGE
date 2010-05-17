package uk.ac.ebi.age.model;

public interface ExactCardinalityRestriction extends AgeRestriction
{
 AgeRelationClass getAgeRelationClass();
 AgeRestriction getFiller();
 
 int getCardinality();
}
