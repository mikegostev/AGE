package uk.ac.ebi.age.entity;

public class CommonID implements ID
{
 private EntityDomain domain;
 private String id;
 private ID parentObjectID;
 
 @Override
 public EntityDomain getDomain()
 {
  return domain;
 }

 @Override
 public String getId()
 {
  return id;
 }

 @Override
 public ID getParentObjectID()
 {
  return parentObjectID;
 }

 public void setDomain(EntityDomain domain)
 {
  this.domain = domain;
 }

 public void setId(String id)
 {
  this.id = id;
 }

 public void setParentObjectID(ID parentObjectID)
 {
  this.parentObjectID = parentObjectID;
 }

}
