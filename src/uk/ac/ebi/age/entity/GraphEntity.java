package uk.ac.ebi.age.entity;

public class GraphEntity implements Entity
{
 private static GraphEntity instance = new GraphEntity(); 
 
 private GraphEntity()
 {}

 public static Entity getInstance()
 {
  return instance;
 }
 
 @Override
 public String getEntityID()
 {
  return "";
 }

 @Override
 public EntityDomain getEntityDomain()
 {
  return EntityDomain.GRAPH;
 }

 @Override
 public Entity getParentEntity()
 {
  return null;
 }

}
