package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.storage.impl.serswap.SerializedSwapStorage;

public class StoragePlug
{
 private transient SerializedSwapStorage storage;

 public StoragePlug()
 {}

 public StoragePlug(SerializedSwapStorage serializedSwapStorage)
 {
  storage = serializedSwapStorage;
 }

 public SerializedSwapStorage getStorage()
 {
  return storage;
 }

 public void setStorage(SerializedSwapStorage storage)
 {
  this.storage = storage;
 }

}
