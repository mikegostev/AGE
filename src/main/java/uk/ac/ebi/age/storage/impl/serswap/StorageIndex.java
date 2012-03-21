package uk.ac.ebi.age.storage.impl.serswap;

import java.io.Serializable;

import uk.ac.ebi.age.storage.impl.serswap.v3.StoragePlug;

class StorageIndex implements Serializable
{
 private StoragePlug storagePlug;

 public StoragePlug getStoragePlug()
 {
  return storagePlug;
 }

 public void setStoragePlug(StoragePlug storagePlug)
 {
  this.storagePlug = storagePlug;
 }
}
