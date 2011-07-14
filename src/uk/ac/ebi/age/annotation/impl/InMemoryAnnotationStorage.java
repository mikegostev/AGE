package uk.ac.ebi.age.annotation.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;

import uk.ac.ebi.age.annotation.AnnotationManager;
import uk.ac.ebi.age.annotation.DBInitException;
import uk.ac.ebi.age.annotation.Topic;
import uk.ac.ebi.age.entity.EntityDomain;
import uk.ac.ebi.age.entity.ID;

public class InMemoryAnnotationStorage implements AnnotationManager
{
 private static final String serialFileName = "annotdb.ser";

 private Map< Topic, Map<EntityDomain, Map<String,Serializable> > > annotMap;

 private ReadWriteLock lock = new ReentrantReadWriteLock();
 
 private FileResourceManager txManager;
 private String serialFileRelPath;
 private File serialFile;

 
 public InMemoryAnnotationStorage(FileResourceManager frm, String annRelPath) throws DBInitException
 {
  txManager=frm;
 
  serialFileRelPath = annRelPath+"/"+serialFileName;
  
  serialFile = new File(frm.getStoreDir(),serialFileRelPath);
  
  if( serialFile.exists() )
  {
   try
   {
    readData();
   }
   catch(IOException e)
   {
    throw new DBInitException(e);
   }
  }
  else
  {
   annotMap = new HashMap<Topic, Map<EntityDomain,Map<String,Serializable>>>();

   String txId;

   try
   {
    txId = txManager.generatedUniqueTxId();
    txManager.startTransaction(txId);
    OutputStream outputStream = txManager.writeResource(txId, serialFileRelPath);
    
    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
    
    oos.writeObject(annotMap);
    
    oos.close();
    
    txManager.commitTransaction(txId);
   }
   catch(Exception e)
   {
    throw new DBInitException(e);
   }

  }
 }
 
 @SuppressWarnings("unchecked")
 private void readData() throws IOException
 {
  FileInputStream fis = new FileInputStream(serialFile);
  ObjectInputStream ois = new ObjectInputStream( fis );
  
  try
  {
   annotMap = (Map< Topic, Map<EntityDomain, Map<String,Serializable> > >)ois.readObject();
  }
  catch(ClassNotFoundException e)
  {
   e.printStackTrace();
  }
  
  fis.close();
  
 }

 @Override
 public boolean addAnnotation(Topic tpc, ID objId, Serializable value)
 {
  try
  {
   lock.writeLock().lock();
   
   Map<EntityDomain, Map<String, Serializable>> tMap = annotMap.get(tpc);

   Map<String, Serializable> eMap = null;

   if(tMap == null)
   {
    tMap = new HashMap<EntityDomain, Map<String, Serializable>>();

    annotMap.put(tpc, tMap);

    eMap = new HashMap<String, Serializable>();

    tMap.put(objId.getDomain(), eMap);
   }
   else
   {
    eMap = tMap.get(objId.getDomain());

    if(eMap == null)
     eMap = new HashMap<String, Serializable>();

    tMap.put(objId.getDomain(), eMap);
   }

   eMap.put(objId.getId(), value);
   
   try
   {
    sync();
   }
   catch(Exception e)
   {
    try
    {
     readData();
    }
    catch(IOException e1)
    {
     e1.printStackTrace();
    }
    
    return false;
   }
  }
  finally
  {
   lock.writeLock().unlock();
  }
  
  return true;
 }


 @Override
 public Object getAnnotation(Topic tpc, ID objId)
 {
  try
  {
   lock.readLock().lock();
   
   Map<EntityDomain, Map<String,Serializable> >  tMap = annotMap.get(tpc);
   
   if( tMap == null )
    return null;
   
   Map<String,Serializable> eMap = tMap.get(objId.getDomain());
   
   if( eMap == null )
    return null;
   
   return eMap.get(objId.getId());

  }
  finally
  {
   lock.readLock().unlock();
  }
  
 }

 private void sync() throws ResourceManagerException, IOException
 {
  String txId;

  txId = txManager.generatedUniqueTxId();

  txManager.startTransaction(txId);

  txManager.moveResource(txId, serialFileRelPath, serialFileRelPath + "." + System.currentTimeMillis(), true);

  OutputStream outputStream = txManager.writeResource(txId, serialFileRelPath);

  ObjectOutputStream oos = new ObjectOutputStream(outputStream);

  oos.writeObject(annotMap);
  
  oos.close();

  txManager.commitTransaction(txId);
 }
}
