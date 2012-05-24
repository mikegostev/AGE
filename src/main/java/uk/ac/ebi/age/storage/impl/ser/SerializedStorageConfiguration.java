package uk.ac.ebi.age.storage.impl.ser;

import java.io.File;

public class SerializedStorageConfiguration
{
 public static String ramIndexDir = "<<ram>>";

 private File storageBaseDir;
 private long maintenanceModeTimeout;
 private boolean master;
 private String indexDir;

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

 public String getIndexDir()
 {
  return indexDir;
 }

 public void setIndexDir(String indexDir)
 {
  this.indexDir = indexDir;
 }

}
