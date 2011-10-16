package uk.ac.ebi.age.entity;

public class ModuleEntity implements Entity
{
 private String id;
 private ClusterEntity clusterEntity;
 
 public ModuleEntity( ClusterEntity ce, String id )
 {
  this.id=id;
  clusterEntity = ce;
 }
 
 @Override
 public String getEntityID()
 {
  return id;
 }

 @Override
 public EntityDomain getEntityDomain()
 {
  return EntityDomain.ATTACHMENT;
 }

 @Override
 public Entity getParentEntity()
 {
  return clusterEntity;
 }

 public void setEntityId(String id)
 {
  this.id = id;
 }

}
