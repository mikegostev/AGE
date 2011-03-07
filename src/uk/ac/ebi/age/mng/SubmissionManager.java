package uk.ac.ebi.age.mng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import uk.ac.ebi.age.conf.Constants;
import uk.ac.ebi.age.ext.submission.DataModuleMeta;
import uk.ac.ebi.age.ext.submission.FileAttachmentMeta;
import uk.ac.ebi.age.ext.submission.SubmissionMeta;
import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.DataModule;
import uk.ac.ebi.age.model.DataModule.AttributedSelector;
import uk.ac.ebi.age.model.SubmissionContext;
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
import uk.ac.ebi.age.parser.impl.AgeTab2AgeConverterImpl;
import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.service.id.IdGenerator;
import uk.ac.ebi.age.service.submission.SubmissionDB;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.validator.AgeSemanticValidator;
import uk.ac.ebi.age.validator.impl.AgeSemanticValidatorImpl;

import com.pri.util.Pair;

public class SubmissionManager
{
 private static SubmissionManager instance = new SubmissionManager();
 
 private SubmissionDB submissionDB;

 /*
  * submission algorithm
  * 
  * 0. Assumptions
  *  0a. If subm wasn't marked "forUpdate" it assumes that this is a new submission
  * 
  * 1. Check whether the subm is for update
  *  1a. Check for for the following errors:
  *    1. Subm ID is provided but original subm doesn't exist
  * 
  * 2. Separate DMs into groups for insert Di, update Du, delete Dd
  *  2a. Check for for the following errors:
  *    1. Module ID provided but submission is not in UPDATE mode
  *    2. Module ID provided but module is not in the storage
  *    3. Original module exists in the storage but belongs to the other submission
  *    4. Module has no body (assumed that it is for deletion) but ID is not provided
  * 
  * 
  */
 
 
 private static class ModMeta
 {
  AgeTabModule atMod;
  DataModuleWritable origModule;
  DataModuleWritable module;
  DataModuleMeta meta;
 }
 
 public static SubmissionManager getInstance()
 {
  return instance;
 }
 
 private AgeTabSyntaxParser ageTabParser = new AgeTabSyntaxParserImpl();
 private AgeTab2AgeConverter converter = new AgeTab2AgeConverterImpl();
 private AgeSemanticValidator validator = new AgeSemanticValidatorImpl();
 
 public boolean storeSubmission( SubmissionMeta sMeta,  SubmissionContext context, AgeStorageAdm stor, LogNode logRoot )
 {
//  AgeTabModule atSbm=null;
  
//  DataModuleWritable origSbm = null;
  
//  if( update && name != null )
//  {
//   origSbm = stor.getDataModule(name);
//   
//   if( origSbm == null )
//   {
//    logRoot.log(Level.ERROR, "The storage doesn't contain data module with ID='"+name+"'");
//    return null;
//   }
//  }
  
  SubmissionMeta origSbm = null;
  
  if( sMeta.isForUpdate() )
  {
   if(  sMeta.getId() == null  )
   {
    logRoot.log(Level.ERROR, "Submission is marked for update but no ID is provided");
    return false;
   }

   origSbm = submissionDB.getSubmission( sMeta.getId() );
   
   if( origSbm == null )
   {
    logRoot.log(Level.ERROR, "Submission with ID='"+sMeta.getId()+"' is not found to be updated");
    return false;
   }
  }

  
  List<FileAttachmentMeta> files = sMeta.getAttachments();
  
//  String clusterId = sMeta.getClusterId();
//  
//  boolean update = true;
//  
//  if( clusterId == null )
//  {
//   update = false;
//   clusterId=IdGenerator.getInstance().getStringId(Constants.clusterIDDomain);
//  }
  
  List<ModMeta> modules;
  
  
  if(sMeta.getDataModules() == null )
   modules = Collections.emptyList();
  else
   modules = new ArrayList<ModMeta>( sMeta.getDataModules().size() );
   
  
  
  List<ModMeta> mod2Ins;
  
  Map<String,ModMeta> mod2Upd;
  Map<String,ModMeta> mod2Del;
  Map<String,DataModuleMeta> mod2Hld;

  Map<String,FileAttachmentMeta> att4Ins;
  Map<String,FileAttachmentMeta> att4Upd;
  Map<String,FileAttachmentMeta> att4Del;
  Map<String,FileAttachmentMeta> att4G2L;
  Map<String,FileAttachmentMeta> att4L2G;
  
  
  boolean res = true;
  

  int n=0;
  
  if( sMeta.getDataModules() != null )
  {
   modules = new ArrayList<ModMeta>(sMeta.getDataModules().size());

   for(DataModuleMeta dm : sMeta.getDataModules())
   {
    n++;

    ModMeta mm = new ModMeta();
    mm.meta = dm;

    modules.add(mm);

    if(dm.isForUpdate())
    {
     if(origSbm == null)
     {
      logRoot.log(Level.ERROR, "Module " + n + " is marked for update but submission is not in UPDATE mode");
      res = false;
      continue;
     }

     if( mm.meta.getId() == null  )
     {
      logRoot.log(Level.ERROR, "Module " + n + " is marked for update but no ID is provided");
      res = false;
      continue;
     }
     
     mm.origModule = stor.getDataModule(mm.meta.getId());

     if(mm.origModule == null)
     {
      logRoot.log(Level.ERROR, "The storage doesn't contain data module with ID='" + mm.meta.getId() + "' to be updated");
      res = false;
     }
     else if(!mm.origModule.getClusterId().equals(origSbm.getId()))
     {
      logRoot.log(Level.ERROR, "Module with ID='" + mm.meta.getId() + "' belongs to another submission (" + mm.origModule.getClusterId() + ")");
      res = false;
     }
     else if(mm.meta.getText() == null)
     {
      if(mod2Del == null)
       mod2Del = new HashMap<String, SubmissionManager.ModMeta>();

      mod2Del.put(mm.meta.getId(),mm);
     }
     else
     {
      if(mod2Upd == null)
       mod2Upd = new HashMap<String, SubmissionManager.ModMeta>();

      mod2Upd.put(mm.meta.getId(),mm);
     }

    }
    else if(mm.meta.getText() == null)
    {
     logRoot.log(Level.ERROR, "Module "+n+" is marked for insertion but no data were provided");
     res = false;
     continue;
    }
    else
    {
     if( mm.meta.getId() != null)
     {
      DataModuleWritable clashMod = stor.getDataModule(mm.meta.getId());
      
      if( clashMod != null )
      {
       logRoot.log(Level.ERROR,"Module "+n+" is marked for insertion and has it's own ID ("+mm.meta.getId()
         +") but this ID it already taken by module of cluster '"+clashMod.getClusterId()+"'");
       res = false;
       continue;
      }
     }
     
     if(mod2Ins == null)
      mod2Ins = new ArrayList<ModMeta>(5);

     mod2Ins.add(mm);
    }

   }
  }
  else
   modules = Collections.emptyList();
  
  if( origSbm != null && origSbm.getDataModules() != null )
  {
   for( DataModuleMeta odm : origSbm.getDataModules() )
   {
    String modID = odm.getId();
    
    if( ! mod2Upd.containsKey(modID) && ! mod2Del.containsKey(modID) )
    {
     if( mod2Hld == null )
      mod2Hld = new HashMap<String, DataModuleMeta>();
     
     mod2Hld.put(modID, odm);
    }
   }
  }
  
  
  if( ! res )
   return false;

  if( files != null && files.size() > 0 )
  {
   LogNode fileNode = logRoot.branch("Checking file ID uniqueness");

   
   for( n=0; n < files.size(); n++)
   {
    FileAttachmentMeta fm = files.get(n);

    if( fm.getOriginalId() == null )
    {
     fileNode.log(Level.ERROR, "File "+(n+1)+" has empty ID");
     res = false;
     continue;
    }

    FileAttachmentMeta origFm = null;
    
    if( origSbm != null && origSbm.getAttachments() != null )
    {
     for( FileAttachmentMeta ofa : origSbm.getAttachments() )
     { 
      if( ofa.getOriginalId().equals(fm.getOriginalId()) )
      {
       origFm = ofa;
       break;
      }
     }
    }
    
    for( int k=n+1; k < files.size(); k++ ) // All IDs must be unique within the submission
    {
     if( fm.getOriginalId().equals(files.get(k).getOriginalId() ) )
     {
      fileNode.log(Level.ERROR, "File ID ("+fm.getOriginalId()+") conflict. Files: "+(n+1)+" and "+(k+1));
      res = false;
      continue;
     }
    }
    
    if( fm.getAux() == null ) //File for deletion or visibility change without update
    {
     if( origSbm == null ) // No original submission. This means a new submission 
     {
      fileNode.log(Level.ERROR, "File " + (n + 1) + " is marked for deletion but submission is not in UPDATE mode");
      res = false;
      continue;
     }

     if(origFm == null)
     {
      fileNode.log(Level.ERROR, "File " + (n + 1) + " is marked for deletion but it doesn't exist within the submission");
      res = false;
      continue;
     }
     
     if( fm.isGlobal() != origFm.isGlobal() )
     {
      if( fm.isGlobal() )
      {
       if( att4L2G == null )
        att4L2G = new HashMap<String,FileAttachmentMeta>();
       
       att4L2G.put(fm.getOriginalId(), fm);
      }
      else
      {
       if( att4G2L == null )
        att4G2L = new HashMap<String,FileAttachmentMeta>();
       
       att4G2L.put(fm.getOriginalId(), fm);
      }
      
     }
     else
     {
      if( att4Del == null )
       att4Del = new HashMap<String,FileAttachmentMeta>();
      
      att4Del.put(fm.getOriginalId(), fm);
     }
 
    }
    else //Files for update and for update+visibility change
    {
     if( origFm == null && fm.isGlobal() ) //this is a new file with a new global ID. We have to check its uniqueness
     {
      String gid = stor.makeGlobalFileID( fm.getOriginalId() );
      
      if( stor.getAttachment( gid ) != null )
      {
       fileNode.log(Level.ERROR, "File " + (n + 1) + " has global ID but this ID is already taken");
       res = false;
       continue;
      }
      
      fm.setId(gid);
     }

     if(origFm == null)
     {
      if( att4Ins == null )
       att4Ins = new HashMap<String,FileAttachmentMeta>();
      
      att4Ins.put(fm.getOriginalId(), fm);
     }
     else
     {
      if( att4Upd == null )
       att4Upd = new HashMap<String,FileAttachmentMeta>();
      
      att4Upd.put(fm.getOriginalId(), fm);

      if( fm.isGlobal() != origFm.isGlobal() )
      {
       if( fm.isGlobal() )
       {
        if( att4L2G == null )
         att4L2G = new HashMap<String,FileAttachmentMeta>();
        
        att4L2G.put(fm.getOriginalId(), fm);
       }
       else
       {
        if( att4G2L == null )
         att4G2L = new HashMap<String,FileAttachmentMeta>();
        
        att4G2L.put(fm.getOriginalId(), fm);
       }
      }
     }
    }
   }
  }
  
  
  for( n=0; n < modules.size(); n++)
  {
   ModMeta mm = modules.get(n);
   
   boolean modRes = true;
   LogNode modNode = logRoot.branch("Processing module: " + (n+1) );
   
   if( mm.meta.getText() == null )
   {
    modNode.log(Level.INFO, "Module is marked for deletion. Skiping");
    continue;
   }
   
   boolean atRes = true;
   LogNode atLog = modNode.branch("Parsing AgeTab");
   try
   {
    mm.atMod = ageTabParser.parse(mm.meta.getText());
    atLog.log(Level.INFO, "Success");
   }
   catch(ParserException e)
   {
    atLog.log(Level.ERROR, "Parsing failed: " + e.getMessage() + ". Row: " + e.getLineNumber() + ". Col: " + e.getColumnNumber());
    res = false;
    continue;
   }
   
   LogNode convLog = modNode.branch("Converting AgeTab to Age data module");
   mm.module = converter.convert(mm.atMod, SemanticManager.getInstance().getContextModel(context), convLog );
   
   if( mm.module != null )
    convLog.log(Level.INFO, "Success");
   else
   {
    convLog.log(Level.ERROR, "Conversion failed");
    res = false;
    continue;
   }
   
   boolean uniqRes1 = true;
   
   LogNode uniqGLog = modNode.branch("Checking global identifiers uniqueness");

   LogNode uniqLog = uniqGLog.branch("Checking main graph");

   for( AgeObjectWritable obj : mm.module.getObjects())
   {
    if( obj.getId() != null ) //Local objects have no IDs yet
    {
     AgeObject origObj = stor.getObjectById( obj.getId() );
     
     boolean unqOK = true;
     if( origObj != null ) //We've found an object but if it belongs to updated/replaced module we postpone the conflict resolution 
     {
      String oModId = origObj.getDataModule().getId();
      
      if( ! ( (mod2Del != null && mod2Del.containsKey(oModId)) || (mod2Upd != null && mod2Upd.containsKey(oModId) ) ) )
      {
       uniqLog.log(Level.ERROR, "Object id '"+obj.getId()+"' has been taken by the object from data module: "+oModId);
       uniqRes1 = false;
      }
     }
    }
   }
   
   if( uniqRes1 )
    uniqLog.log(Level.INFO, "Success");
   else
    uniqLog.log(Level.ERROR, "Failed");
  
   boolean uniqRes2 = true;

   if( modules.size() > 1 )
   {
    uniqLog = uniqGLog.branch("Checking other modules within this cluster");
    
    for( int k=0; k < n; k++ )
    {
     DataModuleWritable om = modules.get(k).module;
     
     if( om == null )
      continue;
     
     for( AgeObjectWritable obj : mm.module.getObjects())
     {
      if( obj.getId() != null )
      {
       for( AgeObject othObj : om.getObjects() )
       {
        if( othObj.getId() != null && othObj.getId().equals(obj.getId()) )
        {
         uniqLog.log(Level.ERROR, "Object id '"+obj.getId()+"' has been taken by the object from sibling data module: "+(k+1));
         uniqRes2 = false;
        }
       }
      }
     }
    }
   }
   
   if( uniqRes2 )
    uniqLog.log(Level.INFO, "Success");
   else
    uniqLog.log(Level.ERROR, "Failed");

   if( uniqRes1 && uniqRes2 )
    uniqGLog.log(Level.INFO, "Success");
   else
    uniqGLog.log(Level.ERROR, "Failed");

   
   modRes = uniqRes1 && uniqRes2 && atRes;
   
   if( modRes )
    modNode.log(Level.INFO, "Success");
   else
   {
    modNode.log(Level.ERROR, "Failed");
    mm.module = null;
   }
   
   res = res && modRes;
  }
  

  
  if( ! res )  
   return false;

  
  try
  {
   LogNode connLog = logRoot.branch("Connecting data module"+(modules.size()>1?"s":"")+" to the main graph");
   stor.lockWrite();

   Map<AgeObject,Set<AgeRelationWritable>> invRelMap = new HashMap<AgeObject, Set<AgeRelationWritable>>();
   
   // invRelMap contains a map of external objects to sets of prepared inverse relations for new external relations
   
   if( connectDataModule( modules, stor, invRelMap, connLog) )
    connLog.log(Level.INFO, "Success");
   else
   {
    connLog.log(Level.ERROR, "Connection failed");
    return false;
   }
   
   
   LogNode semLog = logRoot.branch("Validating semantic");

   boolean vldRes = true;
   int n=0;
   for( ModMeta mm : modules )
   {
    n++;
    
    if( mm.module == null )
     continue;
    
    LogNode vldLog = semLog.branch("Processing module: "+n);
    
    boolean modRes = validator.validate(mm.module, semLog);
    
    if(modRes)
     vldLog.log(Level.INFO, "Success");
    else
     vldLog.log(Level.ERROR, "Validation failed");

    vldRes = vldRes && modRes;
   }
   
   if( vldRes )
    semLog.log(Level.INFO, "Success");
   else
    semLog.log(Level.ERROR, "Failed");

   res = res && vldRes;
   

   Map<AgeObject,Set<AgeRelationWritable>> detachedRelMap = new HashMap<AgeObject, Set<AgeRelationWritable>>();

   for( ModMeta mm : modules )
   {
    if(mm.origModule == null)
     continue;

    Collection<? extends AgeExternalRelationWritable> origExtRels = mm.origModule.getExternalRelations();

    if(origExtRels != null)
    {
     for(AgeExternalRelationWritable extRel : origExtRels)
     {
      AgeObject target = extRel.getTargetObject();

      Set<AgeRelationWritable> objectsRels = detachedRelMap.get(target);

      if(objectsRels == null)
       detachedRelMap.put(target, objectsRels = new HashSet<AgeRelationWritable>());

      objectsRels.add(extRel.getInverseRelation());
     }
    }
   }
   
   Set<AgeObject> affObjSet = new HashSet<AgeObject>();
   
   affObjSet.addAll( invRelMap.keySet() );
   affObjSet.addAll( detachedRelMap.keySet() );
   
   if( affObjSet.size() > 0 )
   {
    boolean invRelRes = true;
    LogNode invRelLog = logRoot.branch("Validating externaly related object semantic");
    
    for( AgeObject obj :  affObjSet )
    {
     LogNode objLogNode = invRelLog.branch("Validating object Id: "+obj.getId()+" Class: "+obj.getAgeElClass());
     
     if( validator.validateRelations(obj, invRelMap.get(obj), detachedRelMap.get(obj), objLogNode) )
      objLogNode.log(Level.INFO, "Success");
     else
      invRelRes = false;
    }
    
    if(invRelRes)
     invRelLog.log(Level.INFO, "Success");
    else
    {
     invRelLog.log(Level.ERROR, "Validation failed");
     return false;
    }

    res = res && invRelRes;
   }
   
   if( ! res )
    return false;
   
//   boolean storRes = true;
   LogNode storLog = logRoot.branch("Storing data");
    
   long ts = System.currentTimeMillis();
   
   for( ModMeta mm : modules )
   {
    mm.module.setVersion(ts);
    
    if( mm.origModule == null )
    {
     String id = null;
     
     do
     {
      id = Constants.dataModuleIDPrefix+IdGenerator.getInstance().getStringId(Constants.dataModuleIDDomain);
     }
     while( stor.hasDataModule(id) );
    
     mm.module.setId(id);
     
     for( AgeObjectWritable obj : mm.module.getObjects() )
     {
      if( obj.getId() == null )
      {
       do
       {
        id = Constants.localObjectIDPrefix+obj.getAgeElClass().getIdPrefix()+IdGenerator.getInstance().getStringId(Constants.objectIDDomain)+"-"+obj.getOriginalId()+"@"+mm.module.getId();
       }
       while( stor.hasObject(id) );
       
       obj.setId(id);
      }
     }
    }
   }
    
   try
   {
    if( modules.size() > 1 )
    {
     ArrayList<DataModuleWritable> modList = new ArrayList<DataModuleWritable>( modules.size() );
     
     for( ModMeta mm : modules )
      modList.add(mm.module);
     
     stor.storeDataModule(modList);
    }
    else
     stor.storeDataModule(modules.get(0).module);
    
    storLog.log(Level.INFO, "Success");
   }
   catch(Exception e)
   {
    storLog.log(Level.ERROR, "Data module storing failed: " + e.getMessage());
    res = false;
   }
   
   for( Map.Entry<AgeObject, Set<AgeRelationWritable>> me :  detachedRelMap.entrySet() )
    stor.removeRelations(me.getKey().getId(),me.getValue());

   for( Map.Entry<AgeObject, Set<AgeRelationWritable>> me :  invRelMap.entrySet() )
    stor.addRelations(me.getKey().getId(),me.getValue());

   n=0;
   for( ModMeta mm : modules )
   {
    DataModuleMeta m = mods.get(n++);
    
    m.setId(mm.module.getId());
    m.setVersion( mm.module.getVersion() );
   }
   
  }
  finally
  {
   stor.unlockWrite();
  }

  //Impute reverse relation and revalidate.

  return res;
 }



 private boolean reconnectDataModules(List<ModMeta> mods, Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject>> conn, AgeStorageAdm stor, LogNode logRoot)
 {
  
  LogNode logRecon = logRoot.branch("Reconnecting external object attributes");
  
  boolean res = true; 
  
  for( DataModule extDM : stor.getDataModules() )
  {
   Collection<? extends Attributed> attrs = extDM.getAttributed( new AttributedSelector()
   {
    @Override
    public boolean select(Attributed at)
    {
     return at instanceof AgeExternalObjectAttributeWritable;
    }
   });
   
   for( Attributed atb : attrs )
   {
    AgeExternalObjectAttributeWritable extObjAttr = (AgeExternalObjectAttributeWritable) atb;
    String refModId = extObjAttr.getValue().getDataModule().getId();

    int n = 0;
    for(ModMeta mm : mods)
    {
     n++;

     if(refModId.equals(mm.meta.getId()))
     {
      AgeObject replObj = null;

      
      lookup : for(ModMeta rfmm : mods)
      {
       if( rfmm.module != null )
       {
        for( AgeObject rfObj : rfmm.module.getObjects() )
        {
         if( extObjAttr.getTargetObjectId().equals(rfObj.getId())  )
         {
          replObj = rfObj;
          break lookup;
         }
        }
       }
      }
     
      if( replObj == null )
      {
       if(mm.module == null)
        logRecon.log(Level.ERROR, "Module " + n + " (ID='" + mm.id + "') is marked for deletion but some object (ID='" + extObjAttr.getValue().getId()
          + "') is referred by object attribute from module '" + extDM.getId() + "' of cluster '" + extDM.getClusterId() + "'");
       else
        logRecon.log(Level.ERROR, "Module " + n + " (ID='" + mm.id + "') is marked for update but some object (ID='" + extObjAttr.getValue().getId()
          + "') is referred by object attribute from module '" + extDM.getId() + "' of cluster '" + extDM.getClusterId() + "' and the reference can't be resolved anymore");
       
       res = false;
      }
      else
       conn.add( new Pair<AgeExternalObjectAttributeWritable, AgeObject>(extObjAttr, replObj) );
       
     }
    }
   }
  }
   
  if( res )
   logRecon.log(Level.INFO, "Success");
  else
   logRecon.log(Level.INFO, "Failed");
  
  return res;
 }
 
 

 private boolean connectDataFiles( DataModuleWritable dm, Map<String, FileAttachmentMeta> files )
 {
  for( AgeFileAttributeWritable fattr : dm.getFileAttributes() )
  {
   
  }
  
  return true;
 }
 
 private boolean checkRemovedDataFiles(String clustId, Map<String,FileAttachmentMeta> att4Del, Map<String,FileAttachmentMeta> att4G2L, AgeStorageAdm stor, LogNode logRoot)
 {
  
  LogNode logRecon = logRoot.branch("Reconnecting file attributes");
  
  boolean res = true; 
  
  for( DataModule extDM : stor.getDataModules() )
  {
   if( extDM.getClusterId().equals(clustId) )
    continue;
   
   Collection<? extends Attributed> attrs = extDM.getAttributed( new AttributedSelector()
   {
    @Override
    public boolean select(Attributed at)
    {
     return at instanceof AgeFileAttributeWritable;
    }
   });
   
   for( Attributed atb : attrs )
   {
    AgeFileAttributeWritable fileAttr = (AgeFileAttributeWritable) atb;
    
    if( stor.isFileIdGlobal(fileAttr.getFileID()) )
    {
     
     if( att4Del != null )
     {
      FileAttachmentMeta meta = att4Del.get( fileAttr.getFileReference() );

      if( meta != null && meta.isGlobal() )
      {
       res = false;
       logRecon.log(Level.ERROR, "File with ID '"+fileAttr.getFileReference()+"' is referred by the module '"+extDM.getId()+"' cluster '"+extDM.getClusterId()+"' and can't be deleted");
       break;
      }
     }
     
     if( att4G2L != null )
     {
      FileAttachmentMeta meta = att4G2L.get( fileAttr.getFileReference() );

      if( meta != null && meta.isGlobal() )
      {
       res = false;
       logRecon.log(Level.ERROR, "File with ID '"+fileAttr.getFileReference()+"' is referred by the module '"+extDM.getId()+"' cluster '"+extDM.getClusterId()+"' and can't limit visibility");
       break;
      }
     }
    }
    
   }
  }
   
  if( res )
   logRecon.log(Level.INFO, "Success");
  else
   logRecon.log(Level.INFO, "Failed");
  
  return res;
 }

 private boolean connectDataModule(List<ModMeta> mods, AgeStorageAdm stor, Map<AgeObject,Set<AgeRelationWritable>> invRelMap, LogNode connLog)
 {
  boolean res = true;
  
  LogNode extAttrLog = connLog.branch("Connecting external object attributes");
  boolean extAttrRes = true;

//  LogNode uniqLog = connLog.branch("Verifing object Id uniqueness");
//  boolean uniqRes = true;
  
  Stack<Attributed> attrStk = new Stack<Attributed>();
  
  int n=0;
  for( ModMeta mm : mods )
  {
   n++;
   
   if( mm.module == null )
    continue;
   
   LogNode extAttrModLog = extAttrLog.branch("Processing module: "+n);
   
   for( AgeObjectWritable obj : mm.module.getObjects() )
   {
    attrStk.clear();
    attrStk.push(obj);
    
    boolean mdres = connectExternalAttrs( attrStk, stor, mods, mm, extAttrModLog  );
    
    if( obj.getRelations() != null )
    {
     for( AgeRelationWritable rl : obj.getRelations() )
     {
      attrStk.clear();
      attrStk.push(rl);
      
      mdres = mdres && connectExternalAttrs( attrStk, stor, mods, mm, extAttrModLog  );
     }
    }
    
    if( mdres )
     extAttrModLog.log(Level.INFO, "Success");
    else
     extAttrModLog.log(Level.ERROR, "Failed");

    extAttrRes = extAttrRes && mdres;
   }
   
  }
  
  
  if( extAttrRes )
   extAttrLog.log(Level.INFO, "Success");
  else
   extAttrLog.log(Level.ERROR, "Failed");

  
  LogNode extRelLog = connLog.branch("Connecting external object relations");
  boolean extRelRes = true;
  
  n=0;
  for( ModMeta mm : mods )
  {
   n++;
   
   if( mm.module == null )
    continue;
   
   LogNode extRelModLog = extRelLog.branch("Processing module: "+n);
   
   boolean extModRelRes = true;
   
   for( AgeExternalRelationWritable exr : mm.module.getExternalRelations() )
   {
    String ref = exr.getTargetObjectId();
    
    AgeObjectWritable tgObj = (AgeObjectWritable)stor.getObjectById(ref);
    
    if( tgObj == null )
    {
     modloop : for( ModMeta refmm : mods )
     {
      if( refmm == mm || refmm.module == null )
       continue;
      
      for( AgeObjectWritable candObj : refmm.module.getObjects() )
      {
       if( candObj.getId() != null && candObj.getId().equals(ref) )
       {
        tgObj = candObj;
        break modloop;
       }
      }
     }
    }
    
    if( tgObj == null )
    {
     extModRelRes = false;
     extRelModLog.log(Level.ERROR,"Invalid external relation: '"+ref+"'. Target object not found."
       +" Module: "+n
       +" Source object: '"+exr.getSourceObject().getId()
       +"' (Class: "+exr.getSourceObject().getAgeElClass()
       +", Order: "+exr.getSourceObject().getOrder()+"). Relation: "+exr.getAgeElClass()+" Order: "+exr.getOrder());
    }
    else
    {
      if( ! exr.getAgeElClass().isWithinRange(tgObj.getAgeElClass()) )
      {
       extModRelRes = false;
       extRelModLog.log(Level.ERROR,"External relation target object's class is not within range. Target object: '"+ref
         +"' (Class: "+tgObj.getAgeElClass()
         +"'). Module: "+n+" Source object: '"+exr.getSourceObject().getId()
         +"' (Class: "+exr.getSourceObject().getAgeElClass()
         +", Order: "+exr.getSourceObject().getOrder()+"). Relation: "+exr.getAgeElClass()+" Order: "+exr.getOrder());
      }
      else
      {
       AgeRelationClass invRCls = exr.getAgeElClass().getInverseRelationClass();
       
       boolean invClassOk=false;
       if( invRCls != null )
       {
        if(invRCls.isCustom())
        {
         extModRelRes = false;
         extRelModLog.log(Level.ERROR,"Class of external inverse relation can't be custom. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Module: "+n+" Source object: '"+exr.getSourceObject().getId()
           +"' (Class: "+exr.getSourceObject().getAgeElClass()
           +", Order: "+exr.getSourceObject().getOrder()+"). Relation: '"+exr.getAgeElClass()+"' Order: "+exr.getOrder()
           +". Inverse relation: "+invRCls);
        }
        else if( ! invRCls.isWithinDomain(tgObj.getAgeElClass()) )
        {
         extModRelRes = false;
         extRelModLog.log(Level.ERROR,"Target object's class is not within domain of inverse relation. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Module: "+n+" Source object: '"+exr.getSourceObject().getId()
           +"' (Class: "+exr.getSourceObject().getAgeElClass()
           +", Order: "+exr.getSourceObject().getOrder()+"). Relation: '"+exr.getAgeElClass()+"' Order: "+exr.getOrder()
           +". Inverse relation: "+invRCls);
        }
        else if( ! invRCls.isWithinRange(exr.getSourceObject().getAgeElClass()) )
        {
         extModRelRes = false;
         extRelModLog.log(Level.ERROR,"Source object's class is not within range of inverse relation. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Module: "+n+" Source object: '"+exr.getSourceObject().getId()
           +"' (Class: "+exr.getSourceObject().getAgeElClass()
           +", Order: "+exr.getSourceObject().getOrder()+"). Relation: '"+exr.getAgeElClass()+"' Order: "+exr.getOrder()
           +". Inverse relation: "+invRCls);
        }
        else
         invClassOk=true;
       }
       
       if( invClassOk )
       {
        AgeExternalRelationWritable invRel = tgObj.getAgeElClass().getSemanticModel().createExternalRelation(tgObj, exr.getSourceObject().getId(), invRCls);
        invRel.setTargetObject(exr.getSourceObject());
        invRel.setInferred(true);
        
        Set<AgeRelationWritable> rels = invRelMap.get(tgObj);
        
        if( rels == null )
        {
         rels = new HashSet<AgeRelationWritable>();
         invRelMap.put(tgObj, rels);
        }
        
        rels.add(invRel);
       }
       
       
       exr.setTargetObject(tgObj);
      }
    }
    
   }
   
   extRelRes = extRelRes && extModRelRes;
   
  }
  
  if( extRelRes )
   extRelLog.log(Level.INFO, "Success");
  else
   extRelLog.log(Level.ERROR, "Failed");


  res = res && extRelRes;

  
  return res;
 }

 
 private boolean connectExternalAttrs( Stack<Attributed> atStk, AgeStorageAdm stor, List<ModMeta> mods, ModMeta cmod, LogNode log )
 {
  boolean res = true;
  
  Attributed atInst = atStk.peek();
  
  if( atInst.getAttributes() == null )
   return true;
  
  for( AgeAttribute attr : atInst.getAttributes() )
  {
   if( attr instanceof AgeExternalObjectAttributeWritable )
   {
    AgeExternalObjectAttributeWritable extAttr = (AgeExternalObjectAttributeWritable)attr;
    
    String ref = extAttr.getTargetObjectId();
    
    AgeObject tgObj = stor.getObjectById( ref );
    
    if( tgObj == null )
    {
     for( ModMeta mm : mods )
     {
      if( mm.module == cmod || mm.module == null )
       continue;
      
      for( AgeObjectWritable candObj : mm.module.getObjects() )
      {
       if( candObj.getId() != null && candObj.getId().equals(ref) )
       {
        tgObj = candObj;
        break;
       }
      }
     }
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
   res = res && connectExternalAttrs(atStk,stor, mods, cmod, log);
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
}
