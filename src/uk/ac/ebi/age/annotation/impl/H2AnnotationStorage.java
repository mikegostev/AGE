package uk.ac.ebi.age.annotation.impl;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

import uk.ac.ebi.age.annotation.AnnotationDBException;
import uk.ac.ebi.age.annotation.Topic;
import uk.ac.ebi.age.entity.Entity;
import uk.ac.ebi.age.transaction.ReadLock;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionException;

import com.pri.util.ObjectRecycler;
import com.pri.util.StringUtils;

public class H2AnnotationStorage extends AbstractAnnotationStorage
{
 private static final String annotationDB = "ANNOTATIONDB";
 private static final String annotationTable = "ANNOTATION";

 private static final String selectAnnotationSQL = " SELECT data FROM " + annotationDB + '.' + annotationTable + " WHERE topic=";

 private String connectionString;
 
 private Connection permConn;
 private AtomicBoolean permConnFree = new AtomicBoolean( true );

 private ObjectRecycler< StringBuilder > sbRecycler = new ObjectRecycler<StringBuilder>(3);
 
 private static final String h2DbPath = "h2db";

 public H2AnnotationStorage( File anntDbRoot)
 {
  try
  {
   Class.forName("org.h2.Driver");
   
   connectionString = "jdbc:h2:"+new File(anntDbRoot,h2DbPath).getAbsolutePath();
   
   permConn = DriverManager.getConnection(connectionString, "sa", "");
   permConn.setAutoCommit(false);
   
   initAnnotationDb();

  }
  catch(Exception e)
  {
   e.printStackTrace();
   
   throw new RuntimeException("Database initialization error: "+e.getMessage(),e);
  }
 }
 
 private Connection createConnection() throws SQLException
 {
  if( permConnFree.compareAndSet(true, false) )
   return permConn;
 
  Connection conn = DriverManager.getConnection(connectionString, "sa", "");
  conn.setAutoCommit(false);

  return conn;
 }

 private void releaseConnection( Connection conn ) throws SQLException
 {
  if( permConn == conn )
  {
   permConnFree.set(true);
   return;
  }
  
  conn.close();
 }
 
 private void initAnnotationDb() throws SQLException
 {
  Connection conn = createConnection();

  try
  {

   Statement stmt = conn.createStatement();

   stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + annotationDB);

   stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + annotationDB + '.' + annotationTable + " ("
     + "id VARCHAR PRIMARY KEY, topic VARCHAR, data BINARY)");

   stmt.executeUpdate("CREATE INDEX IF NOT EXISTS topicIdx ON " + annotationDB + '.' + annotationTable + "(topic)");

   conn.commit();

   stmt.close();
  }
  finally
  {
   releaseConnection(conn);
  }
 }

 @Override
 public Object getAnnotation(Topic tpc, Entity objId, boolean recurs) throws AnnotationDBException
 {
  StringBuilder sb = sbRecycler.getObject();
  
  if( sb == null )
   sb = new StringBuilder(200);

  Connection conn = null;
  Statement stmt = null;
  
  try
  {
   conn = createConnection();
   
   stmt = conn.createStatement();
   
   Entity ce = objId;
   
   Object annot = null;
   
   do
   {
    sb.append(selectAnnotationSQL).append(tpc.name()).append("' AND id='");
    StringUtils.appendEscaped(sb, createEntityId(ce), '\'', '\'');
    sb.append('\'');

    String req = sb.toString();    
    sb.setLength(0);
    
    ResultSet rst = stmt.executeQuery(req);
    
    if( ! rst.next() )
    {
     if( ! recurs )
      return null;
     else
      ce = ce.getParentEntity();
    }
    else
    {
     ObjectInputStream ois = new ObjectInputStream( rst.getBinaryStream(1) );
     
     annot = ois.readObject();
     
     break;
    }
    
   }
   while( ce != null );
   
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

   sbRecycler.recycleObject(sb);

   if(stmt != null)
   {
    try
    {
     stmt.close();
    }
    catch(SQLException e)
    {
     e.printStackTrace();
    }
   }
   
   if(conn != null)
   {
    try
    {
     releaseConnection(conn);
    }
    catch(SQLException e)
    {
     e.printStackTrace();
    }
   }
  }
 }

 @Override
 public boolean addAnnotation(Topic tpc, Entity objId, Serializable value)
 {
  // TODO Auto-generated method stub
  return false;
 }

 @Override
 public boolean addAnnotation(Transaction trn, Topic tpc, Entity objId, Serializable value)
 {
  // TODO Auto-generated method stub
  return false;
 }

 @Override
 public boolean removeAnnotation(Transaction trn, Topic tpc, Entity objId, boolean rec)
 {
  // TODO Auto-generated method stub
  return false;
 }

 @Override
 public ReadLock getReadLock()
 {
  // TODO Auto-generated method stub
  return null;
 }

 @Override
 public void releaseLock(ReadLock l)
 {
  // TODO Auto-generated method stub

 }

 @Override
 public Transaction startTransaction()
 {
  // TODO Auto-generated method stub
  return null;
 }

 @Override
 public void commitTransaction(Transaction t) throws TransactionException
 {
  // TODO Auto-generated method stub

 }

 @Override
 public void rollbackTransaction(Transaction t) throws TransactionException
 {
  // TODO Auto-generated method stub

 }

}
