package uk.ac.ebi.age.storage.impl.ser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeExternalRelation;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.service.IdGenerator;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.storage.IndexFactory;
import uk.ac.ebi.age.storage.StoreException;
import uk.ac.ebi.age.storage.TextIndex;
import uk.ac.ebi.age.storage.exeption.ModelStoreException;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;
import uk.ac.ebi.age.storage.exeption.SubmissionStoreException;
import uk.ac.ebi.age.storage.impl.AgeStorageIndex;
import uk.ac.ebi.age.storage.index.AgeIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.storage.index.TextValueExtractor;

public class SerializedStorage implements AgeStorageAdm
{
 private Log log = LogFactory.getLog(this.getClass());
 
 private static final String modelPath = "model";
 private static final String submissionsPath = "submission";
 private static final String modelFileName = "model.ser";
 
 private File modelFile;
 private File dataDir;
 
 private Map<String, AgeObjectWritable> mainIndexMap = new HashMap<String, AgeObjectWritable>();
 private Map<String, SubmissionWritable> submissionMap = new TreeMap<String, SubmissionWritable>();

 private Map<AgeIndex,AgeStorageIndex> indexMap = new HashMap<AgeIndex,AgeStorageIndex>();

 private SemanticModel model;
 
 private ReadWriteLock dbLock = new ReentrantReadWriteLock();
 
// public void updateModel( SemanticModel sm )
// {
//  model=sm;
// }
 
 public SerializedStorage()
 {
 }
 
 public SemanticModel getSemanticModel()
 {
  return model;
 }
 
 public AgeIndex createTextIndex(AgeQuery qury, TextValueExtractor cb)
 {
  AgeIndex idx = new AgeIndex();

  TextIndex ti = IndexFactory.getInstance().createFullTextIndex();

  try
  {
   dbLock.readLock().lock();
   
   ti.index(executeQuery(qury), cb);

   indexMap.put(idx, ti);

   return idx;

  }
  finally
  {
   dbLock.readLock().unlock();
  }
 }

 public AgeIndex createTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts)
 {
  AgeIndex idx = new AgeIndex();

  TextIndex ti = IndexFactory.getInstance().createFullTextIndex();

  try
  {
   dbLock.readLock().lock();

   ti.index(executeQuery(qury), exts);

   indexMap.put(idx, ti);

   return idx;
  }
  finally
  {
   dbLock.readLock().unlock();
  }

 }

 
 public List<AgeObject> executeQuery(AgeQuery qury)
 {
  try
  {
   dbLock.readLock().lock();

   Iterable<AgeObject> trv = traverse(qury);

   ArrayList<AgeObject> res = new ArrayList<AgeObject>();

   for(AgeObject nd : trv)
    res.add(nd);

   return res;
  }
  finally
  {
   dbLock.readLock().unlock();
  }

 }

 private Iterable<AgeObject>  traverse(AgeQuery query)
 {
  return new InMemoryQueryProcessor(query,submissionMap.values());
 }

 public List<AgeObject> queryTextIndex(AgeIndex idx, String query)
 {
  TextIndex ti = (TextIndex)indexMap.get(idx);
  
  return ti.select(query);
 }

 public String storeSubmission(SubmissionWritable sbm) throws StoreException
 {
  try
  {
   dbLock.writeLock().lock();

   Map<AgeObjectWritable, AgeRelationClass> extNodeHash = new HashMap<AgeObjectWritable, AgeRelationClass>();

   for(AgeObjectWritable obj : sbm.getObjects())
   {
    Iterator<? extends AgeRelationWritable> iter = obj.getRelations().iterator();

    while(iter.hasNext())
    {
     AgeRelationWritable rel = iter.next();

     if(rel instanceof AgeExternalRelation)
     {
      String id = ((AgeExternalRelation) rel).getTargetObjectId();

      AgeObjectWritable nd = getObjectById(id);

      if(nd == null)
       throw new StoreException(obj.getOrder(), rel.getOrder(), "Invalid external reference: '" + id + "'");

      extNodeHash.put(nd, rel.getRelationClass());

      iter.remove();
     }
     else
     {
      AgeRelationClass invcls = rel.getRelationClass().getInverseClass();

      boolean found = false;

      for(AgeRelationWritable irel : rel.getTargetObject().getRelations())
      {
       if(irel.getRelationClass().equals(invcls) && irel.getTargetObject().equals(obj))
       {
        found = true;
        break;
       }
      }

      if(!found)
       rel.getTargetObject().createRelation(obj, invcls);
     }

    }

    for(Map.Entry<AgeObjectWritable, AgeRelationClass> me : extNodeHash.entrySet())
    {
     obj.createRelation(me.getKey(), me.getValue());
     me.getKey().createRelation(obj, me.getValue().getInverseClass());
    }

    extNodeHash.clear();
   }

   String newSubmissionId = "SBM" + IdGenerator.getInstance().getStringId();

   submissionMap.put(newSubmissionId, sbm);

   return newSubmissionId;
  }
  finally
  {
   dbLock.writeLock().unlock();
  }

 }

 private AgeObjectWritable getObjectById(String targetObjectId)
 {
  return mainIndexMap.get(targetObjectId);
 }

 public void init(String initStr) throws StorageInstantiationException
 {
  File baseDir = new File( initStr );

  File modelDir = new File( baseDir, modelPath );
  
  modelFile = new File(modelDir, modelFileName );
  dataDir = new File( baseDir, submissionsPath ); 
  
  if( baseDir.isFile() )
   throw new StorageInstantiationException("The initial path must be directory: "+initStr);
  
  if( ! baseDir.exists() )
   baseDir.mkdirs();

  if( ! modelDir.exists() )
   modelDir.mkdirs();

  if( ! dataDir.exists() )
   dataDir.mkdirs();
 
  if( modelFile.canRead() )
   loadModel();
  else
   model = SemanticManager.getInstance().createMasterModel();
  
  loadData();
 }

 
 private void loadData() throws StorageInstantiationException
 {
  try
  {
   dbLock.writeLock().lock();
   
   for( File f : dataDir.listFiles() )
   {
    ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) );
    
    SubmissionWritable submission = (SubmissionWritable)ois.readObject();
    
    ois.close();
    
    submissionMap.put(submission.getId(), submission);
    
    for( AgeObjectWritable obj : submission.getObjects() )
     mainIndexMap.put(obj.getId(), obj);
    
    submission.setMasterModel(model);
   }
   
   for( SubmissionWritable smb : submissionMap.values() )
   {
    if( smb.getExternalRelations() != null )
    {
     for( AgeExternalRelationWritable exr : smb.getExternalRelations() )
     {
      AgeObjectWritable tgObj = mainIndexMap.get(exr.getTargetObjectId());
      
      if( tgObj == null )
       log.warn("Can't resolve external relation. "+exr.getTargetObjectId());
      
      exr.setTargetObject(tgObj);
     }
    }
   }
  }
  catch(Exception e)
  {
   throw new StorageInstantiationException("Can't read submissions. System error", e);
  }
  finally
  {
   dbLock.writeLock().unlock();
  }
 }
 
 
 private void loadModel() throws StorageInstantiationException
 {
  try
  {
   ObjectInputStream ois = new ObjectInputStream( new FileInputStream(modelFile) );
   
   model = (SemanticModel)ois.readObject();
   
   ois.close();
   
   SemanticManager.getInstance().setMasterModel( model );
  }
  catch(Exception e)
  {
   throw new StorageInstantiationException("Can't read model. System error", e);
  }
 }
 
 private void saveModel(SemanticModel sm) throws ModelStoreException
 {
  File modelFile2 = new File( modelFile.getAbsolutePath() );
  File tmpModelFile = new File(modelFile.getAbsolutePath()+".tmp");
  File oldModelFile = new File( modelFile.getAbsolutePath()+"."+System.currentTimeMillis() );
  
  try
  {
   FileOutputStream fileOut = new FileOutputStream(tmpModelFile);
   
   ObjectOutputStream oos = new ObjectOutputStream( fileOut );
   
   oos.writeObject(sm);
   
   oos.close();
   
   if( modelFile2.exists() )
    modelFile2.renameTo(oldModelFile);
   
   tmpModelFile.renameTo( modelFile );
   
  }
  catch(Exception e)
  {
   throw new ModelStoreException("Can't store model: "+e.getMessage(), e);
  }
 }

 private void saveSubmission(SubmissionWritable sm) throws SubmissionStoreException
 {
  File sbmFile = new File( dataDir, sm.getId()+".ser" );
  
  try
  {
   FileOutputStream fileOut = new FileOutputStream(sbmFile);
   
   ObjectOutputStream oos = new ObjectOutputStream( fileOut );
   
   oos.writeObject(sm);
   
   oos.close();

   
  }
  catch(Exception e)
  {
   throw new SubmissionStoreException("Can't store model: "+e.getMessage(), e);
  }
 }

 
 public void shutdown()
 {

 }

 @Override
 public void updateSemanticModel(SemanticModel sm) throws ModelStoreException
 {
  checkModelUpgradable();

  saveModel(sm);

  try
  {
   dbLock.writeLock().lock();

   for(SubmissionWritable sbm : submissionMap.values())
    sbm.setMasterModel(sm);

   SemanticManager.getInstance().setMasterModel(model);

   model = sm;
  }
  finally
  {
   dbLock.writeLock().unlock();
  }

 }

 private void checkModelUpgradable()
 {
 }
 
}
