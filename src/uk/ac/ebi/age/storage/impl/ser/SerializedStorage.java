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

import uk.ac.ebi.age.mng.SemanticManager;
import uk.ac.ebi.age.model.AgeExternalRelation;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.query.AgeQuery;
import uk.ac.ebi.age.service.IdGenerator;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.storage.IndexFactory;
import uk.ac.ebi.age.storage.StoreException;
import uk.ac.ebi.age.storage.TextIndex;
import uk.ac.ebi.age.storage.exeption.StorageInstantiationException;
import uk.ac.ebi.age.storage.impl.AgeStorageIndex;
import uk.ac.ebi.age.storage.index.AgeIndex;
import uk.ac.ebi.age.storage.index.TextFieldExtractor;
import uk.ac.ebi.age.storage.index.TextValueExtractor;

public class SerializedStorage implements AgeStorageAdm
{
 private static final String modelPath = "model";
 private static final String submissionsPath = "submission";
 private static final String modelFileName = "model.ser";
 
 private File modelFile;
 
 private Map<String, AgeObjectWritable> mainIndexMap = new HashMap<String, AgeObjectWritable>();
 private Map<String, SubmissionWritable> submussionMap = new TreeMap<String, SubmissionWritable>();

 private Map<AgeIndex,AgeStorageIndex> indexMap = new HashMap<AgeIndex,AgeStorageIndex>();

 private SemanticModel model;
 
 public void updateModel( SemanticModel sm )
 {
  model=sm;
 }
 
 public SemanticModel getSemanticModel()
 {
  return model;
 }
 
 public AgeIndex createTextIndex(AgeQuery qury, TextValueExtractor cb)
 {
  AgeIndex idx = new AgeIndex();
  
  TextIndex ti =IndexFactory.getInstance().createFullTextIndex();
  
  ti.index(executeQuery(qury), cb);
  
  indexMap.put(idx, ti);
  
  return idx;
 
 }

 public AgeIndex createTextIndex(AgeQuery qury, Collection<TextFieldExtractor> exts)
 {
  AgeIndex idx = new AgeIndex();
  
  TextIndex ti =IndexFactory.getInstance().createFullTextIndex();
  
  ti.index(executeQuery(qury), exts);
  
  indexMap.put(idx, ti);
  
  return idx;
 
 }

 
 public List<AgeObject> executeQuery(AgeQuery qury)
 {
  
  Iterable<AgeObject> trv = traverse( qury );
  
  ArrayList<AgeObject> res = new ArrayList<AgeObject>();
  
  for( AgeObject nd : trv )
   res.add( nd );
  
  return res;
 }

 private Iterable<AgeObject>  traverse(AgeQuery query)
 {
  return new InMemoryQueryProcessor(query,submussionMap.values());
 }

 public List<AgeObject> queryTextIndex(AgeIndex idx, String query)
 {
  TextIndex ti = (TextIndex)indexMap.get(idx);
  
  return ti.select(query);
 }

 public String storeSubmission(SubmissionWritable sbm) throws StoreException
 {
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
     
     for( AgeRelationWritable irel : rel.getTargetObject().getRelations() )
     {
      if( irel.getRelationClass().equals(invcls) && irel.getTargetObject().equals(obj) )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
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

  submussionMap.put(newSubmissionId, sbm);

  return newSubmissionId;
 }

 private AgeObjectWritable getObjectById(String targetObjectId)
 {
  return mainIndexMap.get(targetObjectId);
 }

 public void init(String initStr) throws StorageInstantiationException
 {
  File baseDir = new File( initStr );

  File modelDir = new File( baseDir, modelPath );
  File submissionDir = new File( baseDir, submissionsPath );
  
  File modelFile = new File(modelDir, modelFileName );
  
  if( baseDir.isFile() )
   throw new StorageInstantiationException("The initial path must be directory: "+initStr);
  
  if( ! baseDir.exists() )
   baseDir.mkdirs();

  if( ! modelDir.exists() )
   modelDir.mkdirs();

  if( ! submissionDir.exists() )
   submissionDir.mkdirs();
 
  if( modelFile.canRead() )
   loadModel();
  else
   model = SemanticManager.getInstance().createMasterModel();
  
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
 
 private void saveModel(SemanticModel sm)
 {
  try
  {
   FileOutputStream fileOut = new FileOutputStream(modelFile);
   
   ObjectOutputStream oos = new ObjectOutputStream( fileOut );
   
   oos.writeObject(sm);
   
   oos.close();
   
   SemanticManager.getInstance().setMasterModel( model );
  }
  catch(Exception e)
  {
   throw new StorageInstantiationException("Can't read model. System error", e);
  }
 }

 
 
 public void shutdown()
 {

 }

 @Override
 public void updateSemanticModel(SemanticModel sm)
 {
  saveModel(sm);
  model = sm;
 }
 
}
