package uk.ac.ebi.age.annotation.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;

import uk.ac.ebi.age.annotation.AnnotationDBInitException;
import uk.ac.ebi.age.annotation.Topic;
import uk.ac.ebi.age.ext.annotation.AnnotationDBException;
import uk.ac.ebi.age.ext.entity.Entity;
import uk.ac.ebi.age.transaction.InvalidStateException;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionException;
import uk.ac.ebi.mg.assertlog.Log;
import uk.ac.ebi.mg.assertlog.LogFactory;
import uk.ac.ebi.mg.rwarbiter.InvalidTokenException;
import uk.ac.ebi.mg.rwarbiter.RWArbiter;
import uk.ac.ebi.mg.rwarbiter.TokenFactory;
import uk.ac.ebi.mg.rwarbiter.TokenW;

import com.pri.util.ObjectRecycler;

public class H2AnnotationStorage extends AbstractAnnotationStorage
{
 private static long CACHE_DUMP_DELAY = 30000;
 
 private static Log log = LogFactory.getLog(H2AnnotationStorage.class);
 
 private static final String           serialFileName      = "annotdb.ser";
 private static final String           h2DbPath            = "h2db";

 private static final String           annotationDB        = "ANNOTATIONDB";
 private static final String           annotationTable     = "ANNOTATION";

 private static final String           selectAnnotationSQL = "SELECT data FROM " + annotationDB + '.' + annotationTable + " WHERE topic='";
 private static final String           deleteAnnotationSQL = "DELETE FROM " + annotationDB + '.' + annotationTable + " WHERE ";
 private static final String           insertAnnotationSQL = "MERGE INTO " + annotationDB + '.' + annotationTable + " (id,topic,data) VALUES (?,?,?)";
 private static final String           getDBVerSQL         = "SELECT LAST_MODIFICATION FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_CATALOG='" + h2DbPath.toUpperCase()
                                                             + "' AND TABLE_SCHEMA='" + annotationDB + "' AND TABLE_NAME='" + annotationTable + "'";

 private String                        connectionString;

 private Connection                    permConn;

 private AnnotationCache               cache;

 private ObjectRecycler<StringBuilder> sbRecycler          = new ObjectRecycler<StringBuilder>(3);

 private FileResourceManager           txManager;
 private String                        cacheFileRelPath;
 private File                          cacheFile;

 private AtomicBoolean                 cacheDirty          = new AtomicBoolean(false);

 private RWArbiter<TrnInfo>            arbiter             = new RWArbiter<TrnInfo>(new TokenFactory<TrnInfo>()
                                                           {
                                                            @Override
                                                            public TrnInfo createToken()
                                                            {
                                                             return new TrnInfo();
                                                            }
                                                           });

 private long lastUpdateTime;

 public H2AnnotationStorage(FileResourceManager frm, String annRelPath) throws AnnotationDBInitException
 {
  long startTime=0;
  
  File annotDir = new File(frm.getStoreDir(), annRelPath);

  try
  {
   Class.forName("org.h2.Driver");

   connectionString = "jdbc:h2:" + new File(annotDir, h2DbPath).getAbsolutePath();

   permConn = DriverManager.getConnection(connectionString, "sa", "");

   initAnnotationDb();

  }
  catch(Exception e)
  {
   e.printStackTrace();

   throw new AnnotationDBInitException(e);
  }

  txManager = frm;

  cacheFileRelPath = annRelPath + "/" + serialFileName;

  cacheFile = new File(frm.getStoreDir(), cacheFileRelPath);

  lastUpdateTime = System.currentTimeMillis();
  
  if(cacheFile.exists())
  {

   try
   {
    assert ( startTime = System.currentTimeMillis() ) != 0;

    FileInputStream fis = new FileInputStream(cacheFile);
    ObjectInputStream ois = new ObjectInputStream(fis);

    cache = (AnnotationCache) ois.readObject();

    fis.close();

    assert log.info("Cache read time: "+(System.currentTimeMillis()-startTime)+"ms");

    long ver = -1;
    Statement stmt;

    stmt = permConn.createStatement();

    ResultSet rst = stmt.executeQuery(getDBVerSQL);

    if(rst.next())
     ver = rst.getLong(1);
    else
     throw new SQLException("Can't get database version");

    stmt.close();

    if(ver != cache.getVerison())
     buildCache();
    
   }
   catch( AnnotationDBInitException ae )
   {
    throw ae;
   }
   catch(Exception e)
   {
    throw new AnnotationDBInitException(e);
   }
  }

  if( cache == null ) 
   buildCache();
 }

 private void buildCache() throws AnnotationDBInitException
 {
  long startTime=0;
  int annotCount=0;
  
  assert ( startTime = System.currentTimeMillis() ) != 0;
  
  cache = new AnnotationCache();

  Statement stmt;
  try
  {
   stmt = permConn.createStatement();

   ResultSet rst = stmt.executeQuery("SELECT * FROM " + annotationDB + '.' + annotationTable);

   boolean hasData = false;

   while(rst.next())
   {
    hasData = true;

    Topic tpc = Topic.valueOf(rst.getString("topic"));

    ObjectInputStream ois = new ObjectInputStream(rst.getBinaryStream("data"));

    Object ann = ois.readObject();

    cache.addAnnotation(tpc, rst.getString("id"), ann);
    
    assert ++annotCount > 0;
   }

   rst.close();

   rst = stmt.executeQuery(getDBVerSQL);

   if(rst.next())
    cache.setVerison(rst.getLong(1));
   else
    throw new Exception("Can't get database version");

   if(hasData)
    setCacheDirty();

   rst.close();
   stmt.close();
  }
  catch(Exception e)
  {
   throw new AnnotationDBInitException(e);
  }

  assert log.info("Cache build time: "+(System.currentTimeMillis()-startTime)+"ms Annotations: "+annotCount);
  
 }

 private void setCacheDirty()
 {
  cacheDirty.set(true);
 }

 private void setCacheDirty2()
 {
  if( cacheDirty.getAndSet(true) )
   return;
  
  new Thread()
  {
   @Override
   public void run()
   {
    setName("Annotation Db cache sync");
    
    while( true )
    {
     if( System.currentTimeMillis()-lastUpdateTime > CACHE_DUMP_DELAY )
     {
      TokenW wtok = arbiter.getWriteLock();
      
      try
      {
       syncCache();
       cacheDirty.set(false);
      }
      catch(Exception e)
      {
       log.error("Can't sync cache",e);
      }
      finally
      {
       try
       {
        arbiter.releaseLock(wtok);
       }
       catch(InvalidTokenException e)
       {
       }
      }
      
      return;
     }
     
     try
     {
      Thread.sleep(CACHE_DUMP_DELAY);
     }
     catch(InterruptedException e)
     {
     }
    }
   }
  }
  .start();
  
 }
 
 private void syncCache() throws ResourceManagerException, IOException, SQLException
 {
  long ver = -1;
  Statement stmt;

  stmt = permConn.createStatement();

  ResultSet rst = stmt.executeQuery(getDBVerSQL);

  if(rst.next())
   ver = rst.getLong(1);
  else
   throw new SQLException("Can't get database version");

  stmt.close();

  cache.setVerison(ver);
  
  String txId;

  txId = txManager.generatedUniqueTxId();

  txManager.startTransaction(txId);

  if( cacheFile.exists() )
   txManager.moveResource(txId, cacheFileRelPath, cacheFileRelPath + ".bak", true);

  OutputStream outputStream = txManager.writeResource(txId, cacheFileRelPath);

  ObjectOutputStream oos = new ObjectOutputStream(outputStream);

  oos.writeObject(cache);
  
  oos.close();

  txManager.commitTransaction(txId);
 }


 private Statement getStatement(TrnInfo ti) throws SQLException
 {
  if(ti.getStatement() != null)
   return ti.getStatement();

  Statement s = permConn.createStatement();

  ti.setStatement(s);

  return s;
 }

 private void initAnnotationDb() throws SQLException
 {
  Statement stmt = permConn.createStatement();

  stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + annotationDB);

  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + annotationDB + '.' + annotationTable + " ("
    + "id VARCHAR, topic VARCHAR, data BINARY, PRIMARY KEY (id,topic))");

  permConn.commit();

  stmt.close();
 }

 @Override
 public Object getAnnotation(Topic tpc, Entity objId, boolean recurs) throws AnnotationDBException
 {
  TrnInfo ti = arbiter.getReadLock();

  try
  {
   return getAnnotation(ti, tpc, objId, recurs);
  }
  finally
  {
   try
   {
    arbiter.releaseLock(ti);
   }
   catch(InvalidTokenException e)
   {
    e.printStackTrace();
   }
  }
 }

 @Override
 public Object getAnnotation(ReadLock lock, Topic tpc, Entity objId, boolean recurs) throws AnnotationDBException
 {
  if(!((TrnInfo) lock).isActive())
   throw new InvalidStateException();

  Entity cEnt = objId;

  do
  {
   String id = createEntityId(cEnt);

   Object annt = cache.getAnnotation(tpc, id);

   if(annt != null || !recurs)
    return annt;

   cEnt = cEnt.getParentEntity();
  } while(cEnt != null);

  return null;
 }
 
 public Object getAnnotationNC(ReadLock lock, Topic tpc, Entity objId, boolean recurs) throws AnnotationDBException
 {
  if(!((TrnInfo) lock).isActive())
   throw new InvalidStateException();

  StringBuilder sb = sbRecycler.getObject();

  if(sb == null)
   sb = new StringBuilder(200);

  Statement stmt = null;

  try
  {

   stmt = getStatement((TrnInfo) lock);

   Entity ce = objId;

   Object annot = null;

   sb.append(selectAnnotationSQL).append(tpc.name()).append("' AND id='");
   int pos = sb.length();

   do
   {
    sb.setLength(pos);
    appendEntityId(ce, sb, true, '\'', '\'');
    sb.append('\'');

    String req = sb.toString();

    ResultSet rst = stmt.executeQuery(req);

    try
    {

     if(!rst.next())
     {
      if(!recurs)
       return null;
      else
       ce = ce.getParentEntity();
     }
     else
     {
      ObjectInputStream ois = new ObjectInputStream(rst.getBinaryStream(1));

      annot = ois.readObject();

      break;
     }

    }
    finally
    {
     rst.close();
    }

   } while(ce != null);

   return annot;
  }
  catch(ClassNotFoundException e)
  {
   e.printStackTrace();
   throw new AnnotationDBException("Can't deserialize object", e);
  }
  catch(Exception e)
  {
   e.printStackTrace();
   throw new AnnotationDBException("System error", e);
  }
  finally
  {
   sb.setLength(0);
   sbRecycler.recycleObject(sb);
  }
 }

 @Override
 public boolean addAnnotation(Topic tpc, Entity objId, Serializable value) throws AnnotationDBException
 {
  TrnInfo ti = arbiter.getWriteLock();

  try
  {
   boolean res = addAnnotation(ti, tpc, objId, value);

   permConn.commit();

   return res;
  }
  catch(SQLException e)
  {
   e.printStackTrace();
   throw new AnnotationDBException("System error", e);
  }
  finally
  {
   try
   {
    arbiter.releaseLock(ti);
   }
   catch(InvalidTokenException e)
   {
    e.printStackTrace();
   }

  }

 }

 @Override
 public boolean addAnnotation(Transaction trn, Topic tpc, Entity objId, Serializable value) throws AnnotationDBException
 {
  if(!((TrnInfo) trn).isActive())
   throw new InvalidStateException();

  PreparedStatement pstmt = null;

  try
  {
   pstmt = permConn.prepareStatement(insertAnnotationSQL);

   String entId = createEntityId(objId);
   
   pstmt.setString(1, entId);
   pstmt.setString(2, tpc.name());

   ByteArrayOutputStream baos = new ByteArrayOutputStream();
   ObjectOutputStream oos = new ObjectOutputStream(baos);

   oos.writeObject(value);
   oos.close();

   pstmt.setBytes(3, baos.toByteArray());

   if( pstmt.executeUpdate() == 0 )
    return false;
   
   lastUpdateTime = System.currentTimeMillis();
   
   setCacheDirty();

   return cache.addAnnotation(tpc, entId, value);
  }
  catch(Exception e)
  {
   e.printStackTrace();
   throw new AnnotationDBException("System error", e);
  }
  finally
  {
   if(pstmt != null)
   {
    try
    {
     pstmt.close();
    }
    catch(SQLException e)
    {
     e.printStackTrace();
    }
   }
  }
 }

 @Override
 public boolean removeAnnotation(Transaction trn, Topic tpc, Entity objId, boolean rec) throws AnnotationDBException
 {
  if(!((TrnInfo) trn).isActive())
   throw new InvalidStateException();

  StringBuilder sb = sbRecycler.getObject();

  if(sb == null)
   sb = new StringBuilder(200);

  Statement stmt = null;

  try
  {

   stmt = getStatement((TrnInfo) trn);

   sb.append(deleteAnnotationSQL);

   if(tpc != null)
    sb.append("topic='").append(tpc.name()).append("' AND ");

   if(rec)
   {
    sb.append("id LIKE '");
    appendEntityId(objId, sb, true, '\'', '\'');
    sb.append("%'");
   }
   else
   {
    sb.append("id='");
    appendEntityId(objId, sb, true, '\'', '\'');
    sb.append('\'');
   }
   
   stmt.executeUpdate(sb.toString());

   lastUpdateTime = System.currentTimeMillis();
   setCacheDirty();
   
   return cache.removeAnnotation(tpc, createEntityId(objId), rec);
  }
  catch(Exception e)
  {
   e.printStackTrace();
   throw new AnnotationDBException("System error", e);
  }
  finally
  {
   sb.setLength(0);
   sbRecycler.recycleObject(sb);
  }
 }

 @Override
 public ReadLock getReadLock()
 {
  return arbiter.getReadLock();
 }

 @Override
 public void releaseLock(ReadLock l)
 {
  if(!((TrnInfo) l).isActive())
   throw new InvalidStateException();

  if(arbiter.isWriteToken((TrnInfo) l))
   throw new InvalidStateException("Use commit or rollback for transactions");

  try
  {
   arbiter.releaseLock((TrnInfo) l);
  }
  catch(InvalidTokenException e)
  {
   throw new InvalidStateException();
  }

  Statement s = ((TrnInfo) l).getStatement();

  try
  {
   if(s != null)
    s.close();
  }
  catch(SQLException e)
  {
   e.printStackTrace();
  }
 }

 @Override
 public Transaction startTransaction()
 {
  return arbiter.getWriteLock();
 }

 @Override
 public void commitTransaction(Transaction t) throws TransactionException
 {
  if(!((TrnInfo) t).isActive())
   throw new InvalidStateException();

  try
  {
   if(!((TrnInfo) t).isPrepared())
   {
    permConn.commit();
    return;
   }

   Statement s = getStatement((TrnInfo) t);

   s.executeUpdate("COMMIT TRANSACTION T1");
  }
  catch(SQLException e)
  {
   throw new TransactionException("Commit failed", e);
  }
  finally
  {
   try
   {
    arbiter.releaseLock((TrnInfo) t);
   }
   catch(InvalidTokenException e)
   {
    throw new TransactionException("Invalid token type", e);
   }

   Statement s = ((TrnInfo) t).getStatement();

   if(s != null)
   {
    try
    {
     s.close();
    }
    catch(SQLException e)
    {
     e.printStackTrace();
    }
   }
  }

 }

 @Override
 public void rollbackTransaction(Transaction t) throws TransactionException
 {
  if(!((TrnInfo) t).isActive())
   throw new InvalidStateException();

  try
  {
   if(!((TrnInfo) t).isPrepared())
   {
    permConn.rollback();
    return;
   }

   Statement s = getStatement((TrnInfo) t);

   s.executeUpdate("ROLLBACK TRANSACTION T1");
   
   buildCache();
  }
  catch(Exception e)
  {
   throw new TransactionException("Rollback failed", e);
  }
  finally
  {
   try
   {
    arbiter.releaseLock((TrnInfo) t);
   }
   catch(InvalidTokenException e)
   {
    throw new TransactionException("Invalid token type", e);
   }

   Statement s = ((TrnInfo) t).getStatement();

   if(s != null)
   {
    try
    {
     s.close();
    }
    catch(SQLException e)
    {
     e.printStackTrace();
    }
   }

  }
 }

 @Override
 public void prepareTransaction(Transaction t) throws TransactionException
 {
  try
  {
   Statement s = getStatement((TrnInfo) t);

   s.executeUpdate("PREPARE COMMIT T1");

   ((TrnInfo) t).setPrepared(true);
  }
  catch(SQLException e)
  {
   throw new TransactionException("Commit preparation failed", e);
  }
 }

 private static class TrnInfo implements TokenW, Transaction
 {
  private boolean   active   = true;
  private boolean   prepared = false;
  private Statement stmt;

  public boolean isActive()
  {
   return active;
  }

  public void setActive(boolean active)
  {
   this.active = active;
  }

  public Statement getStatement()
  {
   return stmt;
  }

  public void setStatement(Statement stmt)
  {
   this.stmt = stmt;
  }

  public boolean isPrepared()
  {
   return prepared;
  }

  public void setPrepared(boolean prepared)
  {
   this.prepared = prepared;
  }

 }
}
