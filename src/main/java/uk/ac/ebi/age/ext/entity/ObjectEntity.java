package uk.ac.ebi.age.ext.entity;

import java.io.Serializable;

public class ObjectEntity implements Entity, Serializable
{

 private static final long serialVersionUID = 1L;

 private String id;
 private ModuleEntity moduleEntity;

 ObjectEntity()
 {}

 public ObjectEntity( ModuleEntity ce, String id )
 {
  this.id=id;
  moduleEntity = ce;
 }
 
 @Override
 public String getEntityID()
 {
  return id;
 }

 @Override
 public EntityDomain getEntityDomain()
 {
  return EntityDomain.AGEOBJECT;
 }

 @Override
 public Entity getParentEntity()
 {
  return moduleEntity;
 }

 public void setEntityId(String id)
 {
  this.id = id;
 }

}
