package uk.ac.ebi.age.entity;

public interface Entity
{
 String getEntityID();
 EntityDomain getEntityDomain();
 Entity getParentEntity();
}
