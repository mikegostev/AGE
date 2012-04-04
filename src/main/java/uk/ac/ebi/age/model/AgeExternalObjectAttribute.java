package uk.ac.ebi.age.model;


public interface AgeExternalObjectAttribute extends AgeObjectAttribute, Resolvable
{

 public String getTargetObjectId();
 ResolveScope getTargetResolveScope();
}
