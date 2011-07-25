package uk.ac.ebi.age.model.impl.v3;

import uk.ac.ebi.age.entity.EntityDomain;
import uk.ac.ebi.age.entity.ID;
import uk.ac.ebi.age.model.AgeObject;

public class AgeObjectID implements ID
{
 private AgeObject obj;
 
 public AgeObjectID(AgeObject ageObjectImpl)
 {
  obj=ageObjectImpl;
 }

 @Override
 public EntityDomain getDomain()
 {
  return EntityDomain.AGEOBJECT;
 }

 @Override
 public String getId()
 {
  StringBuilder sb = new StringBuilder(100);
 
  String xid = obj.getDataModule().getClusterId();
  
  sb.append( xid.length() ).append(xid);

  xid = obj.getDataModule().getId();
  
  sb.append( xid.length() ).append(xid);
  
  xid = obj.getId();
  
  sb.append( xid.length() ).append(xid);
  
  return sb.toString();
 }

 @Override
 public ID getParentObjectID()
 {
  return obj.getDataModule().getEntityID();
 }

}
