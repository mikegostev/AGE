package uk.ac.ebi.age.mng;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.DataModule;
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
import uk.ac.ebi.age.storage.exeption.AttachmentIOException;
import uk.ac.ebi.age.validator.AgeSemanticValidator;
import uk.ac.ebi.age.validator.impl.AgeSemanticValidatorImpl;

import com.pri.util.Extractor;
import com.pri.util.Pair;
import com.pri.util.collection.CollectionsUnion;
import com.pri.util.collection.ExtractorCollection;

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
  int ord;
 }
 
 private static Extractor<ModMeta, DataModuleWritable> modExtractor = new Extractor<ModMeta, DataModuleWritable>()
 {
  @Override
  public DataModuleWritable extract(ModMeta obj)
  {
   return obj.module;
  }
 };
 
 private static Extractor<ModMeta, String> idExtractor = new Extractor<ModMeta, String>()
 {
  @Override
  public String extract(ModMeta obj)
  {
   return obj.origModule.getId();
  }
 };

 
 private static class FileMeta
 {
  FileAttachmentMeta origFile;
  FileAttachmentMeta newFile;
 }
 
 private static class ClustMeta
 {
  List<ModMeta> incomeMods = new ArrayList<SubmissionManager.ModMeta>();
 
  List<ModMeta> mod4Use = new ArrayList<SubmissionManager.ModMeta>();
 
  List<ModMeta> mod4Ins = new ArrayList<SubmissionManager.ModMeta>();
  
  Map<String,ModMeta> mod4Upd = new HashMap<String, SubmissionManager.ModMeta>();
  Map<String,ModMeta> mod4Del = new HashMap<String, SubmissionManager.ModMeta>();
  Map<String,ModMeta> mod4Hld = new HashMap<String, SubmissionManager.ModMeta>();

  Map<String,FileAttachmentMeta> att4Ins = new HashMap<String, FileAttachmentMeta>();
  Map<String,FileMeta> att4Upd = new HashMap<String, FileMeta>();
  Map<String,FileAttachmentMeta> att4Del = new HashMap<String, FileAttachmentMeta>();
  Map<String,FileMeta> att4G2L = new HashMap<String, FileMeta>();
  Map<String,FileMeta> att4L2G = new HashMap<String, FileMeta>();
  Map<String,FileAttachmentMeta> att4Hld = new HashMap<String, FileAttachmentMeta>();
  Map<String,FileAttachmentMeta> att4Use = new HashMap<String, FileAttachmentMeta>();

  public String id;
 }
 
 public static SubmissionManager getInstance()
 {
  return instance;
 }
 
 private AgeTabSyntaxParser ageTabParser = new AgeTabSyntaxParserImpl();
 private AgeTab2AgeConverter converter = new AgeTab2AgeConverterImpl();
 private AgeSemanticValidator validator = new AgeSemanticValidatorImpl();
 
 @SuppressWarnings("unchecked")
 public boolean storeSubmission( SubmissionMeta sMeta,  SubmissionContext context, AgeStorageAdm stor, LogNode logRoot )
 {
  
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
  
  ClustMeta cstMeta = new ClustMeta();
  cstMeta.id = sMeta.getId();
  
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
  
  
  boolean res = true;
  

  int n=0;
  
  if( sMeta.getDataModules() != null )
  {
   for(DataModuleMeta dm : sMeta.getDataModules())
   {
    n++;

    ModMeta mm = new ModMeta();
    mm.meta = dm;
    mm.ord = n;

    cstMeta.incomeMods.add(mm);

    if(dm.isForUpdate())
    {
     if(origSbm == null)
     {
      logRoot.log(Level.ERROR, "Module " + n + " is marked for update but submission is not in UPDATE mode");
      res = false;
      continue;
     }

     sMeta.setSubmitter(origSbm.getSubmitter());
     sMeta.setSubmissionTime(origSbm.getSubmissionTime());
     
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
      cstMeta.mod4Del.put(mm.meta.getId(),mm);
     else
     {
      cstMeta.mod4Upd.put(mm.meta.getId(),mm);
      cstMeta.mod4Use.add(mm);
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
     
     cstMeta.mod4Ins.add(mm);
     cstMeta.mod4Use.add(mm);
    }

   }
  }
  
  if( origSbm != null && origSbm.getDataModules() != null )
  {
   for( DataModuleMeta odm : origSbm.getDataModules() )
   {
    String modID = odm.getId();

    ModMeta updMod = cstMeta.mod4Upd.get(modID);
    
    if( updMod != null )
    {
     updMod.meta.setSubmissionTime( odm.getSubmissionTime() );
     updMod.meta.setSubmitter( odm.getSubmitter() );
    }
    
    if( updMod == null && ! cstMeta.mod4Del.containsKey(modID) )
    {
     ModMeta mm = new ModMeta();
     mm.meta = odm;
     mm.origModule = stor.getDataModule(modID);
     mm.ord=-1;

     
     cstMeta.mod4Use.add(mm);
     cstMeta.mod4Hld.put(modID, mm);
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
      fileNode.log(Level.ERROR, "File " + (n + 1) + " is marked for deletion or visibility change but submission is not in UPDATE mode");
      res = false;
      continue;
     }

     if(origFm == null)
     {
      fileNode.log(Level.ERROR, "File " + (n + 1) + " is marked for deletion or visibility change but it doesn't exist within the submission");
      res = false;
      continue;
     }
     
     if( fm.isGlobal() != origFm.isGlobal() )
     {
      FileMeta fmeta = new FileMeta();
      
      fmeta.newFile = fm;
      fmeta.origFile = origFm;
      
      if( fm.isGlobal() )
       cstMeta.att4L2G.put(fm.getOriginalId(), fmeta);
      else
       cstMeta.att4G2L.put(fm.getOriginalId(), fmeta);
      
     }
     else
      cstMeta.att4Del.put(fm.getOriginalId(), fm);
 
    }
    else //Files for insert, update and for update+visibility change
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
      cstMeta.att4Ins.put(fm.getOriginalId(), fm);
      cstMeta.att4Use.put(fm.getOriginalId(), fm);
     }
     else
     {
      FileMeta fmeta = new FileMeta();
      
      fmeta.newFile = fm;
      fmeta.origFile = origFm;

      
      cstMeta.att4Upd.put(fm.getOriginalId(), fmeta);

      if( fm.isGlobal() != origFm.isGlobal() )
      {
       if( fm.isGlobal() )
        cstMeta.att4L2G.put(fm.getOriginalId(), fmeta);
       else
        cstMeta.att4G2L.put(fm.getOriginalId(), fmeta);
      }
     }
    }
   }
  }
  
  if( origSbm != null && origSbm.getAttachments() != null )
  {
   for( FileAttachmentMeta fm : origSbm.getAttachments() )
   {
    if(! cstMeta.att4Del.containsKey(fm.getOriginalId()) )
    {
     cstMeta.att4Use.put(fm.getOriginalId(), fm);

     if( ! cstMeta.att4Upd.containsKey(fm.getOriginalId()) )
      cstMeta.att4Hld.put(fm.getDescription(), fm);
    }
   }
  }
  
  for( n=0; n < cstMeta.incomeMods.size(); n++)
  {
   ModMeta mm = cstMeta.incomeMods.get(n);
   
   boolean modRes = true;
   LogNode modNode = logRoot.branch("Processing module: " + (n+1) );
   
   if( mm.meta.getText() == null )
   {
    modNode.log(Level.INFO, "Module is marked for deletion. Skiping");
    continue;
   }
   
   LogNode atLog = modNode.branch("Parsing AgeTab");
   try
   {
    mm.atMod = ageTabParser.parse(mm.meta.getText());
    atLog.log(Level.INFO, "Success");
   }
   catch(ParserException e)
   {
    atLog.log(Level.ERROR, "Parsing failed: " + e.getMessage() + ". Row: " + e.getLineNumber() + ". Col: " + e.getColumnNumber());
    modRes = false;
    continue;
   }
   
   LogNode convLog = modNode.branch("Converting AgeTab to Age data module");
   mm.module = converter.convert(mm.atMod, SemanticManager.getInstance().getContextModel(context), convLog );
   
   if( mm.module != null )
    convLog.log(Level.INFO, "Success");
   else
   {
    convLog.log(Level.ERROR, "Conversion failed");
    modRes = false;
    continue;
   }
   
   
   
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
   stor.lockWrite();

   
   // XXX connection to main graph
   
   if( ! checkUniqObjects(cstMeta, stor, logRoot) )
    return false;


 
   
   Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject> > extAttrConnector = new ArrayList<Pair<AgeExternalObjectAttributeWritable,AgeObject>>();
   Collection<Pair<AgeExternalRelationWritable, AgeObjectWritable> > relConnections = null;
   Map<AgeObjectWritable,Set<AgeRelationWritable> > relationDetachMap = null;
   
   if( cstMeta.mod4Upd.size() != 0 || ( cstMeta.mod4Ins.size() !=0 && cstMeta.mod4Del.size() != 0 ) )
   {
    relConnections = new ArrayList<Pair<AgeExternalRelationWritable,AgeObjectWritable>>();
    relationDetachMap = new HashMap<AgeObjectWritable, Set<AgeRelationWritable>>();
    
    if( ! reconnectExternalObjectAttributes(cstMeta, extAttrConnector, stor, logRoot))
     return false;
    
    
    if( ! reconnectExternalRelations(cstMeta, relConnections, relationDetachMap, stor, logRoot) )
     return false;
   }
   
   Map<AgeObjectWritable,Set<AgeRelationWritable>> invRelMap = new HashMap<AgeObjectWritable, Set<AgeRelationWritable>>();
   // invRelMap contains a map of external objects to sets of prepared inverse relations for new external relations
   
   if( !connectNewExternalRelations(cstMeta, stor, invRelMap, logRoot) )
   {
    return false;
   }
   
   if( !connectNewObjectAttributes(cstMeta, stor, logRoot) )
   {
    return false;
   }

   
   if( cstMeta.att4Del.size() != 0 ||  cstMeta.att4G2L.size() != 0 )
   {
    if( ! checkRemovedDataFiles(cstMeta, stor, logRoot) )
     return false;
   }

  
   
   LogNode semLog = logRoot.branch("Validating semantic");

   boolean vldRes = true;
   n=0;
   for( ModMeta mm : cstMeta.incomeMods )
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
     
     if( validator.validateRelations(obj, invRelMap.get(obj), relationDetachMap.get(obj), objLogNode) )
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
   
   
   long ts = System.currentTimeMillis();
   
   if( cstMeta.id == null )
   {
    String id = null;
    
    do
    {
     id = Constants.submissionIDPrefix+IdGenerator.getInstance().getStringId(Constants.clusterIDDomain);
    }
    while( submissionDB.hasSubmission(id) );
   
    cstMeta.id = id;
    sMeta.setId(id);
   }
   
   for( ModMeta mm : cstMeta.incomeMods )
   {
    mm.module.setVersion(ts);
    mm.module.setClusterId(cstMeta.id);
    
    if( mm.module.getId() == null )
    {
     String id = null;
     
     do
     {
      id = Constants.dataModuleIDPrefix+IdGenerator.getInstance().getStringId(Constants.dataModuleIDDomain);
     }
     while( stor.hasDataModule(id) );
    
     mm.module.setId(id);
     mm.meta.setId(id);
     
    }
    
    if( mm.module != null )
    {
     for( AgeObjectWritable obj : mm.module.getObjects() )
     {
      if( obj.getId() == null )
      {
       String id=null;
       
       do
       {
        id = Constants.localObjectIDPrefix+obj.getAgeElClass().getIdPrefix()+IdGenerator.getInstance().getStringId(Constants.objectIDDomain)
        +"-"+obj.getOriginalId()+"@"+mm.module.getId();
       }
       while( stor.hasObject(id) );
       
       obj.setId(id);
      }
     }
    }
   }
   
   
   connectIncomingModulesToFiles(cstMeta, stor, logRoot);
   
   Collection< Pair<AgeFileAttributeWritable,String> > fileConn = new ArrayList< Pair<AgeFileAttributeWritable,String> >();
   reconnectLocalModulesToFiles(cstMeta, fileConn, stor, logRoot);
   
   for( FileMeta fatm : cstMeta.att4G2L.values() )
    fatm.newFile.setId(stor.makeLocalFileID(fatm.origFile.getOriginalId(), cstMeta.id));
    
   for( FileMeta fatm : cstMeta.att4L2G.values() )
    fatm.newFile.setId(stor.makeGlobalFileID(fatm.origFile.getOriginalId()));

   for( FileAttachmentMeta fam : cstMeta.att4Ins.values() )
   {
    if( ! fam.isGlobal() ) // We generated IDs for the global files earlier
     fam.setId(stor.makeLocalFileID(fam.getOriginalId(), cstMeta.id));
   }
   
   if( cstMeta.att4Del.size() > 0 )
   {
    LogNode fdelLog = logRoot.branch("Deleting files");
    
    for( FileAttachmentMeta fam : cstMeta.att4Del.values() )
    {
     fdelLog.log(Level.INFO, "Deleting file: '"+fam.getOriginalId()+"' (ID="+fam.getId()+")");
     
     stor.deleteAttachment(fam.getId());
     fdelLog.log(Level.INFO, "Success");
    }
   }
   
   if( cstMeta.att4Ins.size() > 0 )
   {
    LogNode finsLog = logRoot.branch("Storing files");
    
    for( FileAttachmentMeta fam : cstMeta.att4Ins.values() )
    {
     finsLog.log(Level.INFO, "Storing file: '"+fam.getOriginalId()+"' (ID="+fam.getId()+")");
     
     try
     {
      stor.storeAttachment(fam.getId(), (File)fam.getAux());
     }
     catch(AttachmentIOException e)
     {
      finsLog.log(Level.ERROR, e.getMessage());
      finsLog.log(Level.ERROR, "Failed");

      res = false;
      return false;
     }
     
     finsLog.log(Level.INFO, "Success");
    }
   }
   
   if( cstMeta.att4Upd.size() > 0 )
   {
    LogNode fupdLog = logRoot.branch("Updating files");
    
    for( FileMeta fam : cstMeta.att4Upd.values() )
    {
     fupdLog.log(Level.INFO, "Updating file: '"+fam.origFile.getOriginalId()+"' (ID="+fam.origFile.getId()+")");
     
     try
     {
      stor.storeAttachment(fam.origFile.getId(), (File)fam.newFile.getAux());
     }
     catch(AttachmentIOException e)
     {
      fupdLog.log(Level.ERROR, e.getMessage());
      fupdLog.log(Level.ERROR, "Failed");

      res = false;
      return false;
     }
     
     fupdLog.log(Level.INFO, "Success");
    }
   }

   if( cstMeta.att4G2L.size() > 0 )
   {
    LogNode fupdLog = logRoot.branch("Changing file visibility (global to local)");
    
    for( FileMeta fam : cstMeta.att4G2L.values() )
    {
     fupdLog.log(Level.INFO, "Renaming file: '"+fam.origFile.getOriginalId()+"' old ID="+fam.origFile.getId()+", new ID="+fam.newFile.getId());
     
     try
     {
      stor.renameAttachment(fam.origFile.getId(), fam.newFile.getId());
     }
     catch(AttachmentIOException e)
     {
      fupdLog.log(Level.ERROR, e.getMessage());
      fupdLog.log(Level.ERROR, "Failed");

      res = false;
      return false;
     }
     
     fupdLog.log(Level.INFO, "Success");
    }
   }

   if( cstMeta.att4L2G.size() > 0 )
   {
    LogNode fupdLog = logRoot.branch("Changing file visibility (local to global)");
    
    for( FileMeta fam : cstMeta.att4L2G.values() )
    {
     fupdLog.log(Level.INFO, "Renaming file: '"+fam.origFile.getOriginalId()+"' old ID="+fam.origFile.getId()+", new ID="+fam.newFile.getId());
     
     try
     {
      stor.renameAttachment(fam.origFile.getId(), fam.newFile.getId());
     }
     catch(AttachmentIOException e)
     {
      fupdLog.log(Level.ERROR, e.getMessage());
      fupdLog.log(Level.ERROR, "Failed");

      res = false;
      return false;
     }
     
     fupdLog.log(Level.INFO, "Success");
    }
   }
 
   
   LogNode updtLog = logRoot.branch("Updating storage");

   try
   {
    if( cstMeta.mod4Upd.size() > 0 || cstMeta.mod4Del.size() > 0 )
    {
     
     stor.update( 
       new CollectionsUnion<DataModuleWritable>( 
        new ExtractorCollection<ModMeta, DataModuleWritable>(cstMeta.mod4Upd.values(), modExtractor),
        new ExtractorCollection<ModMeta, DataModuleWritable>(cstMeta.mod4Ins, modExtractor)
       ),
       
       new CollectionsUnion<String>( 
        new ExtractorCollection<ModMeta, String>(cstMeta.mod4Upd.values(), idExtractor),
        new ExtractorCollection<ModMeta, String>(cstMeta.mod4Del.values(), idExtractor)
       ) );
     
     updtLog.log(Level.INFO, "Success");
    }
   }
   catch (Exception e)
   {
    updtLog.log(Level.ERROR, e.getMessage());
    updtLog.log(Level.ERROR, "Failed");
    
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
    fc.getFirst().setFileId( fc.getSecond() );
   
   for( Map.Entry<AgeObjectWritable, Set<AgeRelationWritable>> me :  relationDetachMap.entrySet() )
    for( AgeRelationWritable rel : me.getValue() )
     me.getKey().removeRelation(rel);
    
    //    stor.removeRelations(me.getKey().getId(),me.getValue());

   for( Map.Entry<AgeObjectWritable, Set<AgeRelationWritable>> me :  invRelMap.entrySet() )
    for( AgeRelationWritable rel : me.getValue() )
     me.getKey().addRelation(rel);
    
   for( ModMeta dm : cstMeta.incomeMods )
   {
    if( dm.module != null && dm.module.getExternalRelations() != null  )
    {
     for( AgeExternalRelationWritable rel : dm.module.getExternalRelations() )
      rel.getInverseRelation().setInverseRelation(rel);
    }
   }
    //    stor.addRelations(me.getKey().getId(),me.getValue());

  }
  finally
  {
   stor.unlockWrite();
  }

  //Impute reverse relation and revalidate.

  return res;
 }

 
 private boolean connectNewExternalRelations( ClustMeta cstMeta, AgeStorageAdm stor, Map<AgeObjectWritable,Set<AgeRelationWritable>> invRelMap, LogNode rootNode )
 {

  LogNode extRelLog = rootNode.branch("Connecting external object relations");
  boolean extRelRes = true;

  int n = 0;
  for(ModMeta mm : cstMeta.incomeMods)
  {
   n++;

   if(mm.module == null)
    continue;

   LogNode extRelModLog = extRelLog.branch("Processing module: " + n);

   boolean extModRelRes = true;

   for(AgeExternalRelationWritable exr : mm.module.getExternalRelations())
   {
    if( exr.getTargetObject() != null )
     continue;
    
    String ref = exr.getTargetObjectId();

    AgeObjectWritable tgObj = (AgeObjectWritable) stor.getObjectById(ref);

    if(tgObj == null || cstMeta.mod4Del.containsKey(tgObj.getDataModule().getId()) || cstMeta.mod4Upd.containsKey(tgObj.getDataModule().getId()) )
    {
     modloop: for(ModMeta refmm : cstMeta.incomeMods) // Old modules can't hold this ID due to obj ID  uniqueness
     {
      if(refmm == mm || refmm.module == null)
       continue;

      for(AgeObjectWritable candObj : refmm.module.getObjects())
      {
       if(candObj.getId() != null && candObj.getId().equals(ref))
       {
        tgObj = candObj;
        break modloop;
       }
      }
     }
    }

    if(tgObj == null)
    {
     extModRelRes = false;
     extRelModLog.log(Level.ERROR, "Invalid external relation: '" + ref + "'. Target object not found." + " Module: " + n + " Source object: '"
       + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: " + exr.getSourceObject().getOrder()
       + "). Relation: " + exr.getAgeElClass() + " Order: " + exr.getOrder());
    }
    else
    {
     if(!exr.getAgeElClass().isWithinRange(tgObj.getAgeElClass()))
     {
      extModRelRes = false;
      extRelModLog.log(Level.ERROR,
        "External relation target object's class is not within range. Target object: '" + ref + "' (Class: " + tgObj.getAgeElClass() + "'). Module: " + n
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
          + "'). Module: " + n + " Source object: '" + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: "
          + exr.getSourceObject().getOrder() + "). Relation: '" + exr.getAgeElClass() + "' Order: " + exr.getOrder() + ". Inverse relation: " + invRCls);
       }
       else if(!invRCls.isWithinDomain(tgObj.getAgeElClass()))
       {
        extModRelRes = false;
        extRelModLog.log(Level.ERROR,
          "Target object's class is not within domain of inverse relation. Target object: '" + ref + "' (Class: " + tgObj.getAgeElClass() + "'). Module: "
            + n + " Source object: '" + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: "
            + exr.getSourceObject().getOrder() + "). Relation: '" + exr.getAgeElClass() + "' Order: " + exr.getOrder() + ". Inverse relation: " + invRCls);
       }
       else if(!invRCls.isWithinRange(exr.getSourceObject().getAgeElClass()))
       {
        extModRelRes = false;
        extRelModLog.log(Level.ERROR,
          "Source object's class is not within range of inverse relation. Target object: '" + ref + "' (Class: " + tgObj.getAgeElClass() + "'). Module: "
            + n + " Source object: '" + exr.getSourceObject().getId() + "' (Class: " + exr.getSourceObject().getAgeElClass() + ", Order: "
            + exr.getSourceObject().getOrder() + "). Relation: '" + exr.getAgeElClass() + "' Order: " + exr.getOrder() + ". Inverse relation: " + invRCls);
       }
       else
        invClassOk = true;
      }

      if(invClassOk)
      {
       AgeExternalRelationWritable invRel = tgObj.getAgeElClass().getSemanticModel().createExternalRelation(tgObj, exr.getSourceObject().getId(), invRCls);
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

   extRelRes = extRelRes && extModRelRes;

  }

  if(extRelRes)
   extRelLog.log(Level.INFO, "Success");
  else
   extRelLog.log(Level.ERROR, "Failed");

  return extRelRes;
 }


 private boolean checkUniqObjects( ClustMeta  cstMeta, AgeStorageAdm stor, LogNode logRoot )
 {
  boolean res = true;
  
  LogNode logUniq = logRoot.branch("Checking object identifiers uniquness");
  
  Map<String,AgeObject> objIDs = new HashMap<String,AgeObject>();
  Map<DataModule,ModMeta> modMap = new HashMap<DataModule, SubmissionManager.ModMeta>();
  
  for( ModMeta mm : cstMeta.mod4Use )
  {
   modMap.put(mm.module, mm);
   
   for( AgeObject obj : mm.module.getObjects() )
   {
    if( obj.getId() != null )
    {
     AgeObject clashObj = objIDs.get(obj.getId());
     
     if( clashObj != null )
     {
      res = false;
      
      ModMeta clashMM = modMap.get(clashObj.getDataModule());
      
      logUniq.log(Level.ERROR, "Object identifiers clash (ID='"+obj.getId()+"'). Object 1: module "
        +( (mm.ord!=-1?mm.ord+" ":"(existing) ") + (mm.meta.getId()!=null?("ID='"+mm.meta.getId()+"' "):"") + "Row: " + obj.getOrder()  )
        +" Object 2: module "
        +( (clashMM.ord!=-1?clashMM.ord+" ":"(existing) ") + (clashMM.meta.getId()!=null?("ID='"+clashMM.meta.getId()+"' "):"") + "Row: " + clashObj.getOrder()  )
        );
     }
     else
     {
      clashObj = stor.getObjectById(obj.getId());
      
      if( clashObj != null )
      {
       res = false;
       
       logUniq.log(Level.ERROR, "Object identifiers clash (ID='"+obj.getId()+"'). Object 1: module "
         +( (mm.ord!=-1?mm.ord+" ":"(existing) ") + (mm.meta.getId()!=null?("ID='"+mm.meta.getId()+"' "):"") + "Row: " + obj.getOrder()  )
         +" Object 2: cluster ID='"
         +( clashObj.getDataModule().getClusterId() + "' module ID='"+clashObj.getDataModule().getId()+"' " + "Row: " + clashObj.getOrder()  )
         );
      }
      else
       objIDs.put(obj.getId(), obj);
     }
    }
   }
   
  }
  
  if( res )
   logUniq.log(Level.INFO, "Success");
  else
   logUniq.log(Level.INFO, "Failed");

  return res;
 }
 
 private boolean reconnectExternalRelations( ClustMeta  cstMeta, Collection<Pair<AgeExternalRelationWritable, AgeObjectWritable>> relConn, 
   Map<AgeObjectWritable,Set<AgeRelationWritable>> detachedRelMap, AgeStorageAdm stor, LogNode logRoot)
 {
  LogNode logRecon = logRoot.branch("Reconnecting external relations");
  
  boolean res = true; 
  
  for( ModMeta mm : cstMeta.incomeMods )
  {
   if( mm.origModule == null ) //Skipping new modules, processing only update/delete modules (where original data are going away)
    continue;
   
   Collection<? extends AgeExternalRelationWritable> origExtRels = mm.origModule.getExternalRelations();

   if(origExtRels != null)
   {
    for(AgeExternalRelationWritable extRel : origExtRels)
    {
     AgeExternalRelationWritable invrsRel = extRel.getInverseRelation();
     AgeObjectWritable target = extRel.getTargetObject();
     
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
      String tgObjId = invrsRel.getTargetObjectId();
      
      lookup : for(ModMeta rfmm : cstMeta.incomeMods) // looking for alternative resolution among the new modules
      {
       if( rfmm.module != null ) // Skipping deleted modules
       {
        for( AgeObjectWritable rfObj : rfmm.module.getObjects() )
        {
         if( tgObjId.equals(rfObj.getId())  )
         {
          replObj = rfObj;
          break lookup;
         }
        }
       }
      }
    
      if( replObj == null )
      {
        logRecon.log(Level.ERROR, "Module " + mm.ord + " (ID='" + mm.meta.getId() + "') is marked for "
          +(mm.module == null?"deletion":"update")+" but some object (ID='" + extRel.getTargetObjectId()
          + "' Module ID: '"+extRel.getTargetObject().getDataModule().getId()+"' Cluster ID: '"
          +extRel.getTargetObject().getDataModule().getClusterId()+"') holds the relation of class  '" + invrsRel.getAgeElClass() 
          + "' with object '"  +invrsRel.getTargetObjectId() + "'");
       res = false;
      }
      else if( ! invrsRel.getAgeElClass().isWithinRange(replObj.getAgeElClass()) )
      {
        res = false;

        logRecon.log(Level.ERROR, "Module " + mm.ord + " (ID='" + mm.meta.getId() + "') is marked for "
          +(mm.module == null?"deletion":"update")+" but some object (ID='" + extRel.getTargetObjectId()
          + "' Module ID: '"+extRel.getTargetObject().getDataModule().getId()+"' Cluster ID: '"
          +extRel.getTargetObject().getDataModule().getClusterId()+"') holds the relation of class  '" + invrsRel.getAgeElClass() 
          + "' with object '"  +invrsRel.getTargetObjectId() + "' and the replacement object (Class: "
          +replObj.getAgeElClass()+") is not within realtion's class range");

      }
      else if( ! invrsRel.getAgeElClass().getInverseRelationClass().isWithinRange(target.getAgeElClass()) ) 
      {
        res = false;

        logRecon.log(Level.ERROR, "Module " + mm.ord + " (ID='" + mm.meta.getId() + "') is marked for "
          +(mm.module == null?"deletion":"update")+" but some object (ID='" + extRel.getTargetObjectId()
          + "' Module ID: '"+extRel.getTargetObject().getDataModule().getId()+"' Cluster ID: '"
          +target.getDataModule().getClusterId()+"') holds the relation of class  '" + invrsRel.getAgeElClass() 
          + "' with object '"  +invrsRel.getTargetObjectId() + "' and reverse relation can't be establishes as source object's class (Class: "
          +target.getAgeElClass()+") is not within inverse realtion's class range");

      }
      else if( ! invrsRel.getAgeElClass().getInverseRelationClass().isWithinDomain(replObj.getAgeElClass()) ) 
      {
        res = false;

        logRecon.log(Level.ERROR, "Module " + mm.ord + " (ID='" + mm.meta.getId() + "') is marked for "
          +(mm.module == null?"deletion":"update")+" but some object (ID='" + extRel.getTargetObjectId()
          + "' Module ID: '"+extRel.getTargetObject().getDataModule().getId()+"' Cluster ID: '"
          +target.getDataModule().getClusterId()+"') holds the relation of class  '" + invrsRel.getAgeElClass() 
          + "' with object '"  +invrsRel.getTargetObjectId() + "' and reverse relation can't be establishes as target object's class (Class: "
          +replObj.getAgeElClass()+") is not within inverse realtion's class domain");

      }
      else
      {
       AgeExternalRelationWritable dirRel = null;
       
       if( replObj.getDataModule().getExternalRelations() != null )
       {
        for( AgeExternalRelationWritable cndRel : replObj.getDataModule().getExternalRelations() )
        {
         if( cndRel.getAgeElClass().equals(extRel.getAgeElClass()) && cndRel.getTargetObjectId().equals(target.getId()) && invrsRel.getTargetObjectId().equals(replObj.getId()) )
         {
          dirRel=cndRel;
          break;
         }
        }
       }
       
       if( dirRel == null )
       {
        dirRel = replObj.getAgeElClass().getSemanticModel().createExternalRelation(replObj, target.getId(), invrsRel.getAgeElClass().getInverseRelationClass());

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
   
  }

  
  if( res )
   logRecon.log(Level.INFO, "Success");
  else
   logRecon.log(Level.INFO, "Failed");
  
  return res;

 }
 
 private boolean reconnectExternalObjectAttributes( ClustMeta  cstMeta, Collection<Pair<AgeExternalObjectAttributeWritable, AgeObject>> attrConn, AgeStorageAdm stor, LogNode logRoot)
 {
  
  LogNode logRecon = logRoot.branch("Reconnecting external object attributes");
  
  boolean res = true; 
  
  for( DataModule extDM : stor.getDataModules() )
  {
   if( cstMeta.mod4Del.containsKey(extDM.getId()) || cstMeta.mod4Upd.containsKey(extDM.getId()) )
    continue;
   
   
   for( Attributed atb : extDM.getExternalObjectAttributes() )
   {
    AgeExternalObjectAttributeWritable extObjAttr = (AgeExternalObjectAttributeWritable) atb;
    String refModId = extObjAttr.getValue().getDataModule().getId();

  
    if( cstMeta.mod4Del.containsKey(refModId) || cstMeta.mod4Upd.containsKey(refModId) )
    {
     AgeObject replObj = null;
     
     lookup : for(ModMeta rfmm : cstMeta.incomeMods) // looking from alternative resolution among the new modules
     {
      if( rfmm.module != null ) // Skipping deleted modules
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
      ModMeta errMod = null;
      
      if( (errMod = cstMeta.mod4Del.get(refModId)) != null )
       logRecon.log(Level.ERROR, "Module " + errMod.ord + " (ID='" + errMod.meta.getId() + "') is marked for deletion but some object (ID='" + extObjAttr.getValue().getId()
         + "') is referred by object attribute from the module '" + extDM.getId() + "' of the cluster '" + extDM.getClusterId() + "'");
      else
      {
       errMod = cstMeta.mod4Upd.get(refModId);
       logRecon.log(Level.ERROR, "Module " + errMod.ord + " (ID='" + errMod.meta.getId() + "') is marked for update but some object (ID='" + extObjAttr.getValue().getId()
         + "') is referred by object attribute from module '" + extDM.getId() + "' of cluster '" + extDM.getClusterId() + "' and the reference can't be resolved anymore");
      }
      
      res = false;
     }
     else
      attrConn.add( new Pair<AgeExternalObjectAttributeWritable, AgeObject>(extObjAttr, replObj) );

    }
   }
  }
   
  if( res )
   logRecon.log(Level.INFO, "Success");
  else
   logRecon.log(Level.INFO, "Failed");
  
  return res;
 }
 
 

 @SuppressWarnings("unchecked")
 private boolean connectIncomingModulesToFiles(ClustMeta cMeta, AgeStorageAdm stor, LogNode logRoot ) //Identifiers must be generated by this moment
 {
  boolean res = true;

  LogNode logCon = logRoot.branch("Connecting file attributes to files");

  
  for(ModMeta mm : new CollectionsUnion<ModMeta>( cMeta.mod4Ins, cMeta.mod4Upd.values() ))
  {
   for(AgeFileAttributeWritable fattr : mm.module.getFileAttributes())
   {
    FileAttachmentMeta fmt = cMeta.att4Use.get(fattr.getFileReference());

    if(fmt != null)
    {
     fattr.setFileId(fmt.getId());
    }
    else
    {
     String gId = stor.makeGlobalFileID(fattr.getFileReference());

     if(stor.getAttachment(gId) != null)
      fattr.setFileId(gId);
     else
     {
      AttributeClassRef clRef = fattr.getClassRef();

      logCon.log(Level.ERROR, "Reference to file can't be resolved. Module: " + mm.ord
        + (mm.meta.getId() != null ? (" (ID='" + mm.meta.getId() + "')") : "") + " Attribute: row: " + fattr.getOrder() + " col: " + clRef.getOrder());

      res = false;
     }
    }
   }
  }

  if( res )
   logCon.log(Level.INFO, "Success");
  else
   logCon.log(Level.INFO, "Failed");

  
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
    if( stor.isFileIdGlobal(fileAttr.getFileID()) )
    {

     FileAttachmentMeta meta = cMeta.att4Del.get(fileAttr.getFileReference());

     if(meta != null && meta.isGlobal())
     {
      res = false;
      logRecon.log(Level.ERROR, "File with ID '" + fileAttr.getFileReference() + "' is referred by the module '"
        + extDM.getId() + "' cluster '" + extDM.getClusterId() + "' and can't be deleted");
      continue;
     }

     FileMeta fm = cMeta.att4G2L.get(fileAttr.getFileReference());

     if( fm != null )
     {
      res = false;
      logRecon.log(Level.ERROR, "File with ID '" + fileAttr.getFileReference() + "' is referred by the module '"
        + extDM.getId() + "' cluster '" + extDM.getClusterId() + "' and can't limit visibility");
      continue;
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

 private boolean reconnectLocalModulesToFiles( ClustMeta cMeta, Collection<Pair<AgeFileAttributeWritable,String>> fileConn, AgeStorageAdm stor, LogNode reconnLog )
 {
  boolean res = true;
  
  for( ModMeta mm : cMeta.mod4Hld.values() )
  {
   for( AgeFileAttributeWritable fattr : mm.origModule.getFileAttributes() )
   {
    FileAttachmentMeta fam = cMeta.att4Use.get(fattr.getFileReference());
    
    if( fam == null )
    {
     String gid = stor.makeGlobalFileID(fattr.getFileReference());
     
     if( stor.getAttachment(gid) != null )
      fileConn.add(new Pair<AgeFileAttributeWritable, String>(fattr,gid));
     else
     {
      reconnLog.log(Level.ERROR, "Can't connect file attribute: '"+fattr.getFileReference()+"'. Module: "+mm.ord+" (ID='"+mm.meta.getId()
        +"') Row: "+fattr.getOrder()+" Col: "+fattr.getClassRef().getOrder());
      res = false;
     }
      
    }
    else if( ! fam.getId().equals(fattr.getFileID()) )
     fileConn.add(new Pair<AgeFileAttributeWritable, String>(fattr,fam.getId()));
   }
  }
  
  return res;
 }
 
 private boolean connectNewObjectAttributes(ClustMeta cstMeta, AgeStorageAdm stor, LogNode logRoot)
 {
  
  LogNode connLog = logRoot.branch("Connecting data module"+(cstMeta.incomeMods.size()>1?"s":"")+" to the main graph");

  LogNode extAttrLog = connLog.branch("Connecting external object attributes");
  boolean extAttrRes = true;

  
  Stack<Attributed> attrStk = new Stack<Attributed>();
  
  int n=0;
  for( ModMeta mm : cstMeta.incomeMods )
  {
   n++;
   
   if( mm.module == null )
    continue;
   
   LogNode extAttrModLog = extAttrLog.branch("Processing module: "+n);
   
   for( AgeObjectWritable obj : mm.module.getObjects() )
   {
    attrStk.clear();
    attrStk.push(obj);
    
    boolean mdres = connectExternalAttrs( attrStk, stor, cstMeta, mm, extAttrModLog  );
    
    if( obj.getRelations() != null )
    {
     for( AgeRelationWritable rl : obj.getRelations() )
     {
      attrStk.clear();
      attrStk.push(rl);
      
      mdres = mdres && connectExternalAttrs( attrStk, stor, cstMeta, mm, extAttrModLog  );
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


  return extAttrRes;
 }

 
 private boolean connectExternalAttrs( Stack<Attributed> atStk, AgeStorageAdm stor, ClustMeta cstMeta , ModMeta cmod, LogNode log )
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
    
    if( tgObj != null && ( cstMeta.mod4Del.containsKey(tgObj.getId()) || cstMeta.mod4Upd.containsKey(tgObj.getId()) ) ) //We don't want to get dead objects 
     tgObj = null;
    
    if( tgObj == null ) // Trying to resolve to the new objects
    {
     for( ModMeta mm : cstMeta.incomeMods )
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
}
