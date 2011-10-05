package uk.ac.ebi.age.annotation.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;

import uk.ac.ebi.age.annotation.DBInitException;
import uk.ac.ebi.age.annotation.Topic;
import uk.ac.ebi.age.entity.Entity;
import uk.ac.ebi.age.transaction.InconsistentStateException;
import uk.ac.ebi.age.transaction.InvalidStateException;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionException;

public class InMemoryAnnotationStorage extends AbstractAnnotationStorage
{
 private static final String serialFileName = "annotdb.ser";

 private Map< Topic, SortedMap<String,Serializable> > annotMap;

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
   annotMap = new HashMap<Topic, SortedMap<String,Serializable>>();

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
   annotMap = (Map<Topic, SortedMap<String,Serializable>>)ois.readObject();
  }
  catch(ClassNotFoundException e)
  {
   e.printStackTrace();
  }
  
  fis.close();
  
 }

 @Override
 public boolean addAnnotation(Topic tpc, Entity objId, Serializable value)
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
 public Object getAnnotation(Topic tpc, Entity objId, boolean recurs)
 {
  try
  {
   lock.readLock().lock();
   
   SortedMap<String,Serializable> tMap = annotMap.get(tpc);
   
   if( tMap == null )
    return null;
   
   Entity cEnt = objId;
   
   do
   {
    String id = createEntityId(cEnt);
    
    Object annt = tMap.get(id);
    
    if( annt != null || ! recurs )
     return annt;
    
    cEnt = cEnt.getParentEntity();
   }
   while( cEnt != null );
   
   return null;
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

  txManager.moveResource(txId, serialFileRelPath, serialFileRelPath + ".bak", true);

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
 public boolean addAnnotation(Transaction trn, Topic tpc, Entity objId, Serializable value)
 {
  if(lockOwner != Thread.currentThread())
   throw new InvalidStateException();

  dirty = true;
  
  SortedMap<String, Serializable> tMap = annotMap.get(tpc);

  if(tMap == null)
  {
   tMap = new TreeMap<String, Serializable>();

   annotMap.put(tpc, tMap);
  }


  tMap.put(createEntityId(objId), value);

  return true;
 }

 @Override
 public boolean removeAnnotation(Transaction trn, Topic tpc, Entity objId, boolean rec)
 {
  if(lockOwner != Thread.currentThread())
   throw new InvalidStateException();

  dirty = true;
  
  SortedMap<String, Serializable> tMap = annotMap.get(tpc);

  if(tMap == null)
   return false;

  String id = createEntityId(objId);
  
  if( ! rec )
   return tMap.remove(id) != null ;
  else
  {
   Map<String, Serializable> smp = tMap.tailMap(id);
   
   Iterator<String> keys = smp.keySet().iterator();
   
   boolean removed = false;
   
   while( keys.hasNext() )
   {
    String key = keys.next();
    
    if( key.startsWith(id) )
    {
     keys.remove();
    
     removed = true;
    }
    else
     break;
   }
  
   return removed;

  }

 }
}
