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
import uk.ac.ebi.age.transaction.InconsistentStateException;
import uk.ac.ebi.age.transaction.InvalidStateException;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionException;

public class InMemoryAnnotationStorage implements AnnotationManager
{
 private static final String serialFileName = "annotdb.ser";

 private Map< Topic, Map<EntityDomain, Map<String,Serializable> > > annotMap;

 private ReadWriteLock lock = new ReentrantReadWriteLock();
 
 private FileResourceManager txManager;
 private String serialFileRelPath;
 private File serialFile;
 
 private ReadLock readLock = new ReadLock() {

  public void release()
  {
   releaseLock( this );
  }};
  
 private Transaction transaction  = new Transaction()
 {
  
  @Override
  public void release()
  {
   try
   {
    rollbackTransaction(this);
   }
   catch(TransactionException e)
   {
    e.printStackTrace();
   }
  }
 };
 
 private Thread lockOwner;
 private boolean dirty = false;
 
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
  
  Transaction t = startTransaction();
  
  addAnnotation(t, tpc, objId, value);
  
  try
  {
   commitTransaction(t);
  }
  catch(TransactionException e2)
  {
   return false;
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

 @Override
 public ReadLock getReadLock()
 {
  lock.readLock().lock();
  
  return readLock;
 }

 @Override
 public void releaseLock(ReadLock l)
 {
  lock.readLock().unlock();
 }

 @Override
 public Transaction startTransaction()
 {
  lock.writeLock().lock();

  lockOwner = Thread.currentThread();
  
  return transaction;
 }

 @Override
 public void commitTransaction(Transaction t) throws TransactionException
 {
  if( lockOwner != Thread.currentThread() )
   throw new InvalidStateException();
  
  
  try
  {
   if( ! dirty )
    return;

   sync();

   dirty = false;
  }
  catch(Exception e)
  {
   try
   {
    readData();
    dirty = false;
   }
   catch(IOException e1)
   {
    e1.printStackTrace();
   }
  }
  finally
  {
   lockOwner=null;
   lock.writeLock().unlock();
  }
 }

 @Override
 public void rollbackTransaction(Transaction t) throws TransactionException
 {
  if( lockOwner != Thread.currentThread() )
   throw new InvalidStateException();

  try
  {
   if( ! dirty )
    return;

   readData();
   dirty = false;
  }
  catch(IOException e1)
  {
   e1.printStackTrace();
   
   throw new InconsistentStateException("Transaction rollback failed", e1);
  }
  finally
  {
   lockOwner=null;
   lock.writeLock().unlock();
  }
  
 
 }

 @Override
 public boolean addAnnotation(Transaction trn, Topic tpc, ID objId, Serializable value)
 {
  if(lockOwner != Thread.currentThread())
   throw new InvalidStateException();

  dirty = true;
  
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

  return true;
 }
}
