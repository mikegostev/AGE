package uk.ac.ebi.age.storage.impl.serswap;

import java.io.Serializable;


class StorageIndex implements Serializable
{
 private StoragePlug storagePlug;

// public StoragePlug getStoragePlug()
// {
//  return storagePlug;
// }

 public void setStoragePlug(StoragePlug storagePlug)
 {
  this.storagePlug = storagePlug;
 }
}
