package uk.ac.ebi.age.storage;

import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;

public class AgeStorageManager
{
 public enum DB_TYPE
 {
  AgeDB("uk.ac.ebi.age.storage.impl.ser.SerializedStorage");
  
  private DB_TYPE( String cls )
  {
   className=cls;
  }
  
  public String getClassName()
  {
   return className;
  }
  
  private String className;
 }

 public static AgeStorageAdm createInstance(DB_TYPE agedb, String dbPath) throws StorageInstantiationException
 {
  try
  {
   Class<?> storClass = Thread.currentThread().getContextClassLoader().loadClass(agedb.getClassName());
   
   AgeStorageAdm stor = (AgeStorageAdm)storClass.newInstance();
   
   stor.init(dbPath);
   
   return stor;
   
  }
  catch( Exception e)
  {
   throw new StorageInstantiationException( "Can't instantiate storage engine: "+e.getMessage(), e );
  }

 }
}
