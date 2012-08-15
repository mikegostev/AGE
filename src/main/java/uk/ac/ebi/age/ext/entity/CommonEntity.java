package uk.ac.ebi.age.ext.entity;

import java.io.Serializable;

public class CommonEntity implements Entity, Serializable
{

 private static final long serialVersionUID = 1L;

 private String id;
 private Entity parentEntity;
 private EntityDomain domain;

 CommonEntity()
 {}

 public CommonEntity( Entity ce, String id, EntityDomain dom )
 {
  this.id=id;
  parentEntity = ce;
  domain = dom;
 }
 
 @Override
 public String getEntityID()
 {
  return id;
 }

 @Override
 public EntityDomain getEntityDomain()
 {
  return domain;
 }

 @Override
 public Entity getParentEntity()
 {
  return parentEntity;
 }

 public void setEntityId(String id)
 {
  this.id = id;
 }

}
