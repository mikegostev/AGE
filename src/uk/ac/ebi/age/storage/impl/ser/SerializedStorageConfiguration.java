package uk.ac.ebi.age.storage.impl.ser;

import java.io.File;

public class SerializedStorageConfiguration
{
 private File storageBaseDir;
 private long maintenanceModeTimeout;
 private boolean master;

 public File getStorageBaseDir()
 {
  return storageBaseDir;
 }

 public void setStorageBaseDir(File storageBaseDir)
 {
  this.storageBaseDir = storageBaseDir;
 }

 public long getMaintenanceModeTimeout()
 {
  return maintenanceModeTimeout;
 }

 public void setMaintenanceModeTimeout(long maintenanceModeTimeout)
 {
  this.maintenanceModeTimeout = maintenanceModeTimeout;
 }

 public boolean isMaster()
 {
  return master;
 }

 public void setMaster(boolean master)
 {
  this.master = master;
 }

}
