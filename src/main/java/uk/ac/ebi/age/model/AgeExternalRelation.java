package uk.ac.ebi.age.model;



public interface AgeExternalRelation extends AgeRelation, Resolvable
{
 String getTargetObjectId();
 
 AgeExternalRelation getInverseRelation();
 
 ResolveScope getTargetResolveScope();
}
