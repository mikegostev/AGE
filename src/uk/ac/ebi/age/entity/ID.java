package uk.ac.ebi.age.entity;


public interface ID
{
 EntityDomain getDomain();
 
 String getId();
 
 ID getParentObjectID();
}
