package uk.ac.ebi.age.service.submission.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.age.ext.submission.DataModuleMeta;
import uk.ac.ebi.age.ext.submission.FileAttachmentMeta;
import uk.ac.ebi.age.ext.submission.SubmissionDBException;
import uk.ac.ebi.age.ext.submission.SubmissionMeta;
import uk.ac.ebi.age.ext.submission.SubmissionQuery;
import uk.ac.ebi.age.ext.submission.SubmissionReport;
import uk.ac.ebi.age.service.submission.SubmissionDB;
import uk.ac.ebi.age.util.FileUtil;
import uk.ac.ebi.mg.filedepot.FileDepot;

import com.pri.util.M2Pcodec;
import com.pri.util.StringUtils;

public class H2SubmissionDB extends SubmissionDB
{
 private static final String submissionDB = "SUBMISSIONDB";
 private static final String submissionTable = "SUBMISSION";
 private static final String moduleTable = "MODULE";
 private static final String attachmentTable = "ATTACHMENT";
 private static final String historyTable = "HISTORY";

 private static final String selectSubmissionIDSQL = "SELECT id FROM "+submissionDB+"."+submissionTable
 +" WHERE id=?";
 
 private static final String selectSubmissionSQL = "SELECT * FROM "+submissionDB+"."+submissionTable
 +" WHERE id='";
 
 private static final String selectModuleBySubmissionSQL = "SELECT * FROM "+submissionDB+"."+moduleTable
 +" WHERE submid=?";
 
 private static final String selectAttachmentBySubmissionSQL = "SELECT * FROM "+submissionDB+"."+attachmentTable
 +" WHERE submid=?";

 
 private static final String deleteSubmissionSQL = "DELETE FROM "+submissionDB+"."+submissionTable
 +" WHERE id=?";

 private static final String insertSubmissionSQL = "INSERT INTO "+submissionDB+"."+submissionTable
 +" (id,desc,ctime,mtime,creator,modifier,ft_desc) VALUES (?,?,?,?,?,?,?)";
 
 private static final String insertModuleSQL = "INSERT INTO "+submissionDB+"."+moduleTable
 +" (id,submid,desc,ctime,mtime,creator,modifier) VALUES (?,?,?,?,?,?,?)";
 
 private static final String insertAttachmentSQL = "INSERT INTO "+submissionDB+"."+attachmentTable
 +" (id,submid,desc,ctime,mtime,creator,modifier,filename) VALUES (?,?,?,?,?,?,?,?)";
 
 private static final String insertHistorySQL = "INSERT INTO "+submissionDB+"."+historyTable
 +" (id,mtime,modifier,data) VALUES (?,?,?,?)";

 
 private static final String h2DbPath = "h2db";
 private static final String docDepotPath = "docs";
 private static final String attDepotPath = "att";
 
 private static final Charset docCharset = Charset.forName("UTF-8");
 
 private Connection conn;
 private FileDepot docDepot;
 private FileDepot attachmentDepot;

 private static StringUtils.ReplacePair likePairs[] = new StringUtils.ReplacePair[]
                 {
                  new StringUtils.ReplacePair('\'',"''"), 
                  new StringUtils.ReplacePair('*',"%") 
                 };
 
 public H2SubmissionDB( File sbmDbRoot )
 {
  try
  {
   Class.forName("org.h2.Driver");
   conn = DriverManager.getConnection("jdbc:h2:"+new File(sbmDbRoot,h2DbPath).getAbsolutePath(), "sa", "");
   conn.setAutoCommit(false);
   
   System.out.println("DB URL: "+"jdbc:h2:"+new File(sbmDbRoot,h2DbPath).getAbsolutePath());
   
   initSubmissionDb();
   
   docDepot = new FileDepot( new File(sbmDbRoot,docDepotPath) );
   attachmentDepot = new FileDepot( new File(sbmDbRoot,attDepotPath), true );
  }
  catch(Exception e)
  {
   e.printStackTrace();
   
   throw new RuntimeException("Database initialization error: "+e.getMessage(),e);
  }

 }

 @Override
 public void storeSubmission(SubmissionMeta sMeta, SubmissionMeta oldSbm)
 {
  StringBuilder sb = new StringBuilder(1000);

  sb.append(sMeta.getDescription());

  if(sMeta.getDataModules() != null)
  {
   for(DataModuleMeta dmm : sMeta.getDataModules())
    sb.append(' ').append(dmm.getDescription());
  }

  if(sMeta.getAttachments() != null)
  {
   for(FileAttachmentMeta dmm : sMeta.getAttachments())
    sb.append(' ').append(dmm.getDescription());
  }

  try
  {
   if( oldSbm != null )
   {
    PreparedStatement pstsmt = conn.prepareStatement(deleteSubmissionSQL);
    
    pstsmt.setString(1, oldSbm.getId());
    
    pstsmt.executeUpdate();
    pstsmt.close();
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream( baos );
    oos.writeObject(oldSbm);
    oos.close();
    
    //(id,mtime,modifier,data)
    pstsmt = conn.prepareStatement(insertHistorySQL);
    pstsmt.setString(1, sMeta.getId());
    pstsmt.setLong(2, sMeta.getModificationTime());
    pstsmt.setString(3, sMeta.getModifier());
    pstsmt.setBytes(4, baos.toByteArray());

   }
   
   // (id,desc,ctime,mtime,creator,modifier,ft_desc)
   PreparedStatement pstsmt = conn.prepareStatement(insertSubmissionSQL);
   pstsmt.setString(1, sMeta.getId());
   pstsmt.setString(2, sMeta.getDescription());
   pstsmt.setLong(3, sMeta.getSubmissionTime());
   pstsmt.setLong(4, sMeta.getModificationTime());
   pstsmt.setString(5, sMeta.getSubmitter());
   pstsmt.setString(6, sMeta.getModifier());
   pstsmt.setString(7, sb.toString());

   pstsmt.executeUpdate();
   pstsmt.close();

   if(sMeta.getDataModules() != null)
   {
    //(id,submid,desc,ctime,mtime,creator,modifier)
    pstsmt = conn.prepareStatement(insertModuleSQL);
    for(DataModuleMeta dmm : sMeta.getDataModules())
    {
     pstsmt.setString(1, dmm.getId());
     pstsmt.setString(2, sMeta.getId());
     pstsmt.setString(3, dmm.getDescription());
     pstsmt.setLong(4, dmm.getSubmissionTime());
     pstsmt.setLong(5, dmm.getModificationTime());
     pstsmt.setString(6, dmm.getSubmitter());
     pstsmt.setString(7, dmm.getModifier());

     pstsmt.executeUpdate();

     if( dmm.getText() != null )
     {
      File outPFile = docDepot.getFilePath(dmm.getId(), dmm.getModificationTime());
      
      OutputStreamWriter wrtr = new OutputStreamWriter(new FileOutputStream(outPFile), docCharset);
      
      wrtr.write(dmm.getText());
      
      wrtr.close();
     }
     
    }

    pstsmt.close();
   }

   if( sMeta.getAttachments() != null )
   {
    //(id,submid,desc,ctime,mtime,creator,modifier,filename)
    pstsmt = conn.prepareStatement(insertAttachmentSQL);
    
    for(FileAttachmentMeta fatm : sMeta.getAttachments())
    {
     pstsmt.setString(1, fatm.getOriginalId());
     pstsmt.setString(2, sMeta.getId());
     pstsmt.setString(3, fatm.getDescription());
     pstsmt.setLong(4, fatm.getSubmissionTime());
     pstsmt.setLong(5, fatm.getModificationTime());
     pstsmt.setString(6, fatm.getSubmitter());
     pstsmt.setString(7, fatm.getModifier());
     pstsmt.setString(8, createFileId(sMeta.getId(), fatm.getOriginalId()));

     pstsmt.executeUpdate();

    }

    pstsmt.close(); 
   }

   conn.commit();
  }
  catch(Exception e)
  {
   try
   {
    conn.rollback();
   }
   catch(SQLException e1)
   {
    e1.printStackTrace();
   }

   e.printStackTrace();
  }

 }

 private void initSubmissionDb() throws SQLException
 {
  Statement stmt = conn.createStatement();
  
  stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS "+submissionDB);

  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+submissionDB+'.'+submissionTable+" ("+
    "id VARCHAR PRIMARY KEY, desc VARCHAR, ctime BIGINT, mtime BIGINT, creator VARCHAR, modifier VARCHAR, FT_DESC VARCHAR)");

  stmt.executeUpdate("CREATE INDEX IF NOT EXISTS ctimeIdx ON "+submissionDB+'.'+submissionTable+"(ctime)");
  stmt.executeUpdate("CREATE INDEX IF NOT EXISTS mtimeIdx ON "+submissionDB+'.'+submissionTable+"(mtime)");
  stmt.executeUpdate("CREATE INDEX IF NOT EXISTS creatorIdx ON "+submissionDB+'.'+submissionTable+"(creator)");
  stmt.executeUpdate("CREATE INDEX IF NOT EXISTS modifierIdx ON "+submissionDB+'.'+submissionTable+"(modifier)");

  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+submissionDB+'.'+moduleTable+" ("+
    "id VARCHAR PRIMARY KEY, submid VARCHAR, desc VARCHAR, ctime BIGINT, mtime BIGINT, creator VARCHAR, modifier VARCHAR," +
    " FOREIGN KEY(submid) REFERENCES "
    +submissionDB+'.'+submissionTable+"(id) ON DELETE CASCADE )");

  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+submissionDB+'.'+attachmentTable+" ("+
    "id VARCHAR, submid VARCHAR, desc VARCHAR, ctime BIGINT, mtime BIGINT, creator VARCHAR, modifier VARCHAR, filename VARCHAR," +
    " PRIMARY KEY (id,submid), FOREIGN KEY (submid) REFERENCES "
    +submissionDB+'.'+submissionTable+"(id) ON DELETE CASCADE )");

  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+submissionDB+'.'+historyTable+" ("+
    "id VARCHAR, mtime BIGINT, modifier VARCHAR, data BINARY," +
    " PRIMARY KEY (id,mtime) )");

  conn.commit();
  
  stmt.executeUpdate("CREATE ALIAS IF NOT EXISTS FTL_INIT FOR \"org.h2.fulltext.FullTextLucene.init\"");
  stmt.executeUpdate("CALL FTL_INIT()");

  try
  {
   stmt.executeUpdate("CALL FTL_CREATE_INDEX('"+submissionDB+"', '"+submissionTable+"', 'FT_DESC')");
  }
  catch (Exception e)
  {
   e.printStackTrace();
  }
  
  stmt.close();
 }

 @Override
 public void shutdown()
 {
  if( conn != null )
   try
   {
    conn.close();
   }
   catch(SQLException e)
   {
    e.printStackTrace();
   }
  
  if( docDepot != null )
   docDepot.shutdown();  
 }

 @Override
 public void init()
 {
 }

 @Override
 public SubmissionReport getSubmissions(SubmissionQuery q) throws SubmissionDBException
 {
  String query = q.getQuery();
  
  if( query != null )
  {
   query = query.trim();
   if( query.length() == 0 )
    query = null;
  }
  
  boolean needJoin = q.getModuleID() != null || q.getModifiedFrom() != -1 || q.getModifiedTo() != -1 || q.getModifier() != null;
  
  
  StringBuilder condExpr = new StringBuilder(800);
  
  int pos=0;
  if( q.getTotal() <= 0 )
  {
   condExpr.append("SELECT COUNT( DISTINCT S.ID) AS SC");
   
   if( needJoin )
    condExpr.append(", COUNT( DISTINCT M.ID )");
  
   pos = condExpr.length();
  }
  else
   condExpr.append("SELECT S.*");
  
  if( query != null )
  {
   condExpr.append(" FROM FTL_SEARCH_DATA('");
   StringUtils.appendEscaped(condExpr, q.getQuery(), '\'', '\'');
   condExpr.append("', 0, 0) FT JOIN "+submissionDB+'.'+submissionTable+" S ON  S.ID=FT.KEYS[0]");
  }
  else
   condExpr.append(" FROM "+submissionDB+'.'+submissionTable+" S");

  if( needJoin )
   condExpr.append(" LEFT JOIN "+submissionDB+'.'+moduleTable+" M ON S.id=M.submid");

  boolean hasCond = false;
  
  if( q.getCreatedFrom() != -1 )
  {
   condExpr.append(" WHERE S.ctime >= "+ q.getCreatedFrom());
   hasCond=true;
  }
  
  if( q.getCreatedTo() != -1 )
  {
   if(hasCond)
    condExpr.append(" AND");
   else
    condExpr.append(" WHERE");
   
   condExpr.append(" S.ctime <= ").append(q.getCreatedTo());
   hasCond=true;
  }

  if( q.getModifiedFrom() != -1 )
  {
   if(hasCond)
    condExpr.append(" AND");
   else
    condExpr.append(" WHERE");
   
   condExpr.append(" (S.mtime >= ").append(q.getModifiedFrom()).append(" OR M.mtime >= ").append(q.getModifiedFrom()).append(")");
   hasCond=true;
  }

  if( q.getModifiedTo() != -1 )
  {
   if(hasCond)
    condExpr.append(" AND");
   else
    condExpr.append(" WHERE");
   
   condExpr.append(" (S.mtime <= ").append(q.getModifiedTo()).append(" OR M.mtime <= ").append(q.getModifiedTo()).append(")");
   hasCond=true;
  }

  if( q.getSubmitter() != null )
  {
   if(hasCond)
    condExpr.append(" AND ");
   else
    condExpr.append(" WHERE ");
   
   addWildcardedCondition(condExpr, "S.creator", q.getSubmitter());
   
   hasCond=true;
  }

  if( q.getModifier() != null )
  {
   if(hasCond)
    condExpr.append(" AND (");
   else
    condExpr.append(" WHERE (");

   addWildcardedCondition(condExpr, "S.modifier", q.getModifier() );

   condExpr.append(" OR ");

   addWildcardedCondition(condExpr, "M.modifier", q.getModifier() );
   
   condExpr.append(")");
   
   hasCond=true;
  }

  if( q.getSubmissionID() != null )
  {
   if(hasCond)
    condExpr.append(" AND ");
   else
    condExpr.append(" WHERE ");
   
   addWildcardedCondition(condExpr, "S.id", q.getSubmissionID());
   
   hasCond=true;
  }
  
  if( q.getModuleID() != null )
  {
   if(hasCond)
    condExpr.append(" AND ");
   else
    condExpr.append(" WHERE ");
   
   addWildcardedCondition(condExpr, "M.id", q.getSubmissionID());

   hasCond=true;
  }
  
  
  try
  {
   SubmissionReport rep = new SubmissionReport();

   if( q.getTotal() <= 0 )
   {
    Statement stmt = conn.createStatement();
    
    ResultSet rst = stmt.executeQuery(condExpr.toString());
    
    rst.next();
    
    rep.setTotalSubmissions(rst.getInt(1));
    
    if( needJoin )
     rep.setTotalMatchedModules(rst.getInt(2));
    
    rst.close();
    stmt.close();
    
    String repStr = "SELECT S.*";
    
    for( int i=0; i < pos; i++ )
     condExpr.setCharAt(i, i>=repStr.length()?' ':repStr.charAt(i));
   }
   else
    rep.setTotalSubmissions( q.getTotal() );

   condExpr.append(" LIMIT ").append(q.getLimit());
   condExpr.append(" OFFSET ").append(q.getOffset());
   
   
   rep.setSubmissions( extractSubmission(condExpr.toString(), null) );
   
   return rep;
  }
  catch(SQLException e)
  {
   e.printStackTrace();
   throw new SubmissionDBException("System error", e);
  }
  
  
  
  //select * from FTL_SEARCH_DATA('Hello seva world', 0, 0) FT join tbl S ON  S.ID=FT.KEYS[0] left join stbl M on S.id=m.pid  where  FT.TABLE='TBL' and m.txt like '%va%';
  //select distinct S.id from  tbl S left join stbl M on S.id=M.pid join FTL_SEARCH_DATA('Seva world', 0, 0) FT ON ( FT.TABLE='TBL' AND S.ID=FT.KEYS[0] ) OR (  FT.TABLE='STBL' AND M.ID=FT.KEYS[0]) limit 1,1
  //SELECT T.*,S.* FROM FTL_SEARCH_DATA('Hello AND external', 0, 0) FT, AAA.TBL T, AAA.STBL S WHERE ( FT.TABLE='TBL' AND T.ID=FT.KEYS[0] ) OR (  FT.TABLE='STBL' AND S.ID=FT.KEYS[0] AND S.PID=T.ID)

 }
 
 private void addWildcardedCondition(StringBuilder sb, String field, String value)
 {
  if( value.indexOf('*') == -1 )
  {
   sb.append(field).append(" = '");
   StringUtils.appendEscaped(sb, value, '\'', '\'');
   sb.append('\'');
  }
  else
  {
   sb.append(field).append(" LIKE '");
   StringUtils.appendReplaced(sb, value, likePairs);
   sb.append('\'');
  }
 }

 private List<SubmissionMeta> extractSubmission( String sql, SubmissionMeta sMeta ) throws SQLException
 {
  Statement s = null;
  PreparedStatement mstmt = null;
  PreparedStatement fstmt = null;
  List<SubmissionMeta> result = null;

  
  ResultSet rstS = null;;
  try
  {
   s = conn.createStatement();
   mstmt = conn.prepareStatement( selectModuleBySubmissionSQL );
   fstmt = conn.prepareStatement( selectAttachmentBySubmissionSQL );

   rstS = s.executeQuery(sql);

   if(sMeta == null)
    result = new ArrayList<SubmissionMeta>(30);

   while(rstS.next())
   {
    SubmissionMeta simp = sMeta != null? sMeta : new SubmissionMeta();

    simp.setId(rstS.getString("id"));
    simp.setDescription(rstS.getString("desc") );

    simp.setSubmissionTime(rstS.getLong("ctime"));
    simp.setModificationTime(rstS.getLong("mtime"));
    
    simp.setSubmitter( rstS.getString("creator") );
    simp.setModifier(rstS.getString("modifier") );

    mstmt.setString(1, simp.getId());
    
    ResultSet rstMF = mstmt.executeQuery();

    while( rstMF.next() )
    {
     DataModuleMeta dmm = new DataModuleMeta();
     
     dmm.setId(rstMF.getString("id"));
     dmm.setDescription(rstMF.getString("desc") );

     dmm.setSubmissionTime(rstMF.getLong("ctime"));
     dmm.setModificationTime(rstMF.getLong("mtime"));
     
     dmm.setSubmitter( rstMF.getString("creator") );
     dmm.setModifier(rstMF.getString("modifier") );

     simp.addDataModule(dmm);
    }

    rstMF.close();
    
    fstmt.setString(1, simp.getId());
    rstMF = fstmt.executeQuery();

    
    while( rstMF.next() )
    {
     FileAttachmentMeta fam = new FileAttachmentMeta();

     fam.setId(rstMF.getString("id"));
     fam.setDescription(rstMF.getString("desc") );

     fam.setSubmissionTime(rstMF.getLong("ctime"));
     fam.setModificationTime(rstMF.getLong("mtime"));
     
     fam.setSubmitter( rstMF.getString("creator") );
     fam.setModifier(rstMF.getString("modifier") );

     simp.addAttachment(fam);
    }
    
    rstMF.close();
   
    if( sMeta != null )
     return null;
    
    result.add(simp);
   }

  }
  finally
  {
   if( s != null )
    s.close();
   
   if( mstmt != null )
    mstmt.close();
   
   if( fstmt != null )
    fstmt.close();
  }
  
  return result;
 }
 
 @Override
 public SubmissionMeta getSubmission(String id) throws SubmissionDBException
 {
  StringBuilder sb = new StringBuilder();

  sb.append(selectSubmissionSQL);
  StringUtils.appendEscaped(sb, id, '\'', '\'');
  sb.append('\'');

  SubmissionMeta sm = new SubmissionMeta();
  
  try
  {
   extractSubmission(sb.toString(), sm);
  }
  catch(SQLException e)
  {
   e.printStackTrace();
   throw new SubmissionDBException("System error", e);
  }
  
  return sm;
 }

 @Override
 public boolean hasSubmission(String id) throws SubmissionDBException
 {
  PreparedStatement pstsmt = null;
  ResultSet rst = null;
  try
  {
   pstsmt = conn.prepareStatement(selectSubmissionIDSQL);
   
   pstsmt.setString(1, id);
   
   rst =pstsmt.executeQuery();
   
   if( rst.next() )
    return true;

   return false;
  }
  catch(SQLException e)
  {
   e.printStackTrace();
   throw new SubmissionDBException("System error", e);
  }
  finally
  {
   try
   {
    if( rst != null )
     rst.close();
    
    if( pstsmt != null )
     pstsmt.close();
   }
   catch(SQLException e1)
   {
    e1.printStackTrace();
   }
  }
  
 }

 @Override
 public void storeAttachment(String submId, String fileId, long modificationTime, File aux) throws SubmissionDBException
 {
  String id = createFileId(submId, fileId);

  File dest = attachmentDepot.getFilePath(id, modificationTime);
  
  dest.delete();
  
  try
  {
   FileUtil.linkOrCopyFile(aux, dest);
  }
  catch(IOException e)
  {
   e.printStackTrace();
   throw new SubmissionDBException("System error", e);
  }
 }
 
 private String createFileId( String submId, String fileId )
 {
  submId = M2Pcodec.encode(submId);
  
  return String.valueOf(submId.length())+'.'+submId+'.'+M2Pcodec.encode(fileId);
 }

 @Override
 public File getAttachment( String clustId, String fileId, long ver )
 {
  return attachmentDepot.getFilePath(createFileId(clustId, fileId), ver);
 }

 @Override
 public File getDocument(String clustId, String docId, long ver)
 {
  return docDepot.getFilePath(docId, ver);
 }
}
