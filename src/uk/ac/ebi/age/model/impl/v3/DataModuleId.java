package uk.ac.ebi.age.model.impl.v3;

import uk.ac.ebi.age.entity.EntityDomain;
import uk.ac.ebi.age.entity.ID;
import uk.ac.ebi.age.model.DataModule;

public class DataModuleId implements ID
{
 private DataModule dmod;

 private class ClusterId implements ID
 {
  @Override
  public EntityDomain getDomain()
  {
   return EntityDomain.CLUSTER;
  }

  @Override
  public String getId()
  {
   return dmod.getClusterId();
  }

  @Override
  public ID getParentObjectID()
  {
   return null;
  }
  
 }
 
 public DataModuleId(DataModuleImpl dataModuleImpl)
 {
  dmod = dataModuleImpl;
 }

 @Override
 public EntityDomain getDomain()
 {
  return EntityDomain.DATAMODULE;
 }

 @Override
 public String getId()
 {
  StringBuilder sb = new StringBuilder(100);
  
  String xid = dmod.getClusterId();
  
  sb.append( xid.length() ).append(xid);

  xid = dmod.getId();
  
  sb.append( xid.length() ).append(xid);
  
  
  return sb.toString();
 }

 @Override
 public ID getParentObjectID()
 {
  return new ClusterId();
 }

}
