package uk.ac.ebi.age.mng.submission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import uk.ac.ebi.age.annotation.AnnotationManager;
import uk.ac.ebi.age.annotation.Topic;
import uk.ac.ebi.age.conf.Constants;
import uk.ac.ebi.age.ext.annotation.AnnotationDBException;
import uk.ac.ebi.age.ext.authz.TagRef;
import uk.ac.ebi.age.ext.entity.AttachmentEntity;
import uk.ac.ebi.age.ext.entity.ClusterEntity;
import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.LogNode.Level;
import uk.ac.ebi.age.ext.submission.DataModuleMeta;
import uk.ac.ebi.age.ext.submission.Factory;
import uk.ac.ebi.age.ext.submission.FileAttachmentMeta;
import uk.ac.ebi.age.ext.submission.Status;
import uk.ac.ebi.age.ext.submission.SubmissionDBException;
import uk.ac.ebi.age.ext.submission.SubmissionMeta;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.ModuleKey;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.service.id.IdGenerator;
import uk.ac.ebi.age.service.submission.SubmissionDB;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.storage.exeption.AttachmentIOException;
import uk.ac.ebi.age.transaction.Transaction;
import uk.ac.ebi.age.transaction.TransactionException;
import uk.ac.ebi.age.validator.AgeSemanticValidator;
import uk.ac.ebi.age.validator.impl.AgeSemanticValidatorImpl;

import com.pri.util.Extractor;
import com.pri.util.Pair;
import com.pri.util.collection.CollectionsUnion;
import com.pri.util.collection.ExtractorCollection;
import com.pri.util.stream.StreamPump;

public class SubmissionManager
{
// private static SubmissionManager instance = new SubmissionManager();
 
 
 private static class ModMeta
 {
  DataModuleWritable origModule;
  DataModuleWritable newModule;
  DataModuleMeta meta;
  ModuleAux aux;
  
  Map<String, AgeObjectWritable> idMap = new HashMap<String, AgeObjectWritable>();
 }
 
 private static Extractor<ModMeta, DataModuleWritable> modExtractor = new Extractor<ModMeta, DataModuleWritable>()
 {
  @Override
  public DataModuleWritable extract(ModMeta obj)
  {
   return obj.newModule;
  }
 };
 
 private static Extractor<ModMeta, ModuleKey> modkeyExtractor = new Extractor<ModMeta, ModuleKey>()
 {
  @Override
  public ModuleKey extract(ModMeta obj)
  {
   return new ModuleKey( obj.origModule.getClusterId(), obj.origModule.getId() );
  }
 };

 
 private static class FileMeta
 {
  FileAttachmentMeta origFile;
  FileAttachmentMeta newFile;
  AttachmentAux aux;
 }
 
 private static class ClustMeta
 {
  List<ModMeta> incomingMods = new ArrayList<SubmissionManager.ModMeta>();  //New modules and modules with data update (Ins+Upd) but in original order
 
  List<ModMeta> mod4Use = new ArrayList<SubmissionManager.ModMeta>(); //Ins+Upd+Hld+MetaUpd convenience map
 
  List<ModMeta> mod4Ins = new ArrayList<SubmissionManager.ModMeta>();
  
  Map<String,ModMeta> mod4MetaUpd = new HashMap<String, SubmissionManager.ModMeta>(); //Modules with meta (description) update only
  Map<String,ModMeta> mod4DataUpd = new HashMap<String, SubmissionManager.ModMeta>(); //Modules with data update
  Map<String,ModMeta> mod4Del = new HashMap<String, SubmissionManager.ModMeta>();     //Modules to be deleted
  Map<String,ModMeta> mod4Hld = new HashMap<String, SubmissionManager.ModMeta>();     //Modules to be retained (fully untouched, even meta)

  Map<String,FileAttachmentMeta> att4Ins = new HashMap<String, FileAttachmentMeta>(); //New files
  Map<String,FileMeta> att4Upd = new HashMap<String, FileMeta>();                     //Files with content update   
  Map<String,FileMeta> att4MetaUpd = new HashMap<String, FileMeta>();                 //Files with meta update only  
  Map<String,FileAttachmentMeta> att4Del = new HashMap<String, FileAttachmentMeta>();
  Map<String,FileMeta> att4G2L = new HashMap<String, FileMeta>();                     //Files that reduce visibility
  Map<String,FileMeta> att4L2G = new HashMap<String, FileMeta>();                     //Files that increase visibility
  Map<String,FileAttachmentMeta> att4Hld = new HashMap<String, FileAttachmentMeta>(); //Files that keep both content and visibility untouched
  Map<String,FileAttachmentMeta> att4Use = new HashMap<String, FileAttachmentMeta>(); //New full file set

  public String id;
  
  Map<String, AgeObjectWritable> clusterIdMap = new HashMap<String, AgeObjectWritable>();
  Map<String, AgeObjectWritable> newGlobalIdMap = new HashMap<String, AgeObjectWritable>();
 
  Map<AgeRelationClass, RelationClassRef> relRefMap = new HashMap<AgeRelationClass, RelationClassRef>();
 }
 
// public static SubmissionManager getInstance()
// {
//  return instance;
// }
 
 private AgeTabSyntaxParser ageTabParser;
 private AgeTab2AgeConverter converter = null;
 private AgeSemanticValidator validator = new AgeSemanticValidatorImpl();
 private AnnotationManager annotationManager;
 
 private SubmissionDB submissionDB;
 private AgeStorageAdm ageStorage;
 
 public SubmissionManager( AgeStorageAdm ageS, SubmissionDB sDB, AgeTabSyntaxParser prs, AgeTab2AgeConverter conv, AnnotationManager aMngr  )
 {
  ageStorage = ageS;
  submissionDB = sDB;
  ageTabParser=prs;
  converter = conv;
  
  annotationManager = aMngr;   
 }

 public boolean storeSubmission( SubmissionMeta sMeta,  String updateDescr, LogNode logRoot )
 {
  return storeSubmission( sMeta,  updateDescr, logRoot, false );
 }

 @SuppressWarnings("unchecked")
 public boolean storeSubmission( SubmissionMeta sMeta,  String updateDescr, LogNode logRoot, boolean verifyOnly )
 {
 
  SubmissionMeta origSbm = null;

  if( sMeta.getId() != null )
  {
   sMeta.setId(sMeta.getId().trim());

   if(sMeta.getId().length() == 0)
    sMeta.setId(null);
  }
  
  if(sMeta.getId() != null)
  {
   if( sMeta.getStatus() == Status.NEW )
   {
    try
    {
     if(submissionDB.hasSubmission(sMeta.getId()))
     {
      logRoot.log(Level.ERROR, "Submission with ID='" + sMeta.getId() + "' already exists");
      return false;
     }
    }
    catch(SubmissionDBException e)
    {
     logRoot.log(Level.ERROR, "Method hasSubmission error: " + e.getMessage());

     return false;
    }
   }
   
   try
   {
    origSbm = submissionDB.getSubmission(sMeta.getId());
   }
   catch(SubmissionDBException e)
   {
    logRoot.log(Level.ERROR, "Method getSubmition error: " + e.getMessage());

    return false;
   }
  }


  if(sMeta.getStatus() == Status.UPDATEORNEW)
  {
   if(sMeta.getId() == null)
    sMeta.setStatus(Status.NEW);
   else
   {
    if(origSbm != null)
     sMeta.setStatus(Status.UPDATE);
    else
     sMeta.setStatus(Status.NEW);
   }
  }  
  
  if( sMeta.getStatus() == Status.UPDATE )
  {
   if( sMeta.getId() == null )
   {
    logRoot.log(Level.ERROR, "Submission is marked for update but no ID is provided");
    return false;
   }
   
   if( origSbm == null )
   {
    logRoot.log(Level.ERROR, "Submission with ID='"+sMeta.getId()+"' is not found to be updated");
    return false;
   }
   
   sMeta.setSubmitter(origSbm.getSubmitter());
   sMeta.setSubmissionTime(origSbm.getSubmissionTime());
   
   if( sMeta.getDescription() == null )
    sMeta.setDescription( origSbm.getDescription() );
  }

  
  ClustMeta clusterMeta = new ClustMeta();
  clusterMeta.id = sMeta.getId();
  
  List<FileAttachmentMeta> files = sMeta.getAttachments();
  
  
  
  boolean res = true;
  

  int n=0;
  
  if( sMeta.getDataModules() != null )
  {
   for(DataModuleMeta dm : sMeta.getDataModules() ) //Separating modules
   {
    
    n++;
    
    ModuleAux modAux = (ModuleAux)dm.getAux();

    DataModuleWritable exstMod = dm.getId() != null && sMeta.getStatus() == Status.UPDATE ?ageStorage.getDataModule(sMeta.getId(), dm.getId()) : null;
    
    if( modAux.getStatus() == Status.UPDATEORNEW )
    {
     if( exstMod == null )
      modAux.setStatus( Status.NEW );
     else
      modAux.setStatus( Status.UPDATE );
    }
    
    if( modAux.getStatus() == Status.UPDATE || modAux.getStatus() == Status.DELETE )
    {
     if(sMeta.getStatus() != Status.UPDATE)
     {
      logRoot.log(Level.ERROR, "Module " + modAux.getOrder() + " is marked for "+modAux.getStatus().name()+" but submission is not in UPDATE mode");
      res = false;
      continue;
     }

     if(exstMod == null)
     {
      logRoot.log(Level.ERROR, "The storage doesn't contain data module with ID='"+dm.getId() + "' (Cluster ID='"+sMeta.getId()+"') to be updated/deleted");
      res = false;
     }

     if( dm.getId() == null  )
     {
      logRoot.log(Level.ERROR, "Module " + modAux.getOrder() + " is marked for "+modAux.getStatus().name()+" but no ID is provided");
      res = false;
      continue;
     }

     ModMeta mm = new ModMeta();
     mm.meta = dm;
     mm.aux = modAux;

     mm.origModule = exstMod;
     
     if( mm.aux.getStatus() == Status.DELETE )
     {
      clusterMeta.mod4Del.put(mm.meta.getId(),mm);
     }
     else if( dm.getText() != null )
     {
      clusterMeta.mod4DataUpd.put(mm.meta.getId(), mm);
      clusterMeta.mod4Use.add(mm);
      clusterMeta.incomingMods.add(mm);
     }
     else
     {
      clusterMeta.mod4MetaUpd.put(mm.meta.getId(), mm);
      clusterMeta.mod4Use.add(mm);
     }
    }
    else //modAux.getStatus() == Status.NEW
    {
     if(dm.getText() == null)
     {
      logRoot.log(Level.ERROR, "Module " + modAux.getOrder() + " is marked for insertion but no data were provided");
      res = false;
      continue;
     }

     if(dm.getId() != null)
     {

      if(exstMod != null)
      {
       logRoot.log(Level.ERROR,
         "Module " + modAux.getOrder() + " is marked for insertion and has it's own ID (" + dm.getId()
           + ") but this ID it already taken by module of cluster '" + exstMod.getClusterId() + "'");
       res = false;
       continue;
      }
     }

     ModMeta mm = new ModMeta();
     mm.meta = dm;
     mm.aux = modAux;

     mm.meta.setDocVersion( mm.meta.getModificationTime() );

     clusterMeta.mod4Ins.add(mm);
     clusterMeta.mod4Use.add(mm);

     clusterMeta.incomingMods.add(mm);
    }
   }
  }
  
  if( origSbm != null && origSbm.getDataModules() != null ) // now we are sorting modules from the existing cluster (submission)
  {
   for( DataModuleMeta odm : origSbm.getDataModules() )
   {
    String modID = odm.getId();

    ModMeta updMod = clusterMeta.mod4DataUpd.get(modID);
    
    if( updMod == null )
     updMod = clusterMeta.mod4MetaUpd.get(modID);
    
    if( updMod != null )
    {
     if( updMod.meta.getDescription() == null ) // if the new module has no description we keep the old one
      updMod.meta.setDescription(odm.getDescription());

     
     updMod.meta.setSubmissionTime( odm.getSubmissionTime() ); //Preserving originsl submitter and submission time
     updMod.meta.setSubmitter( odm.getSubmitter() );
     
     if( updMod.meta.getText() != null )
      updMod.meta.setDocVersion( updMod.meta.getModificationTime() );
     else
      updMod.meta.setDocVersion( odm.getDocVersion() );

    }
    else if( ! clusterMeta.mod4Del.containsKey(modID) ) //i.e. module that will be kept untouched
    {
     ModMeta mm = new ModMeta();
     mm.meta = odm;
     mm.origModule = ageStorage.getDataModule(clusterMeta.id, modID);
     
     if( mm.origModule == null )
     {
      logRoot.log(Level.ERROR,
        "Module '" + modID + "' is in the submission db but not in the graph. It means data inconsistency. Please contact system administrator");
      
      res = false;
      continue;
     }
     
     clusterMeta.mod4Use.add(mm);
     clusterMeta.mod4Hld.put(modID, mm);
    }
    
   }
  }
  
  
  if( ! res )
   return false;

  Map<String,Integer> globalFileConflicts=null; //= new HashMap<String, Integer>();
  
  if( files != null && files.size() > 0 ) // Sorting incoming files
  {
   LogNode fileNode = logRoot.branch("Preparing attachments");

   for(n = 0; n < files.size(); n++)
   {
    FileAttachmentMeta newFileMeta = files.get(n);
    AttachmentAux newAuxInfo = (AttachmentAux) newFileMeta.getAux();

    String cAtId = newAuxInfo.getNewId() != null ? newAuxInfo.getNewId() : newFileMeta.getId(); // atax.getNewId() != null meant that we want to assign the new ID to some attachment

    if(cAtId == null)
    {
     fileNode.log(Level.ERROR, "File " + newAuxInfo.getOrder() + " has empty ID");
     res = false;
     continue;
    }


    if(newAuxInfo.getStatus() != Status.DELETE)
    {
     for(int k = n + 1; k < files.size(); k++) // All IDs must be unique within the submission
     {
      FileAttachmentMeta ofa = files.get(k);

      AttachmentAux cmpax = (AttachmentAux) ofa.getAux();

      if(cmpax.getStatus() == Status.DELETE)
       continue;

      String cmpAtId = cmpax.getNewId() != null ? cmpax.getNewId() : ofa.getId();

      if(cmpAtId.equals(cAtId))
      {
       fileNode.log(Level.ERROR, "File ID (" + cmpAtId + ") conflict. Files: " + newAuxInfo.getOrder() + " and " + cmpax.getOrder());
       res = false;
       continue;
      }
     }
    }
    
    
    FileAttachmentMeta origFileMeta = null;

    if( origSbm != null && origSbm.getAttachments() != null )
    {
     for(FileAttachmentMeta ofa : origSbm.getAttachments())
     {
      if(newFileMeta.getId().equals(ofa.getId()))
      {
       origFileMeta = ofa;
       break;
      }
     }
    }
    
    if( newAuxInfo.getStatus() == Status.UPDATEORNEW )
    {
     if( origFileMeta != null ) // OR ageStorage.getAttachment(fm.getId(), cstMeta.id) != null
      newAuxInfo.setStatus( Status.UPDATE );
     else
      newAuxInfo.setStatus( Status.NEW );
    }

    if(newAuxInfo.getStatus() == Status.DELETE || newAuxInfo.getStatus() == Status.UPDATE)
    {
     if(origSbm == null) // No original submission. This means a new submission
     {
      fileNode.log(Level.ERROR, "File " + newAuxInfo.getOrder() + " is marked for update/deletion but submission is not in UPDATE mode");
      res = false;
      continue;
     }

     if(origFileMeta == null)
     {
      fileNode.log(Level.ERROR, "File " + newAuxInfo.getOrder() + " is marked for update/deletion but it doesn't exist within the submission");
      res = false;
      continue;
     }
     
//     fm.setSystemId(origFm.getSystemId());
     
     if( newFileMeta.getDescription() == null )
      newFileMeta.setDescription(origFileMeta.getDescription());

     newFileMeta.setSubmitter( origFileMeta.getSubmitter() );
     newFileMeta.setSubmissionTime( origFileMeta.getSubmissionTime() );
     
     if(newAuxInfo.getStatus() == Status.DELETE)
     {
      if( origFileMeta.isGlobal() )
      {
       if( globalFileConflicts == null)
        globalFileConflicts = new HashMap<String, Integer>();
       
       globalFileConflicts.put(newFileMeta.getId(), -1);
      }
      
      newFileMeta.setGlobal(origFileMeta.isGlobal());
      clusterMeta.att4Del.put(newFileMeta.getId(), newFileMeta);
     }
     else // UPDATE
     {
      if(newAuxInfo.getNewId() != null && !newAuxInfo.getNewId().equals(newFileMeta.getId())) //Submitter wants to rename this attachment
      {
       
       // We have checked that all IDs are unique within this submission let's check conflicts with the global IDs
       
       if( newFileMeta.isGlobal() )
       {
        if( ageStorage.getAttachment(newAuxInfo.getNewId()) != null ) // ok, it could be a problem if it not some attachment that we a going to delete
        {
         if( globalFileConflicts == null)
          globalFileConflicts = new HashMap<String, Integer>();

         if( ! globalFileConflicts.containsKey(newAuxInfo.getNewId()) )
          globalFileConflicts.put(newAuxInfo.getNewId(), newAuxInfo.getOrder());
         
//         fileNode.log(Level.ERROR, "File " + newAuxInfo.getNewId() + " has global ID but this ID is already taken");
//         res = false;
//         continue;
        }
       }
       
       //To do renaming we will simulate insertion/deletion
       
       FileAttachmentMeta nfm = Factory.createFileAttachmentMeta();
       AttachmentAux nax = new AttachmentAux();

       nfm.setAux(nax);
       nax.setStatus(Status.NEW);
       nfm.setId(newAuxInfo.getNewId());
       nax.setOrder(newAuxInfo.getOrder());
       nax.setFile(newAuxInfo.getFile() != null ? newAuxInfo.getFile() : submissionDB.getAttachment(origSbm.getId(), origFileMeta.getId(), origFileMeta.getModificationTime()));

       nfm.setSubmitter(origFileMeta.getSubmitter());
       nfm.setModifier(newFileMeta.getModifier());
       nfm.setSubmissionTime(origFileMeta.getSubmissionTime());
       nfm.setModificationTime(newFileMeta.getModificationTime());
       nfm.setDescription(newFileMeta.getDescription());
       nfm.setGlobal(newFileMeta.isGlobal());

       clusterMeta.att4Ins.put(nfm.getId(), nfm);
       clusterMeta.att4Use.put(nfm.getId(), nfm);

       newAuxInfo.setNewId(null);
       newAuxInfo.setStatus(Status.DELETE);

       newFileMeta.setGlobal(origFileMeta.isGlobal());

       clusterMeta.att4Del.put(newFileMeta.getId(), newFileMeta);
      }
      else // UPDATE not renaming
      {
       FileMeta fmeta = new FileMeta(); // Our local structure to keep attachment info together

       fmeta.newFile = newFileMeta;
       fmeta.origFile = origFileMeta;
       fmeta.aux = newAuxInfo;

       if( newAuxInfo.getFile() != null )
        clusterMeta.att4Upd.put(newFileMeta.getId(), fmeta);
       else
       {
        clusterMeta.att4MetaUpd.put(newFileMeta.getId(), fmeta);
        newFileMeta.setFileVersion(origFileMeta.getFileVersion());
       }
       
       clusterMeta.att4Use.put(newFileMeta.getId(), newFileMeta);

       if(newFileMeta.isGlobal() != origFileMeta.isGlobal())
       {
        if(newFileMeta.isGlobal())
         clusterMeta.att4L2G.put(newFileMeta.getId(), fmeta);
        else
         clusterMeta.att4G2L.put(newFileMeta.getId(), fmeta);
       }

      }

     }
    }
    else if(newAuxInfo.getStatus() == Status.NEW)
    {
     if( newAuxInfo.getFile() == null )
     {
      fileNode.log(Level.ERROR, "File " + newAuxInfo.getOrder() + " is marked as NEW but contains no data");
      res = false;
      continue;
     }
     
     if( origFileMeta != null )
     {
      fileNode.log(Level.ERROR, "File " + newAuxInfo.getOrder() + " is marked as NEW but file with the same ID already exists");
      res = false;
      continue;
     }

     if(newFileMeta.isGlobal()) // this is a new file with a new global ID. We have to check its uniqueness
     {

      if( ageStorage.getAttachment(newFileMeta.getId()) != null ) // ok, it could be a problem if it not some attachment that we a going to delete
      {
       if( globalFileConflicts == null)
        globalFileConflicts = new HashMap<String, Integer>();

       if( ! globalFileConflicts.containsKey(newFileMeta.getId()) )
        globalFileConflicts.put(newFileMeta.getId(), newAuxInfo.getOrder());
      }

//      fm.setSystemId(gid);
     }

     
     clusterMeta.att4Ins.put(newFileMeta.getId(), newFileMeta);
     clusterMeta.att4Use.put(newFileMeta.getId(), newFileMeta);

    }
   
   }
  
   if( globalFileConflicts != null )
   {
    for( Map.Entry<String, Integer> me : globalFileConflicts.entrySet() )
    {
     if( me.getValue() != -1 )
     {
      fileNode.log(Level.ERROR, "File " + me.getValue() + " has global ID='"+me.getKey()+"' but this ID is already taken");
      res = false;
     }
    }
   
    globalFileConflicts = null;
   }

   if( res )
    fileNode.success();
  
  }
  
  
  
  if( origSbm != null && origSbm.getAttachments() != null )
  {
   for( FileAttachmentMeta fm : origSbm.getAttachments() )
   {
    if(    ! clusterMeta.att4Del.containsKey(fm.getId())
        && ! clusterMeta.att4Upd.containsKey(fm.getId()) 
        && ! clusterMeta.att4MetaUpd.containsKey(fm.getId()) )
    {
     clusterMeta.att4Hld.put(fm.getDescription(), fm);
     clusterMeta.att4Use.put(fm.getId(), fm);
    }
   }
  }
  
  for( n=0; n < clusterMeta.incomingMods.size(); n++)
  {
   ModMeta mm = clusterMeta.incomingMods.get(n);
   
   boolean modRes = true;
   LogNode modNode = logRoot.branch("Processing module: " + mm.aux.getOrder() );
   
   if( mm.meta.getText() == null ) //Modules to be deleted or meta update
    continue;
   
   LogNode atLog = modNode.branch("Parsing AgeTab");
   
   AgeTabModule atMod =null;
   try
   {
    atMod = ageTabParser.parse(mm.meta.getText());

    atLog.success();
   }
   catch(ParserException e)
   {
    atLog.log(Level.ERROR, "Parsing failed: " + e.getMessage() + ". Row: " + e.getLineNumber() + ". Col: " + e.getColumnNumber());
    modRes = false;
    continue;
   }
   
   LogNode convLog = modNode.branch("Converting AgeTab to Age data module");
   mm.newModule = converter.convert(atMod, ageStorage.getSemanticModel().createContextSemanticModel(), convLog );
   
   if( mm.newModule != null )
    convLog.success();
   else
   {
    convLog.log(Level.ERROR, "Conversion failed");
    modRes = false;
   }
   
   ModuleKey modk = new ModuleKey(clusterMeta.id, mm.meta.getId() );
   mm.newModule.setModuleKey(modk);
   
   if( modRes )
    modNode.success();
   else
    mm.newModule = null;
   
   res = res && modRes;
  }
  
  
  if( ! res )  
   return false;

  
  try
  {
   ageStorage.lockWrite();
   // XXX storeSubmission: connection to the main graph
   
   if( ! checkUniqObjects(clusterMeta, logRoot) )
   {
    res = false;
    return false;
   }

   
   Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject> > extAttrConnector = new ArrayList<Pair<AgeExternalObjectAttributeWritable,AgeObject>>();
   Collection<Pair<AgeExternalRelationWritable, AgeObjectWritable> > relConnections = null;
   Map<AgeObjectWritable,Set<AgeRelationWritable> > relationDetachMap = null;
   
   if( clusterMeta.mod4DataUpd.size() != 0 || ( clusterMeta.mod4Ins.size() !=0 && clusterMeta.mod4Del.size() != 0 ) )
   {
    relConnections = new ArrayList<Pair<AgeExternalRelationWritable,AgeObjectWritable>>();
    relationDetachMap = new HashMap<AgeObjectWritable, Set<AgeRelationWritable>>();
    
    if( ! reconnectExternalObjectAttributes(clusterMeta, extAttrConnector, logRoot))
    {
     res = false;
     return false;
    }
    
    
    if( ! reconnectExternalRelations(clusterMeta, relConnections, relationDetachMap, logRoot) )
    {
     res = false;
     return false;
    }
   }
   
   Map<AgeObjectWritable,Set<AgeRelationWritable>> invRelMap = new HashMap<AgeObjectWritable, Set<AgeRelationWritable>>();
   // invRelMap contains a map of external objects to sets of prepared inverse relations for new external relations
   
   if( !connectNewExternalRelations(clusterMeta, invRelMap, logRoot) )
   {
    res = false;
    return false;
   }
   
   if( !connectNewObjectAttributes(clusterMeta, ageStorage, logRoot) )
   {
    res = false;
    return false;
   }

   
   if( clusterMeta.att4Del.size() != 0 ||  clusterMeta.att4G2L.size() != 0 )
   {
    if( ! checkRemovedDataFiles(clusterMeta, ageStorage, logRoot) )
    {
     res = false;
     return false;
    }
   }

  
   
   LogNode semLog = logRoot.branch("Validating semantic");

   boolean vldRes = true;
   n=0;
   for( ModMeta mm : clusterMeta.incomingMods )
   {
    n++;
    
    if( mm.newModule == null )
     continue;
    
    LogNode vldLog = semLog.branch("Processing module: "+n);
    
    boolean modRes = validator.validate(mm.newModule, vldLog);
    
    if(modRes)
     vldLog.success();

    vldRes = vldRes && modRes;
   }
   
   if( vldRes )
    semLog.success();

   res = res && vldRes;
   
   

   Set<AgeObject> affObjSet = new HashSet<AgeObject>();
   
   if( invRelMap != null )
    affObjSet.addAll( invRelMap.keySet() );
   
   if( relationDetachMap != null )
    affObjSet.addAll( relationDetachMap.keySet() );
   
   if( affObjSet.size() > 0 )
   {
    boolean invRelRes = true;
    LogNode invRelLog = logRoot.branch("Validating externaly related object semantic");
    
    for( AgeObject obj :  affObjSet )
    {
     LogNode objLogNode = invRelLog.branch("Validating object Id: "+obj.getId()+" Class: "+obj.getAgeElClass());
     
     if( validator.validateRelations(obj, invRelMap!=null?invRelMap.get(obj):null,
       relationDetachMap!=null?relationDetachMap.get(obj):null, objLogNode) )
      objLogNode.success();
     else
      invRelRes = false;
    }
    
    if(invRelRes)
     invRelLog.success();
    else
     invRelRes =false;

    res = res && invRelRes;
   }
   
   if( ! res )
    return false;
   
   if( verifyOnly )
   {
    return checkLocalModulesToFilesConnections(clusterMeta, ageStorage, logRoot);
   }
   
   if( clusterMeta.id == null )
   {
    String id = null;
    
    try
    {
     do
     {
      id = Constants.submissionIDPrefix+IdGenerator.getInstance().getStringId(Constants.clusterIDDomain);
     }
     while( submissionDB.hasSubmission(id) );
    }
    catch(SubmissionDBException e)
    {
     logRoot.log(Level.ERROR, "Method hasSubmission error: "+e.getMessage());

     res = false;
     
     return false;
    }
   
    clusterMeta.id = id;
    sMeta.setId(id);
   }
   
   ModuleKey mk = new ModuleKey();
   for( ModMeta mm : clusterMeta.incomingMods )
   {
    mk.setClusterId(clusterMeta.id);
    
    mm.newModule.setClusterId(clusterMeta.id);

    if( mm.origModule != null )
     mm.newModule.setId( mm.origModule.getId() );
    else if( mm.meta.getId() != null )
     mm.newModule.setId( mm.meta.getId() );
    else
    {
     String id = null;

     do
     {
      id = Constants.dataModuleIDPrefix + IdGenerator.getInstance().getStringId(Constants.dataModuleIDDomain);
      mk.setModuleId(id);
     } 
     while(ageStorage.hasDataModule(mk));

     mm.newModule.setId(id);
     mm.meta.setId(id);

    }

    for(AgeObjectWritable obj : mm.newModule.getObjects())
    {
     if(obj.getId() == null)
     {
      String id = null;

      do
      {
       id = Constants.localObjectIDPrefix + obj.getAgeElClass().getIdPrefix()
         + IdGenerator.getInstance().getStringId(Constants.objectIDDomain) + "@" + mm.newModule.getId();
      } while(mm.idMap.containsKey(id));

      obj.setId(id);
      mm.idMap.put(id, obj);
     }
    }
   }
   
   
   connectIncomingModulesToFiles(clusterMeta, ageStorage, logRoot);
   
   Collection< Pair<AgeFileAttributeWritable,String> > fileConn = new ArrayList< Pair<AgeFileAttributeWritable,String> >();
   reconnectLocalModulesToFiles(clusterMeta, fileConn, ageStorage, logRoot);
   
//   for( FileMeta fatm : cstMeta.att4G2L.values() )
//    fatm.newFile.setId(ageStorage.makeLocalFileID(fatm.origFile.getId(), cstMeta.id));
//    
//   for( FileMeta fatm : cstMeta.att4L2G.values() )
//    fatm.newFile.setId(ageStorage.makeGlobalFileID(fatm.origFile.getId()));
//
//   for( FileAttachmentMeta fam : cstMeta.att4Ins.values() )
//   {
//    if( ! fam.isGlobal() ) // We generated IDs for the global files earlier 
//     fam.setSystemId(ageStorage.makeLocalFileID(fam.getId(), cstMeta.id));
//   }
   

   
   if( clusterMeta.att4Ins.size() > 0 )
   {
    LogNode finsLog = logRoot.branch("Storing files");
    
    for( FileAttachmentMeta fam : clusterMeta.att4Ins.values() )
    {
     finsLog.log(Level.INFO, "Storing file: '"+fam.getId()+"' (scope "+(fam.isGlobal()?"global":"cluster")+")");
     
     try
     {
      fam.setFileVersion(fam.getModificationTime());
      
      File tagt = ageStorage.storeAttachment(fam.getId(), clusterMeta.id, fam.isGlobal(), ((AttachmentAux)fam.getAux()).getFile());
      
      submissionDB.storeAttachment(clusterMeta.id, fam.getId(), fam.getFileVersion(), tagt);
     }
     catch(Exception e)
     {
      finsLog.log(Level.ERROR, e.getMessage());

      res = false;
      return false;
     }

     
     finsLog.success();
    }
   }
   
   if( clusterMeta.att4Del.size() > 0 )
   {
    LogNode fdelLog = logRoot.branch("Deleting files");
    
    boolean delRes = true;
    
    for( FileAttachmentMeta fam : clusterMeta.att4Del.values() )
    {
     fdelLog.log(Level.INFO, "Deleting file: '"+fam.getId()+"' (scope "+(fam.isGlobal()?"global":"cluster")+")");
     
     if( ! ageStorage.deleteAttachment(fam.getId(), clusterMeta.id, fam.isGlobal()) )
     {
      fdelLog.log(Level.WARN, "File wasn't deleted or doesn't exist" );
      delRes = false;
     }
     
    }
    
    if( delRes )
     fdelLog.success();
     
   }
   
   if( clusterMeta.att4Upd.size() > 0 )
   {
    LogNode fupdLog = logRoot.branch("Updating files");
    
    for( FileMeta fam : clusterMeta.att4Upd.values() )
    {
     fupdLog.log(Level.INFO, "Updating file: '"+fam.origFile.getId()+"' (scope "+(fam.newFile.isGlobal()?"global":"cluster")+")");
     
     try
     {
      fam.newFile.setFileVersion(fam.newFile.getModificationTime());

      File tagt = ageStorage.storeAttachment(fam.origFile.getId(), clusterMeta.id, fam.origFile.isGlobal(), ((AttachmentAux)fam.newFile.getAux()).getFile());

      submissionDB.storeAttachment(clusterMeta.id, fam.newFile.getId(), fam.newFile.getFileVersion(), tagt);
     }
     catch(Exception e)
     {
      fupdLog.log(Level.ERROR, e.getMessage());

      res = false;
      return false;
     }
     
     fupdLog.success();
    }
   }

   if( clusterMeta.att4G2L.size() > 0 )
   {
    LogNode fupdLog = logRoot.branch("Changing file visibility scope (global to local)");
    
    for( FileMeta fam : clusterMeta.att4G2L.values() )
    {
     fupdLog.log(Level.INFO, "Processing file '"+fam.origFile.getId()+"'");
     
     try
     {
      ageStorage.changeAttachmentScope(fam.origFile.getId(), clusterMeta.id, fam.newFile.isGlobal());
     }
     catch(AttachmentIOException e)
     {
      fupdLog.log(Level.ERROR, e.getMessage());

      res = false;
      return false;
     }
     
     fupdLog.success();
    }
   }

   if( clusterMeta.att4L2G.size() > 0 )
   {
    LogNode fupdLog = logRoot.branch("Changing file visibility scope (local to global)");
    
    for( FileMeta fam : clusterMeta.att4L2G.values() )
    {
     fupdLog.log(Level.INFO, "Processing file '"+fam.origFile.getId()+"'");
     
     try
     {
      ageStorage.changeAttachmentScope(fam.origFile.getId(), clusterMeta.id, fam.newFile.isGlobal());
     }
     catch(AttachmentIOException e)
     {
      fupdLog.log(Level.ERROR, e.getMessage());

      res = false;
      return false;
     }
     
     fupdLog.success();
    }
   }
 
   

   if( clusterMeta.mod4DataUpd.size() > 0 || clusterMeta.mod4Del.size() > 0 || clusterMeta.mod4Ins.size() > 0 )
   {
    LogNode updtLog = logRoot.branch("Updating storage");

    try
    {
     
     Collection<DataModuleWritable> chgMods =        new CollectionsUnion<DataModuleWritable>(
       new ExtractorCollection<ModMeta, DataModuleWritable>(clusterMeta.mod4DataUpd.values(), modExtractor),
       new ExtractorCollection<ModMeta, DataModuleWritable>(clusterMeta.mod4Ins, modExtractor));


     for( DataModuleWritable mod :  chgMods)
      mod.pack();
     
     ageStorage.update( 
       chgMods,

      new CollectionsUnion<ModuleKey>(
        new ExtractorCollection<ModMeta, ModuleKey>(clusterMeta.mod4DataUpd.values(), modkeyExtractor),
        new ExtractorCollection<ModMeta, ModuleKey>(clusterMeta.mod4Del.values(), modkeyExtractor)));

     updtLog.success();
    }
    catch(Exception e)
    {
     e.printStackTrace();
     updtLog.log(Level.ERROR, e.getMessage()!=null?e.getMessage():"Exception: "+e.getClass().getName());

     res = false;

     return false;
    }
   }

   
   SubmissionMeta newSMeta = Factory.createSubmissionMeta();
   
   newSMeta.setId(sMeta.getId());
   newSMeta.setDescription(sMeta.getDescription());
   newSMeta.setRemoved(false);
   newSMeta.setSubmitter(sMeta.getSubmitter());
   newSMeta.setModifier(sMeta.getModifier());
   newSMeta.setSubmissionTime(sMeta.getSubmissionTime());
   newSMeta.setModificationTime(sMeta.getModificationTime());
   
   for( ModMeta dmm : clusterMeta.mod4Use )
    newSMeta.addDataModule(dmm.meta);
  
   for( FileAttachmentMeta fam : clusterMeta.att4Use.values() )
    newSMeta.addAttachment(fam);

  
   try
   {
    submissionDB.storeSubmission(newSMeta, origSbm, updateDescr);
   }
   catch(SubmissionDBException e)
   {
    logRoot.log(Level.ERROR, "Method storeSubmission error: "+e.getMessage());

    res = false;
    
    return false;
   }
   
   
   
   
   if( extAttrConnector != null )
   {
    for( Pair<AgeExternalObjectAttributeWritable, AgeObject> cn : extAttrConnector )
     cn.getFirst().setTargetObject( cn.getSecond() );
   }
   
   if( relConnections != null )
   {
    for( Pair<AgeExternalRelationWritable, AgeObjectWritable> cn : relConnections )
     cn.getFirst().setTargetObject(cn.getSecond());
   }
   
   for( Pair<AgeFileAttributeWritable, String> fc :  fileConn ) 
    fc.getFirst().setFileSysRef( fc.getSecond() );
   
   if( relationDetachMap != null )
   {
    for(Map.Entry<AgeObjectWritable, Set<AgeRelationWritable>> me : relationDetachMap.entrySet())
     for(AgeRelationWritable rel : me.getValue())
      me.getKey().removeRelation(rel);
   }
    //    stor.removeRelations(me.getKey().getId(),me.getValue());

   for( Map.Entry<AgeObjectWritable, Set<AgeRelationWritable>> me :  invRelMap.entrySet() )
    for( AgeRelationWritable rel : me.getValue() )
     me.getKey().addRelation(rel);
    
   for( ModMeta dm : clusterMeta.incomingMods )
   {
    if( dm.newModule != null && dm.newModule.getExternalRelations() != null  )
    {
     for( AgeExternalRelationWritable rel : dm.newModule.getExternalRelations() )
      rel.getInverseRelation().setInverseRelation(rel);
    }
   }
    //    stor.addRelations(me.getKey().getId(),me.getValue());

  }
  finally
  {
   ageStorage.unlockWrite();
  }

  if( ! verifyOnly  )
  {
   Transaction trn = annotationManager.startTransaction();
 
   ClusterEntity cEnt = new ClusterEntity(sMeta.getId());

   try
   {
    if(sMeta.getStatus() == Status.NEW)
     annotationManager.addAnnotation(trn, Topic.OWNER, cEnt, sMeta.getModifier());
    else
    {
     for(ModMeta mm : clusterMeta.mod4Del.values())
      annotationManager.removeAnnotation(trn, Topic.OWNER, mm.newModule, true);

     AttachmentEntity ate = new AttachmentEntity(cEnt, null);

     for(FileAttachmentMeta fatm : clusterMeta.att4Del.values())
     {
      ate.setEntityId(fatm.getId());
      annotationManager.removeAnnotation(trn, Topic.OWNER, ate, true);
     }

     String cstOwner = (String) annotationManager.getAnnotation(trn, Topic.OWNER, cEnt, false);

     if(!sMeta.getModifier().equals(cstOwner))
     {
      for(ModMeta mm : clusterMeta.mod4Ins)
       annotationManager.addAnnotation(trn, Topic.OWNER, mm.newModule, sMeta.getModifier());

      for(FileAttachmentMeta fatm : clusterMeta.att4Ins.values())
      {
       ate.setEntityId(fatm.getId());
       annotationManager.addAnnotation(trn, Topic.OWNER, ate, sMeta.getModifier());
      }
     }

    }
    
    if( sMeta.getTags() != null )
    {
     List<TagRef> tgs = sMeta.getTags();
     
     if( ! ( tgs instanceof ArrayList ) )
     {
      tgs = new ArrayList<TagRef>( tgs.size() );
      tgs.addAll(sMeta.getTags());
     }
     
     Collections.sort(tgs);
     
     annotationManager.addAnnotation(trn, Topic.TAG, cEnt, (Serializable)tgs);
    }
    
    for( ModMeta mm : clusterMeta.incomingMods )
    {
     if( mm.meta.getTags() != null )
     {
      List<TagRef> tgs = mm.meta.getTags();
      
      if( ! ( tgs instanceof ArrayList ) )
      {
       tgs = new ArrayList<TagRef>( tgs.size() );
       tgs.addAll(mm.meta.getTags());
      }
      
      Collections.sort(tgs);
      
      annotationManager.addAnnotation(trn, Topic.TAG, mm.newModule, (Serializable)tgs);
     }
    }
    
    if( sMeta.getAttachments() != null )
    {
     AttachmentEntity ate = new AttachmentEntity(cEnt, null);

     for( FileAttachmentMeta att : sMeta.getAttachments() )
     {
      List<TagRef> tgs = att.getTags();

      if( tgs != null )
      {
       if( ! ( tgs instanceof ArrayList ) )
       {
        tgs = new ArrayList<TagRef>( tgs.size() );
        tgs.addAll(att.getTags());
       }
       
       Collections.sort(tgs);
       
       ate.setEntityId(att.getId());
       
       annotationManager.addAnnotation(trn, Topic.TAG, ate, (Serializable)tgs);      }
     }
    }

    
   }
   catch(AnnotationDBException e)
   {
    try
    {
     annotationManager.rollbackTransaction(trn);
    }
    catch(TransactionException e1)
    {
     e1.printStackTrace();
    }
    
    trn=null;
   }
   finally
   {
    if( trn != null )
    {
     try
     {
      annotationManager.commitTransaction(trn);
     }
     catch(TransactionException e)
     {
      e.printStackTrace();
     }
    }
   }

  }

  
  //Impute reverse relation and revalidate.

  return res;
 }

 
 public boolean restoreSubmission( String sbmID, LogNode logRoot )
 {
  
  SubmissionMeta sMeta = null;
  
  try
  {
   sMeta = submissionDB.getSubmission( sbmID );
  }
  catch(SubmissionDBException e)
  {
   logRoot.log(Level.ERROR, "Method restoreSubmission error: "+e.getMessage());
   
   return false;
  }
  
  if( sMeta == null )
  {
   logRoot.log(Level.ERROR, "Submission with ID='"+sbmID+"' is not found to be restored");
   return false;
  }
  
  ClustMeta cstMeta = new ClustMeta();
  cstMeta.id = sbmID;

  int n=0;
  if( sMeta.getDataModules() != null )
  {
   for( DataModuleMeta dmm : sMeta.getDataModules() )
   {
    n++;
    
    ModMeta mm = new ModMeta();
    ModuleAux maux = new ModuleAux();
    maux.setOrder(n);
    
    mm.meta = dmm;
    mm.aux=maux;
    dmm.setAux(maux);
    
    cstMeta.mod4Ins.add(mm);
    cstMeta.mod4Use.add(mm);

    cstMeta.incomingMods.add(mm);
   }
  }
  
  if( sMeta.getAttachments() != null )
  {
   for(FileAttachmentMeta fatt : sMeta.getAttachments() )
   {
    cstMeta.att4Ins.put(fatt.getId(), fatt);
    cstMeta.att4Use.put(fatt.getId(), fatt);
   }
  }

  
  boolean res = true;
  

  for( n=0; n < cstMeta.incomingMods.size(); n++)
  {
   ModMeta mm = cstMeta.incomingMods.get(n);
   
   boolean modRes = true;
   LogNode modNode = logRoot.branch("Processing module: " + mm.meta.getId() );
   
   File modFile = submissionDB.getDocument(cstMeta.id, mm.meta.getId(), mm.meta.getDocVersion());
   
   if( modFile == null )
   {
    modNode.log(Level.ERROR,"File for module "+mm.meta.getId()+" is not found");
    modRes = false;
   }
   
   ByteArrayOutputStream bais = new ByteArrayOutputStream();

   try
   {
    FileInputStream fis = new FileInputStream(modFile);
    StreamPump.doPump(fis, bais, false);
    fis.close();
    
    bais.close();
    
   }
   catch(IOException e)
   {
    modNode.log(Level.ERROR, "File read error. "+e.getMessage());
    res = false;
   }
   

   byte[] barr = bais.toByteArray();
   String enc = "UTF-8";

   if(barr.length >= 2 && (barr[0] == -1 && barr[1] == -2) || (barr[0] == -2 && barr[1] == -1))
    enc = "UTF-16";

   try
   {
    mm.meta.setText(new String(bais.toByteArray(), enc));
   }
   catch(UnsupportedEncodingException e1)
   {
   }
   
   LogNode atLog = modNode.branch("Parsing AgeTab");
   try
   {
    mm.atMod = ageTabParser.parse(mm.meta.getText());
    atLog.success();
   }
   catch(ParserException e)
   {
    atLog.log(Level.ERROR, "Parsing failed: " + e.getMessage() + ". Row: " + e.getLineNumber() + ". Col: " + e.getColumnNumber());
    res = false;
    continue;
   }
   
   LogNode convLog = modNode.branch("Converting AgeTab to Age data module");
   mm.newModule = converter.convert(mm.atMod, ageStorage.getSemanticModel().createContextSemanticModel(), convLog );
   
   if( mm.newModule != null )
    convLog.success();
   else
    modRes = false;
   
   
   
   if( modRes )
    modNode.success();
   else
    mm.newModule = null;
   
   res = res && modRes;
  }
  
  
  if( ! res )  
   return false;

  
  try
  {
   ageStorage.lockWrite();

   
   // XXX connection to main graph
   
   if( ! checkUniqObjects(cstMeta, ageStorage, logRoot) )
   {
    res = false;
    return false;
   }


 
   
   Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject> > extAttrConnector = new ArrayList<Pair<AgeExternalObjectAttributeWritable,AgeObject>>();

   
   Map<AgeObjectWritable,Set<AgeRelationWritable>> invRelMap = new HashMap<AgeObjectWritable, Set<AgeRelationWritable>>();
   // invRelMap contains a map of external objects to sets of prepared inverse relations for new external relations
   
   if( !connectNewExternalRelations(cstMeta, ageStorage, invRelMap, logRoot) )
   {
    res = false;
    return false;
   }
   
   if( !connectNewObjectAttributes(cstMeta, ageStorage, logRoot) )
   {
    res = false;
    return false;
   }
 
   
   LogNode semLog = logRoot.branch("Validating semantic");

   boolean vldRes = true;
   n=0;
   for( ModMeta mm : cstMeta.incomingMods )
   {
    n++;
    
    if( mm.newModule == null )
     continue;
    
    LogNode vldLog = semLog.branch("Processing module: "+mm.meta.getId());
    
    boolean modRes = validator.validate(mm.newModule, vldLog);
    
    if(modRes)
     vldLog.success();

    vldRes = vldRes && modRes;
   }
   
   if( vldRes )
    semLog.success();

   res = res && vldRes;
   
   

//   Set<AgeObject> affObjSet = new HashSet<AgeObject>();
//   
//   if( invRelMap != null )
//    affObjSet.addAll( invRelMap.keySet() );
   
   
   if( invRelMap.size() > 0 )
   {
    boolean invRelRes = true;
    LogNode invRelLog = logRoot.branch("Validating externaly related object semantic");
    
    for( AgeObject obj :  invRelMap.keySet() )
    {
     LogNode objLogNode = invRelLog.branch("Validating object Id: "+obj.getId()+" Class: "+obj.getAgeElClass());
     
     if( validator.validateRelations(obj, invRelMap.get(obj), null, objLogNode) )
      objLogNode.success();
     else
      invRelRes = false;
    }
    
    if(invRelRes)
     invRelLog.success();
    else
     invRelRes =false;

    res = res && invRelRes;
   }
   
   if( ! res )
    return false;
   
   boolean needReload = false;
  
   for( ModMeta mm : cstMeta.incomingMods )
   {
    String modId = mm.meta.getId();
    
    mm.newModule.setClusterId(cstMeta.id);

    
//    while(ageStorage.hasDataModule(modId))
//    {
//     modId = Constants.dataModuleIDPrefix + IdGenerator.getInstance().getStringId(Constants.dataModuleIDDomain);
//    }
    
    
//    if( ! modId.equals(mm.meta.getId()) )
//    {
//     logRoot.log(Level.WARN, "Module ID '"+mm.meta.getId()+"' is already taken by some another module. New ID="+modId+" is assigned");
//     needReload = true;
//    }
    
    mm.newModule.setId(modId);
    mm.meta.setId(modId);

    for(AgeObjectWritable obj : mm.newModule.getObjects())
    {
     if( obj.getId() == null)
     {
      String id = null;

      do
      {
       id = Constants.localObjectIDPrefix + obj.getAgeElClass().getIdPrefix()
         + IdGenerator.getInstance().getStringId(Constants.objectIDDomain) + "@" + mm.newModule.getId();
      } while(mm.idMap.containsKey(id));

      obj.setId(id);
      mm.idMap.put(id, obj);
     }
    }
   }
   
   
   connectIncomingModulesToFiles(cstMeta, ageStorage, logRoot);
   
   Collection< Pair<AgeFileAttributeWritable,String> > fileConn = new ArrayList< Pair<AgeFileAttributeWritable,String> >();
   reconnectLocalModulesToFiles(cstMeta, fileConn, ageStorage, logRoot);
   
 
   
   LogNode updtLog = logRoot.branch("Updating storage");

   try
   {
    if( cstMeta.mod4DataUpd.size() > 0 || cstMeta.mod4Del.size() > 0 || cstMeta.mod4Ins.size() > 0 )
    {
     
     ageStorage.update( 
        new ExtractorCollection<ModMeta, DataModuleWritable>(cstMeta.mod4Ins, modExtractor),
        null  
     );
     
     updtLog.success();
    }
   }
   catch (Exception e)
   {
    updtLog.log(Level.ERROR, e.getMessage());
    
    res = false;
 
    return false;
   }
   
   
   if( ! needReload )
   {
    try
    {
     submissionDB.restoreSubmission(sbmID);
    }
    catch (Exception e)
    {
     logRoot.log(Level.ERROR, "Method restoreSubmission error: "+e.getMessage());

     res = false;
     
     return false;
    }
   }
   else
   {
    SubmissionMeta newSMeta = Factory.createSubmissionMeta();
    
    newSMeta.setId(sMeta.getId());
    newSMeta.setDescription(sMeta.getDescription());
    newSMeta.setRemoved(false);
    newSMeta.setSubmitter(sMeta.getSubmitter());
    newSMeta.setModifier(sMeta.getModifier());
    newSMeta.setSubmissionTime(sMeta.getSubmissionTime());
    newSMeta.setModificationTime(sMeta.getModificationTime());
    
    for( ModMeta dmm : cstMeta.mod4Use )
     newSMeta.addDataModule(dmm.meta);
    
    for( FileAttachmentMeta fam : cstMeta.att4Use.values() )
     newSMeta.addAttachment(fam);
    
    
    try
    {
     submissionDB.storeSubmission(newSMeta, sMeta, "Restoring submission with changed modules' IDs");
    }
    catch(SubmissionDBException e)
    {
     logRoot.log(Level.ERROR, "Method storeSubmission error: "+e.getMessage());
     
     res = false;
     
     return false;
    }
    
    
   }
   
   
   
   if( extAttrConnector != null )
   {
    for( Pair<AgeExternalObjectAttributeWritable, AgeObject> cn : extAttrConnector )
     cn.getFirst().setTargetObject( cn.getSecond() );
   }
   
  
   for( Pair<AgeFileAttributeWritable, String> fc :  fileConn ) 
    fc.getFirst().setFileSysRef( fc.getSecond() );
   

   for( Map.Entry<AgeObjectWritable, Set<AgeRelationWritable>> me :  invRelMap.entrySet() )
    for( AgeRelationWritable rel : me.getValue() )
     me.getKey().addRelation(rel);
    
   for( ModMeta dm : cstMeta.incomingMods )
   {
    if( dm.newModule != null && dm.newModule.getExternalRelations() != null  )
    {
     for( AgeExternalRelationWritable rel : dm.newModule.getExternalRelations() )
      rel.getInverseRelation().setInverseRelation(rel);
    }
   }
    //    stor.addRelations(me.getKey().getId(),me.getValue());

  }
  finally
  {
   ageStorage.unlockWrite();
  }

  //Impute reverse relation and revalidate.

  return res;
 }
 
 public boolean removeSubmission( String sbmID, LogNode logRoot )
 {
  return removeSubmission(sbmID, false, logRoot);
 }
 
 
 public boolean tranklucateSubmission(String sbmID, LogNode logRoot)
 {
  return removeSubmission(sbmID, true, logRoot);
 }
 
 private boolean removeSubmission( String sbmID, boolean wipeOut, LogNode logRoot )
 {
  SubmissionMeta sMeta = null;

  try
  {
   sMeta = submissionDB.getSubmission(sbmID);
  }
  catch(SubmissionDBException e)
  {
   logRoot.log(Level.ERROR, "Method removeSubmission error: " + e.getMessage());

   return false;
  }

  if(sMeta == null)
  {
   logRoot.log(Level.ERROR, "Submission with ID='" + sbmID + "' is not found to be removed");
   return false;
  }

  ClustMeta cstMeta = new ClustMeta();
  cstMeta.id = sbmID;

  if(sMeta.getDataModules() != null)
  {
   for(DataModuleMeta dmm : sMeta.getDataModules())
   {
    ModMeta mm = new ModMeta();

    mm.meta = dmm;
    mm.origModule = ageStorage.getDataModule(sbmID, dmm.getId());

    if(mm.origModule != null)
     cstMeta.mod4Del.put(dmm.getId(), mm);
   }
  }

  if(sMeta.getAttachments() != null)
  {
   for(FileAttachmentMeta fatt : sMeta.getAttachments())
    cstMeta.att4Del.put(fatt.getId(), fatt);
  }

  boolean res = true;

  try
  {
   ageStorage.lockWrite();

   // XXX connection to main graph

   Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject>> extAttrConnector = new ArrayList<Pair<AgeExternalObjectAttributeWritable, AgeObject>>();
   Collection<Pair<AgeExternalRelationWritable, AgeObjectWritable>> relConnections = null;
   Map<AgeObjectWritable, Set<AgeRelationWritable>> relationDetachMap = null;

   if(cstMeta.mod4Del.size() != 0)
   {
    relConnections = new ArrayList<Pair<AgeExternalRelationWritable, AgeObjectWritable>>();
    relationDetachMap = new HashMap<AgeObjectWritable, Set<AgeRelationWritable>>();

    if(!reconnectExternalObjectAttributes(cstMeta, extAttrConnector, ageStorage, logRoot))
    {
     return false;
    }

    if(!reconnectExternalRelations(cstMeta, relConnections, relationDetachMap, ageStorage, logRoot))
    {
     return false;
    }
   }

   if(cstMeta.att4Del.size() != 0)
   {
    if(!checkRemovedDataFiles(cstMeta, ageStorage, logRoot))
    {
     return false;
    }
   }

   if(relationDetachMap != null)
   {
    boolean invRelRes = true;
    LogNode invRelLog = logRoot.branch("Validating externaly related object semantic");

    for(AgeObject obj : relationDetachMap.keySet())
    {
     LogNode objLogNode = invRelLog.branch("Validating object Id: " + obj.getId() + " Class: " + obj.getAgeElClass());

     if(validator.validateRelations(obj, null, relationDetachMap.get(obj), objLogNode))
      objLogNode.success();
     else
      invRelRes = false;
    }

    if(invRelRes)
     invRelLog.success();
    else
     invRelRes = false;

    res = res && invRelRes;
   }

   if(!res)
    return false;

   if(cstMeta.att4Del.size() > 0)
   {
    LogNode fdelLog = logRoot.branch("Deleting files");

    boolean delRes = true;

    for(FileAttachmentMeta fam : cstMeta.att4Del.values())
    {
     fdelLog.log(Level.INFO, "Deleting file: '" + fam.getId() + "' (scope " + (fam.isGlobal() ? "global" : "cluster") + ")");

     if(!ageStorage.deleteAttachment(fam.getId(), cstMeta.id, fam.isGlobal()))
     {
      fdelLog.log(Level.WARN, "File deletion failed");
      delRes = false;
     }
    }

    if(delRes)
     fdelLog.success();
   }

   if(cstMeta.mod4DataUpd.size() > 0 || cstMeta.mod4Del.size() > 0 || cstMeta.mod4Ins.size() > 0)
   {
    LogNode updtLog = logRoot.branch("Updating storage");

    try
    {
     ageStorage.update(null, new ExtractorCollection<ModMeta, ModuleKey>(cstMeta.mod4Del.values(), modkeyExtractor));

     updtLog.success();
    }
    catch(Exception e)
    {
     updtLog.log(Level.ERROR, "Exception: " + e.getClass().getName() + " Message: " + e.getMessage());

     e.printStackTrace();

     res = false;

     return false;
    }
   }

   LogNode updtLog = logRoot.branch("Updating submission DB");
   try
   {

    if(wipeOut)
     submissionDB.tranklucateSubmission(sbmID);
    else
     submissionDB.removeSubmission(sbmID);

    updtLog.success();
   }
   catch(SubmissionDBException e)
   {
    logRoot.log(Level.ERROR, "Method removeSubmission error: " + e.getMessage());

    res = false;

    return false;
   }

   if(relConnections != null)
   {
    for(Pair<AgeExternalRelationWritable, AgeObjectWritable> cn : relConnections)
     cn.getFirst().setTargetObject(cn.getSecond());
   }

   if(relationDetachMap != null)
   {
    for(Map.Entry<AgeObjectWritable, Set<AgeRelationWritable>> me : relationDetachMap.entrySet())
     for(AgeRelationWritable rel : me.getValue())
      me.getKey().removeRelation(rel);
   }

  }
  finally
  {
   ageStorage.unlockWrite();
  }

  if(!wipeOut)
   return res;

  Transaction trn = annotationManager.startTransaction();

  ClusterEntity cEnt = new ClusterEntity(sMeta.getId());

  try
  {
   annotationManager.removeAnnotation(trn, null, cEnt, true);
  }
  catch(AnnotationDBException e)
  {
   try
   {
    annotationManager.rollbackTransaction(trn);
   }
   catch(TransactionException e1)
   {
    e1.printStackTrace();
   }

   trn = null;
  }
  finally
  {
   if(trn != null)
   {
    try
    {
     annotationManager.commitTransaction(trn);
    }
    catch(TransactionException e)
    {
     e.printStackTrace();
    }
   }
  }

  return res;
 }


 
 
 private boolean connectNewExternalRelations( ClustMeta cstMeta, Map<AgeObjectWritable,Set<AgeRelationWritable>> invRelMap, LogNode rootNode )
 {

  LogNode extRelLog = rootNode.branch("Connecting external object relations");
  boolean extRelRes = true;

  for(ModMeta mm : cstMeta.incomingMods)
  {
   if(mm.newModule == null || mm.newModule.getExternalRelations() == null )
    continue;

   LogNode extRelModLog = extRelLog.branch("Processing module: " + mm.aux.getOrder()+(mm.meta.getId()!=null?" ID='"+mm.meta.getId()+"'":""));

   boolean extModRelRes = true;

   for(AgeExternalRelationWritable exr : mm.newModule.getExternalRelations())
   {
    if( exr.getTargetObject() != null ) //It can happen when we connected inverse relation
     continue;
    
    String ref = exr.getTargetObjectId();

    AgeObjectWritable tgObj = null;
    
    if( exr.getTargetResolveScope() == ResolveScope.GLOBAL  )
    {
     tgObj = cstMeta.newGlobalIdMap.get(ref);
     
     if( tgObj == null )
      tgObj = (AgeObjectWritable) ageStorage.getGlobalObject(ref);
    }
    else
    {
     tgObj = cstMeta.clusterIdMap.get(ref);
     
     if( tgObj == null && exr.getTargetResolveScope() == ResolveScope.CASCADE_CLUSTER )
      tgObj = (AgeObjectWritable) ageStorage.getGlobalObject(ref);
    }

    if( !exr.getAgeElClass().getInverseRelationClass().isImplicit() && exr.getSourceObject().getIdScope() == IdScope.MODULE )
    {
     extModRelRes = false;
     extRelModLog.log(Level.ERROR, "Invalid external relation: '" + ref 
       + "'. Target object is not found within the cluster and the source object has not global identifier " +
               "but relation class has explicit inverse class so inverse relation is impossible. Module: " + mm.aux.getOrder() + " Source object: '"
       + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: " + exr.getSourceObject().getOrder()
       + "). Relation class: " + exr.getAgeElClass() + " Order: " + exr.getOrder());
     
     continue;
    } 
    
    // if there is no target object within the cluster let's try to find global object but we have to keep in mind inverse relation!
    if( tgObj == null )
    {
     if( !exr.getAgeElClass().getInverseRelationClass().isImplicit() && exr.getSourceObject().getIdScope() != IdScope.GLOBAL )
     {
      extModRelRes = false;
      extRelModLog.log(Level.ERROR, "Invalid external relation: '" + ref 
        + "'. Target object found is not found within the cluster and the source object has not global identifier " +
        		"but relation class has explicit inverse class so inverse relation is impossible. Module: " + mm.aux.getOrder() + " Source object: '"
        + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: " + exr.getSourceObject().getOrder()
        + "). Relation class: " + exr.getAgeElClass() + " Order: " + exr.getOrder());
      
      continue;
     }

     tgObj = (AgeObjectWritable) ageStorage.getGlobalObject(ref);

     if(tgObj == null || cstMeta.mod4Del.containsKey(tgObj.getDataModule().getId()) || cstMeta.mod4DataUpd.containsKey(tgObj.getDataModule().getId()))
      tgObj = null;
    }
    

    if(tgObj == null)
    {
     extModRelRes = false;
     extRelModLog.log(Level.ERROR, "Invalid external relation: '" + ref + "'. Target object not found." + " Module: " + mm.aux.getOrder() + " Source object: '"
       + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: " + exr.getSourceObject().getOrder()
       + "). Relation: " + exr.getAgeElClass() + " Order: " + exr.getOrder());
    }
    else
    {
     if(!exr.getAgeElClass().isWithinRange(tgObj.getAgeElClass()))
     {
      extModRelRes = false;
      extRelModLog.log(Level.ERROR,
        "External relation target object's class is not within range. Target object: '" + ref + "' (Class: " + tgObj.getAgeElClass() + "'). Module: " + mm.aux.getOrder()
          + " Source object: '" + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: "
          + exr.getSourceObject().getOrder() + "). Relation: " + exr.getAgeElClass() + " Order: " + exr.getOrder());
     }
     else
     {
      AgeRelationClass invRCls = exr.getAgeElClass().getInverseRelationClass();

      boolean invClassOk = false;
      if(invRCls != null)
      {
       if(invRCls.isCustom())
       {
        extModRelRes = false;
        extRelModLog.log(Level.ERROR, "Class of external inverse relation can't be custom. Target object: '" + ref + "' (Class: " + tgObj.getAgeElClass()
          + "'). Module: " + mm.aux.getOrder() + " Source object: '" + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: "
          + exr.getSourceObject().getOrder() + "). Relation: '" + exr.getAgeElClass() + "' Order: " + exr.getOrder() + ". Inverse relation: " + invRCls);
       }
       else if(!invRCls.isWithinDomain(tgObj.getAgeElClass()))
       {
        extModRelRes = false;
        extRelModLog.log(Level.ERROR,
          "Target object's class is not within domain of inverse relation. Target object: '" + ref + "' (Class: " + tgObj.getAgeElClass() + "'). Module: "
            + mm.aux.getOrder() + " Source object: '" + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: "
            + exr.getSourceObject().getOrder() + "). Relation: '" + exr.getAgeElClass() + "' Order: " + exr.getOrder() + ". Inverse relation: " + invRCls);
       }
       else if(!invRCls.isWithinRange(exr.getSourceObject().getAgeElClass()))
       {
        extModRelRes = false;
        extRelModLog.log(Level.ERROR,
          "Source object's class is not within range of inverse relation. Target object: '" + ref + "' (Class: " + tgObj.getAgeElClass() + "'). Module: "
            + mm.aux.getOrder() + " Source object: '" + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: "
            + exr.getSourceObject().getOrder() + "). Relation: '" + exr.getAgeElClass() + "' Order: " + exr.getOrder() + ". Inverse relation: " + invRCls);
       }
       else
        invClassOk = true;
      }

      if(invClassOk)
      {
       RelationClassRef invCRef = cstMeta.relRefMap.get(invRCls);
       
       if( invCRef == null )
       {
        invCRef =tgObj.getDataModule().getContextSemanticModel().getModelFactory().createRelationClassRef(
          tgObj.getDataModule().getContextSemanticModel().getAgeRelationClassPlug(invRCls), 0, invRCls.getId());
        
        cstMeta.relRefMap.put(invRCls, invCRef);
       }
       
       
       AgeExternalRelationWritable invRel = tgObj.getDataModule().getContextSemanticModel().createExternalRelation(invCRef, tgObj, exr.getSourceObject().getId(), true);
       invRel.setTargetObject(exr.getSourceObject());
       invRel.setInferred(true);

       Set<AgeRelationWritable> rels = invRelMap.get(tgObj);

       if(rels == null)
       {
        rels = new HashSet<AgeRelationWritable>();
        invRelMap.put(tgObj, rels);
       }

       rels.add(invRel);
       
       exr.setInverseRelation(invRel);
       invRel.setInverseRelation(exr);
      }

      exr.setTargetObject(tgObj);
     }
    }
   }

   if(extModRelRes)
    extRelModLog.success();

   
   extRelRes = extRelRes && extModRelRes;

  }

  if(extRelRes)
   extRelLog.success();

  return extRelRes;
 }


 private boolean checkUniqObjects( ClustMeta  clusterMeta, LogNode logRoot )
 {
  boolean res = true;
  
  LogNode logUniq = logRoot.branch("Checking object identifiers uniquness");
  
  Map<DataModule,ModMeta> modMap = new HashMap<DataModule, SubmissionManager.ModMeta>();
  
  for( ModMeta mm : clusterMeta.mod4Use )
  {
   modMap.put(mm.newModule, mm);

   if( mm.newModule == null ) // Hld+MetaUpd
   {
    for( AgeObjectWritable obj : mm.origModule.getObjects() )
    {
     if( obj.getIdScope() == IdScope.CLUSTER || obj.getIdScope() == IdScope.GLOBAL )
     {
      AgeObject clashObj = clusterMeta.clusterIdMap.get(obj.getId());

      if( clashObj != null ) // It meant that some new object pretends to this ID
      {
       res = false;
       
       ModMeta clashMM = modMap.get(clashObj.getDataModule());
       
       logUniq.log(Level.ERROR, "Object identifiers clash (ID='"+obj.getId()+"') whithin the cluster. The first object: module "
         +( (mm.aux!=null?mm.aux.getOrder()+" ":"(existing) ") + (mm.meta.getId()!=null?("ID='"+mm.meta.getId()+"' "):"") + "Row: " + obj.getOrder()  )
         +". The second object: module "
         +( (clashMM.aux!=null?clashMM.aux.getOrder()+" ":"(existing) ") + (clashMM.meta.getId()!=null?("ID='"+clashMM.meta.getId()+"' "):"") + "Row: " + clashObj.getOrder()  )
       );
       
      }
      
      clusterMeta.clusterIdMap.put(obj.getId(),obj);
     }
    }

    continue;
   }
   
   
   for( AgeObjectWritable obj : mm.newModule.getObjects() )
   {
    if( obj.getId() == null ) // new object with anonymous ID
     continue;

    AgeObject clashObj = mm.idMap.get(obj.getId());

    if( clashObj != null )
    {
     res = false;
     
     logUniq.log(Level.ERROR, "Object identifiers clash (ID='"+obj.getId()+"') whithin the same module: "
       +( (mm.aux!=null?mm.aux.getOrder()+" ":"(existing) ") + (mm.meta.getId()!=null?("ID='"+mm.meta.getId()+"' "):"") + "Row: " + obj.getOrder()  )
       +" and Row: " + clashObj.getOrder() 
     );
     
     continue;
    }
    
    mm.idMap.put(obj.getId(), obj);
    
    if( obj.getIdScope() == IdScope.CLUSTER || obj.getIdScope() == IdScope.GLOBAL )
    {
     clashObj = clusterMeta.clusterIdMap.get(obj.getId());
     
     if( clashObj != null )
     {
      res = false;
      
      ModMeta clashMM = modMap.get(clashObj.getDataModule());
      
      logUniq.log(Level.ERROR, "Object identifiers clash (ID='"+obj.getId()+"') whithin the cluster. The first object: module "
        +( (mm.aux!=null?mm.aux.getOrder()+" ":"(existing) ") + (mm.meta.getId()!=null?("ID='"+mm.meta.getId()+"' "):"") + "Row: " + obj.getOrder()  )
        +". The second object: module "
        +( (clashMM.aux!=null?clashMM.aux.getOrder()+" ":"(existing) ") + (clashMM.meta.getId()!=null?("ID='"+clashMM.meta.getId()+"' "):"") + "Row: " + clashObj.getOrder()  )
      );
      
      continue;
     }
     
     clusterMeta.clusterIdMap.put(obj.getId(), obj);
    }
    
    if( obj.getIdScope() == IdScope.GLOBAL )
    {
     clashObj = ageStorage.getGlobalObject(obj.getId());
     
     // We try to find clashing object outside of our cluster as all clashes within the cluster we detected earlier 
     if( clashObj != null && ! clashObj.getModuleKey().getClusterId().equals(clusterMeta.id) )
     {
      res = false;


      logUniq.log(Level.ERROR, "Object identifiers clash (ID='"+obj.getId()+"') whithin the global scope. The first object: "
        +( (mm.aux!=null?mm.aux.getOrder()+" ":"(existing) ") + (mm.meta.getId()!=null?("ID='"+mm.meta.getId()+"' "):"") + "Row: " + obj.getOrder()  )
        +". The second object: cluster ID='"+clashObj.getModuleKey().getClusterId()+"' module ID='"+clashObj.getModuleKey().getModuleId()+"' Row: " + clashObj.getOrder()
      );

      continue;
     }

     
     clusterMeta.newGlobalIdMap.put(obj.getId(), obj);
    }
   }
  }
  
  
  if( res )
   logUniq.success();

  return res;
 }
 
 

 @SuppressWarnings("unchecked")
 private boolean reconnectExternalRelations( ClustMeta  cstMeta, Collection<Pair<AgeExternalRelationWritable, AgeObjectWritable>> relConn, 
   Map<AgeObjectWritable,Set<AgeRelationWritable>> detachedRelMap, LogNode logRoot)
 {
  LogNode logRecon = logRoot.branch("Reconnecting external relations");
  
  boolean res = true; 
  
  for( ModMeta mm : new CollectionsUnion<ModMeta>(cstMeta.mod4Del.values(),cstMeta.mod4DataUpd.values()) )
  {
   if( mm.origModule == null ) //Skipping new modules, processing only update/delete modules (where original data are going away)
    continue;
   
   Collection<? extends AgeExternalRelationWritable> origExtRels = mm.origModule.getExternalRelations();

   if(origExtRels == null)
    continue;
   
   for(AgeExternalRelationWritable extRel : origExtRels)
   {
    AgeExternalRelationWritable invrsRel = extRel.getInverseRelation();
    AgeObjectWritable target = extRel.getTargetObject(); //external object

    if( invrsRel.isInferred() )
    {
     Set<AgeRelationWritable> objectsRels = detachedRelMap.get(target);

     if(objectsRels == null)
      detachedRelMap.put(target, objectsRels = new HashSet<AgeRelationWritable>());

     objectsRels.add(invrsRel);
    }
    else
    {
     AgeObjectWritable replObj = null;
     String replObjId = invrsRel.getTargetObjectId();

     if( invrsRel.getTargetResolveScope() == ResolveScope.GLOBAL || ! target.getModuleKey().getClusterId().equals(cstMeta.id) )
      replObj = cstMeta.newGlobalIdMap.get(replObjId);
     else
      replObj = cstMeta.clusterIdMap.get(replObjId);

     if( replObj == null )
     {
      logRecon.log(Level.ERROR, "Module " + mm.aux.getOrder() + " (ID='" + mm.meta.getId() + "') is marked for "
        +(mm.newModule == null?"deletion":"update")+" but some object (ID='" + extRel.getTargetObjectId()
        + "' Module ID: '"+extRel.getTargetObject().getDataModule().getId()+"' Cluster ID: '"
        +extRel.getTargetObject().getDataModule().getClusterId()+"') holds the relation of class  '" + invrsRel.getAgeElClass() 
        + "' with object '"  +invrsRel.getTargetObjectId() + "'");
      res = false;
     }
     else
     {
      AgeExternalRelationWritable dirRel = null;

      if( replObj.getDataModule().getExternalRelations() != null )
      {
       for( AgeExternalRelationWritable cndtRel : replObj.getDataModule().getExternalRelations() ) //Looking for suitable explicit relation
       {
        if(   cndtRel.getAgeElClass().equals(extRel.getAgeElClass())
           && cndtRel.getTargetObjectId().equals(target.getId()) 
           && invrsRel.getTargetObjectId().equals(replObj.getId())
           && ( target.getIdScope() == IdScope.GLOBAL || target.getModuleKey().getClusterId().equals(cstMeta.id) )
           && ( cndtRel.getTargetResolveScope() != ResolveScope.CLUSTER || target.getModuleKey().getClusterId().equals(cstMeta.id) )
          )
        {
         dirRel=cndtRel;
         break;
        }
       }
      }

      if( dirRel == null )
      {

       RelationClassRef invCRef = cstMeta.relRefMap.get(extRel.getAgeElClass());

       if( invCRef == null )
       {
        invCRef =replObj.getDataModule().getContextSemanticModel().getModelFactory().createRelationClassRef(
          replObj.getDataModule().getContextSemanticModel().getAgeRelationClassPlug(extRel.getAgeElClass()), 0,
          extRel.getTargetObjectId());

        cstMeta.relRefMap.put(extRel.getAgeElClass(), invCRef);
       }

       dirRel = replObj.getDataModule().getContextSemanticModel().createExternalRelation(invCRef, replObj, target.getId(), ResolveScope.CASCADE_CLUSTER);

       dirRel.setInferred(true);

       replObj.addRelation(dirRel);
      }

      dirRel.setInverseRelation(invrsRel);
      dirRel.setTargetObject(target);


      relConn.add( new Pair<AgeExternalRelationWritable, AgeObjectWritable>(invrsRel, replObj) );

      if( ! detachedRelMap.containsKey(target) ) //This is to enforce semantic check on the target object
       detachedRelMap.put(target, null);
     }
    }

   }
   
  }

  
  for( ModMeta mm : cstMeta.mod4Hld.values() )
  {
   Collection<? extends AgeExternalRelationWritable> origExtRels = mm.origModule.getExternalRelations();

   if(origExtRels == null)
    continue;
   
   for(AgeExternalRelationWritable extRel : origExtRels)
   {
    AgeExternalRelationWritable invrsRel = extRel.getInverseRelation();
    AgeObjectWritable target = extRel.getTargetObject(); //external object
    
    if(   extRel.isInferred() 
       || extRel.getTargetResolveScope() != ResolveScope.CASCADE_CLUSTER 
       || target.getModuleKey().getClusterId().equals(cstMeta.id) 
      )
     continue; 
     
    AgeObjectWritable newTg = cstMeta.clusterIdMap.get(extRel.getTargetObjectId());
    
    if( newTg != null )
    {
     
     
     
     
     
     Set<AgeRelationWritable> detSet = detachedRelMap.get(target);
     
     if( detSet == null )
      detachedRelMap.put(target, detSet = new HashSet<AgeRelationWritable>());
     
     detSet.add(invrsRel);
    
     extRel.setTargetObject(newTg);
    
    }
   }
  }
  
  if( res )
   logRecon.success();
  
  return res;

 }
 
 
 private AgeExternalRelationWritable getInverseRelation( AgeExternalRelationWritable extRel, AgeObjectWritable replObj )
 {
  AgeExternalRelationWritable dirRel = null;

  if( replObj.getDataModule().getExternalRelations() != null )
  {
   for( AgeExternalRelationWritable cndtRel : replObj.getDataModule().getExternalRelations() ) //Looking for suitable explicit relation
   {
    if(   cndtRel.getAgeElClass().equals(extRel.getAgeElClass())
       && cndtRel.getTargetObjectId().equals(extRel.getSourceObject().getId()) 
       && extRel.getTargetObjectId().equals(replObj.getId())
       && ( extRel.getSourceObject().getIdScope() == IdScope.GLOBAL || extRel.getSourceObject().getModuleKey().getClusterId().equals(replObj.getModuleKey().getClusterId()) )
       && ( cndtRel.getTargetResolveScope() != ResolveScope.CLUSTER || extRel.getSourceObject().getModuleKey().getClusterId().equals(replObj.getModuleKey().getClusterId()) )
      )
    {
     dirRel=cndtRel;
     break;
    }
   }
  }

  if( dirRel == null )
  {

   RelationClassRef invCRef = cstMeta.relRefMap.get(extRel.getAgeElClass());

   if( invCRef == null )
   {
    invCRef =replObj.getDataModule().getContextSemanticModel().getModelFactory().createRelationClassRef(
      replObj.getDataModule().getContextSemanticModel().getAgeRelationClassPlug(extRel.getAgeElClass()), 0,
      extRel.getTargetObjectId());

    cstMeta.relRefMap.put(extRel.getAgeElClass(), invCRef);
   }

   dirRel = replObj.getDataModule().getContextSemanticModel().createExternalRelation(invCRef, replObj, target.getId(), ResolveScope.CASCADE_CLUSTER);

   dirRel.setInferred(true);

   replObj.addRelation(dirRel);
  }

  dirRel.setInverseRelation(invrsRel);
  dirRel.setTargetObject(target);


  relConn.add( new Pair<AgeExternalRelationWritable, AgeObjectWritable>(invrsRel, replObj) );

  return dirRel;
  
 }
 
 
 private boolean reconnectExternalObjectAttributes( ClustMeta  cstMeta, Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject>> attrConn, LogNode logRoot)
 {
  
  LogNode logRecon = logRoot.branch("Reconnecting external object attributes");
  
  boolean res = true; 
  
  for( DataModule extDM : ageStorage.getDataModules() )
  {
   if( extDM.getClusterId().equals(cstMeta.id) )
   {
    if( cstMeta.mod4Del.containsKey(extDM.getId()) || cstMeta.mod4DataUpd.containsKey(extDM.getId()) )
     continue;
    
    if( cstMeta.mod4Hld.containsKey(extDM.getId()) )
    {
     for( Attributed atb : extDM.getExternalObjectAttributes() )
     {
      AgeExternalObjectAttributeWritable extObjAttr = (AgeExternalObjectAttributeWritable) atb;
      
      if( extObjAttr.getTargetResolveScope() == ResolveScope.CASCADE_CLUSTER && ! extObjAttr.getValue().getModuleKey().getClusterId().equals(cstMeta.id ) )
      {
       AgeObject replObj = cstMeta.clusterIdMap.get(extObjAttr.getTargetObjectId());

       if( replObj != null && replObj.getAgeElClass().isClassOrSubclass(extObjAttr.getAgeElClass().getTargetClass()) )
        attrConn.add( new Pair<AgeExternalObjectAttributeWritable, AgeObject>(extObjAttr, replObj) );
      }
     }
    }
   }
   
   
   for( Attributed atb : extDM.getExternalObjectAttributes() )
   {
    AgeExternalObjectAttributeWritable extObjAttr = (AgeExternalObjectAttributeWritable) atb;
    ModuleKey refModId = extObjAttr.getValue().getModuleKey();

  
    if(refModId.getClusterId().equals(cstMeta.id) && ( cstMeta.mod4Del.containsKey(refModId) || cstMeta.mod4DataUpd.containsKey(refModId) ) )
    {
     AgeObject replObj = null;
     
     if( extObjAttr.getTargetResolveScope() == ResolveScope.GLOBAL || ! extDM.getClusterId().equals(cstMeta.id) )
      replObj = cstMeta.newGlobalIdMap.get(extObjAttr.getTargetObjectId());
     else
     {
      replObj = cstMeta.clusterIdMap.get(extObjAttr.getTargetObjectId());
      
      if(   replObj != null && extObjAttr.getTargetResolveScope() == ResolveScope.CASCADE_CLUSTER 
         && ! replObj.getAgeElClass().isClassOrSubclass(extObjAttr.getAgeElClass().getTargetClass())
        )
         replObj = cstMeta.newGlobalIdMap.get(extObjAttr.getTargetObjectId());
     }
   
     if( replObj == null )
     {
      ModMeta errMod = null;
      
      Attributed ch = extObjAttr.getMasterObject();
      
      String hostId = ch!=null?ch.getId():"???";
      
      if( (errMod = cstMeta.mod4Del.get(refModId)) != null )
       logRecon.log(Level.ERROR, "Module " + errMod.aux.getOrder() + " (ID='" + errMod.meta.getId() + "') is marked for deletion but object (ID='" + extObjAttr.getValue().getId()
         + "') is referred by object attribute (Class='"+extObjAttr.getAgeElClass().getName()
         +"') of object with ID='"+hostId+"' from module '" + extDM.getId() + "' of cluster '" + extDM.getClusterId() + "'");
      else
      {
       errMod = cstMeta.mod4DataUpd.get(refModId);
       logRecon.log(Level.ERROR, "Module " + errMod.aux.getOrder() + " (ID='" + errMod.meta.getId() + "') is marked for update but object (ID='" + extObjAttr.getValue().getId()
         + "') is referred by object attribute (Class='"+extObjAttr.getAgeElClass().getName()
         +"') of object with ID='"+hostId+"' from module '" + extDM.getId() + "' of cluster '" + "' and reference can't be resolved anymore");
      }
      
      res = false;
     }
     else
     {
      if( ! replObj.getAgeElClass().isClassOrSubclass(extObjAttr.getAgeElClass().getTargetClass()) )
      {
       Attributed ch = extObjAttr.getMasterObject();

       String hostId = ch!=null?ch.getId():"???";

       logRecon.log(Level.ERROR, "Object attribute (Class='"+extObjAttr.getAgeElClass().getName()
       		+"') of object (ID='" + hostId+ "') from module '" + extDM.getId() + "' of cluster '" + extDM.getClusterId() + "' can be connected but target object has wrong class ");

       res = false;
      }
      else
       attrConn.add( new Pair<AgeExternalObjectAttributeWritable, AgeObject>(extObjAttr, replObj) );
     }
    }
   }
  }
   
  if( res )
   logRecon.success();
  
  return res;
 }
 
 

 @SuppressWarnings("unchecked")
 private boolean connectIncomingModulesToFiles(ClustMeta cMeta, AgeStorageAdm stor, LogNode logRoot ) //Identifiers must be generated by this moment
 {
  boolean res = true;

  LogNode logCon = logRoot.branch("Connecting file attributes to files");

  
  for(ModMeta mm : new CollectionsUnion<ModMeta>( cMeta.mod4Ins, cMeta.mod4DataUpd.values() ))
  {
   for(AgeFileAttributeWritable fattr : mm.newModule.getFileAttributes())
   {
    FileAttachmentMeta fmt = cMeta.att4Use.get(fattr.getFileId());

    if(fmt != null)
    {
     fattr.setFileSysRef(fmt.isGlobal()?stor.makeFileSysRef(fattr.getFileId()):stor.makeFileSysRef(fattr.getFileId(), cMeta.id));
    }
    else
    {
     String sysRef = stor.makeFileSysRef(fattr.getFileId());

     if(stor.getAttachmentBySysRef(sysRef) != null)
      fattr.setFileSysRef(sysRef);
     else
     {
      AttributeClassRef clRef = fattr.getClassRef();

      logCon.log(Level.ERROR, "Reference to file can't be resolved. Module: " + mm.aux.getOrder()
        + (mm.meta.getId() != null ? (" (ID='" + mm.meta.getId() + "')") : "") + " Attribute: row: " + fattr.getOrder() + " col: " + clRef.getOrder());

      res = false;
     }
    }
   }
  }

  if( res )
   logCon.success();
  
  return res;
 }
 
 
 @SuppressWarnings("unchecked")
 private boolean checkLocalModulesToFilesConnections(ClustMeta cMeta, AgeStorageAdm stor, LogNode logRoot )
 {
  boolean res = true;

  LogNode logCon = logRoot.branch("Checking file attributes to files connections");

  
  for(ModMeta mm : new CollectionsUnion<ModMeta>( cMeta.mod4Ins, cMeta.mod4DataUpd.values() ))
  {
   for(AgeFileAttributeWritable fattr : mm.newModule.getFileAttributes())
   {
    FileAttachmentMeta fmt = cMeta.att4Use.get(fattr.getFileId());

    if(fmt == null)
    {
     String sysRef = stor.makeFileSysRef(fattr.getFileId());

     if(stor.getAttachmentBySysRef(sysRef) == null)
     {
      AttributeClassRef clRef = fattr.getClassRef();

      logCon.log(Level.ERROR, "Reference to file can't be resolved. Module: " + mm.aux.getOrder()
        + (mm.meta.getId() != null ? (" (ID='" + mm.meta.getId() + "')") : "") + " Attribute: row: " + fattr.getOrder() + " col: " + clRef.getOrder());

      res = false;
     }
    }
   }
  }

  
  for( ModMeta mm : cMeta.mod4Hld.values() )
  {
   for( AgeFileAttributeWritable fattr : mm.origModule.getFileAttributes() )
   {
    FileAttachmentMeta fam = cMeta.att4Use.get(fattr.getFileId());
    
    if( fam == null )
    {
     String sysref = stor.makeFileSysRef(fattr.getFileId());
     
     if( stor.getAttachmentBySysRef(sysref) == null )
     {
      logCon.log(Level.ERROR, "Can't connect file attribute: '"+fattr.getFileId()+"'. Module: ID='"+mm.meta.getId()
        +"' Row: "+fattr.getOrder()+" Col: "+fattr.getClassRef().getOrder());
      res = false;
     }
      
    }
   }
  }

  
  if( res )
   logCon.success();
  
  return res;
 }

 
 private boolean checkRemovedDataFiles( ClustMeta cMeta, AgeStorageAdm stor, LogNode logRoot)
 {
  
  LogNode logRecon = logRoot.branch("Reconnecting file attributes");
  
  boolean res = true; 
  
  for( DataModule extDM : stor.getDataModules() )
  {
   if( extDM.getClusterId().equals(cMeta.id) )
    continue;
   
  
   for( AgeFileAttributeWritable fileAttr : extDM.getFileAttributes() )
   {
    if( stor.isFileIdGlobal(fileAttr.getFileSysRef()) )
    {

     FileAttachmentMeta meta = cMeta.att4Del.get(fileAttr.getFileId());

     if(meta != null && meta.isGlobal() )
     {
      FileAttachmentMeta fm = cMeta.att4Ins.get(fileAttr.getFileId());
      
      
      if( fm == null || !fm.isGlobal() )
      {
       res = false;
       logRecon.log(Level.ERROR, "File with ID '" + fileAttr.getFileId() + "' is referred by the module '"
         + extDM.getId() + "' cluster '" + extDM.getClusterId() + "' and can't be deleted");
       continue;
      }
     }

     FileMeta fm = cMeta.att4G2L.get(fileAttr.getFileId());

     if( fm != null )
     {
      res = false;
      logRecon.log(Level.ERROR, "File with ID '" + fileAttr.getFileId() + "' is referred by the module '"
        + extDM.getId() + "' cluster '" + extDM.getClusterId() + "' and can't limit visibility");
      continue;
     }
    }
    
   }
  }
   
  if( res )
   logRecon.success();
  
  return res;
 }

 private boolean reconnectLocalModulesToFiles( ClustMeta cMeta, Collection<Pair<AgeFileAttributeWritable,String>> fileConn, AgeStorageAdm stor, LogNode reconnLog )
 {
  boolean res = true;
  
  for( ModMeta mm : cMeta.mod4Hld.values() )
  {
   for( AgeFileAttributeWritable fattr : mm.origModule.getFileAttributes() )
   {
    FileAttachmentMeta fam = cMeta.att4Use.get(fattr.getFileId());
    
    if( fam == null )
    {
     String sysref = stor.makeFileSysRef(fattr.getFileId());
     
     if( stor.getAttachmentBySysRef(sysref) != null )
      fileConn.add(new Pair<AgeFileAttributeWritable, String>(fattr,sysref));
     else
     {
      reconnLog.log(Level.ERROR, "Can't connect file attribute: '"+fattr.getFileId()+"'. Module: ID='"+mm.meta.getId()
        +"' Row: "+fattr.getOrder()+" Col: "+fattr.getClassRef().getOrder());
      res = false;
     }
      
    }
    else
    {
     String newSysRef = fam.isGlobal()?stor.makeFileSysRef(fam.getId()):stor.makeFileSysRef(fam.getId(), cMeta.id);
    
     if( ! newSysRef.equals(fattr.getFileSysRef()) )
      fileConn.add(new Pair<AgeFileAttributeWritable, String>(fattr,newSysRef));

    }
   }
  }
  
  return res;
 }
 
 private boolean connectNewObjectAttributes(ClustMeta cstMeta, AgeStorageAdm stor, LogNode logRoot)
 {
  
  LogNode connLog = logRoot.branch("Connecting data module"+(cstMeta.incomingMods.size()>1?"s":"")+" to the main graph");

  LogNode extAttrLog = connLog.branch("Connecting external object attributes");
  boolean extAttrRes = true;

  
  Stack<Attributed> attrStk = new Stack<Attributed>();
  
  int n=0;
  for( ModMeta mm : cstMeta.incomingMods )
  {
   n++;
   
   if( mm.newModule == null )
    continue;
   
   boolean mdres = true;
   
   LogNode extAttrModLog = extAttrLog.branch("Processing module: "+mm.aux.getOrder());
   
   for( AgeObjectWritable obj : mm.newModule.getObjects() )
   {
    attrStk.clear();
    attrStk.push(obj);
    
    mdres = connectExternalAttrs( attrStk, stor, cstMeta, mm, extAttrModLog  );
    
    if( obj.getRelations() != null )
    {
     for( AgeRelationWritable rl : obj.getRelations() )
     {
      attrStk.clear();
      attrStk.push(rl);
      
      mdres = mdres && connectExternalAttrs( attrStk, stor, cstMeta, mm, extAttrModLog  );
     }
    }
    

    extAttrRes = extAttrRes && mdres;
   }
   
   if( mdres )
    extAttrModLog.success();
   
//   if( mdres )
//    extAttrModLog.log(Level.SUCCESS, "Success");
//   else
//    extAttrModLog.log(Level.ERROR, "Failed");
  }
  
 if( extAttrRes )
  extAttrLog.success();

//  if( extAttrRes )
//   extAttrLog.log(Level.SUCCESS, "Success");
//  else
//   extAttrLog.log(Level.ERROR, "Failed");


  return extAttrRes;
 }

 
 private boolean connectExternalAttrs( Stack<Attributed> atStk, AgeStorageAdm stor, ClustMeta cstMeta , ModMeta cmod, LogNode log )
 {
  boolean res = true;
  
  Attributed atInst = atStk.peek();
  
  if( atInst.getAttributes().isEmpty() )
   return true;
  
  for( AgeAttribute attr : atInst.getAttributes() )
  {
   if( attr instanceof AgeExternalObjectAttributeWritable )
   {
    AgeExternalObjectAttributeWritable extAttr = (AgeExternalObjectAttributeWritable)attr;
    
    String ref = extAttr.getTargetObjectId();

    AgeObject tgObj = cstMeta.clusterIdMap.get( ref );

    if( tgObj == null )
    {
     tgObj = stor.getGlobalObject( ref );
    
     if( tgObj != null && ( cstMeta.mod4Del.containsKey(tgObj.getDataModule().getId()) || cstMeta.mod4DataUpd.containsKey(tgObj.getDataModule().getId()) ) ) //We don't want to get dead objects 
      tgObj = null;
    }
   
    if( tgObj == null )
    {
     AgeObject obj  = (AgeObject)atStk.get(0);
     
     String attrName = attr.getAgeElClass().getName();
     
     if( atStk.size() > 1 )
     {
      StringBuilder sb = new StringBuilder(200);
      
      sb.append(atStk.get(1).getAttributedClass().getName());
      
      for( int i = 2; i < atStk.size(); i++ )
       sb.append("[").append(atStk.get(i).getAttributedClass().getName()).append("]");
      
      sb.append("[").append(attr.getAgeElClass().getName()).append("]");
      
      attrName = sb.toString();
     }
     
     log.log(Level.ERROR,"Invalid external reference: '"+ref+"'. Target object not found. Source object: '"+obj.getId()+"' (Class: "+obj.getAgeElClass()
       +", Order: "+obj.getOrder()+"). Attribute: "+attrName+" Order: "+attr.getOrder());
     res = false;
    }
    else
    {
     if( ! tgObj.getAgeElClass().isClassOrSubclass(extAttr.getAgeElClass().getTargetClass()) )
     {
      AgeObject obj  = (AgeObject)atStk.get(0);

      String attrName = attr.getAgeElClass().getName();
      if( atStk.size() > 1 )
      {
       attrName = atStk.get(1).getAttributedClass().getName();
       for( int i = 2; i < atStk.size(); i++ )
        attrName += "["+atStk.get(i).getAttributedClass().getName()+"]";
       
       attrName+= "["+attr.getAgeElClass().getName()+"]";
      }

      log.log(Level.ERROR,"Inappropriate target object's (Id: '"+ref+"') class: "+tgObj.getAgeElClass()
        +". Expected class: "+extAttr.getAgeElClass().getTargetClass()+" Source object: '"+obj.getId()+"' (Class: "+obj.getAgeElClass()
        +", Order: "+obj.getOrder()+"). Attribute: "+attrName+" Order: "+attr.getOrder());
      res = false;
     }
     else
      extAttr.setTargetObject(tgObj);
    }

   }
   
   atStk.push(attr);
   res = res && connectExternalAttrs(atStk,stor, cstMeta, cmod, log);
   atStk.pop();
  }
 
  return res;
 }

 public AgeTabSyntaxParser getAgeTabParser()
 {
  return ageTabParser;
 }

 public AgeTab2AgeConverter getAgeTab2AgeConverter()
 {
  return converter;
 }

 public AgeSemanticValidator getAgeSemanticValidator()
 {
  return validator;
 }

 public SubmissionDB getSubmissionDB()
 {
  return submissionDB;
 }


}
