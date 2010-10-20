package uk.ac.ebi.age.mng;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeExternalObjectAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelation;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.impl.AgeTab2AgeConverterImpl;
import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.storage.AgeStorageAdm;
import uk.ac.ebi.age.storage.RelationResolveException;
import uk.ac.ebi.age.validator.AgeSemanticValidator;
import uk.ac.ebi.age.validator.impl.AgeSemanticValidatorImpl;

public class SubmissionManager
{
 private static SubmissionManager instance = new SubmissionManager();
 
 public static SubmissionManager getInstance()
 {
  return instance;
 }
 
 private AgeTabSyntaxParser ageTabParser = new AgeTabSyntaxParserImpl();
 private AgeTab2AgeConverter converter = new AgeTab2AgeConverterImpl();
 private AgeSemanticValidator validator = new AgeSemanticValidatorImpl();
 
 public SubmissionWritable prepareSubmission( String text, SubmissionContext context, AgeStorageAdm stor, LogNode logRoot )
 {
  AgeTabSubmission atSbm=null;
  
  LogNode atLog = logRoot.branch("Parsing AgeTab");
  try
  {
   atSbm =  ageTabParser.parse(text);
   atLog.log(Level.INFO, "Success");
  }
  catch(ParserException e)
  {
   atLog.log(Level.ERROR, "Parsing failed: "+e.getMessage()+". Row: "+e.getLineNumber()+". Col: "+e.getColumnNumber());
   return null;
  }

  LogNode convLog = logRoot.branch("Converting AgeTab to Age submission");
  SubmissionWritable ageSbm = converter.convert(atSbm, SemanticManager.getInstance().getContextModel(context), convLog );
  
  if( ageSbm != null )
   convLog.log(Level.INFO, "Success");
  else
  {
   convLog.log(Level.ERROR, "Conversion failed");
   return null;
  }
  
  
  try
  {
   LogNode connLog = logRoot.branch("Connecting submission to the graph");
   stor.lockWrite();

   if( ! connectSubmission(ageSbm,stor,connLog) )
    connLog.log(Level.INFO, "Success");
   else
   {
    connLog.log(Level.ERROR, "Connection failed");
    return null;
   }
   
   LogNode semLog = logRoot.branch("Validating semantic");

   if(validator.validate(ageSbm, semLog))
    convLog.log(Level.INFO, "Success");
   else
   {
    convLog.log(Level.ERROR, "Validation failed");
    return null;
   }

  }
  finally
  {
   stor.unlockWrite();
  }

  //Impute reverse relation and revalidate.

  return ageSbm;
 }

 private boolean connectSubmission(SubmissionWritable sbm, AgeStorageAdm stor, Map<AgeObject,Collection<AgeRelation>> invRelMap, LogNode connLog)
 {
  boolean res = true;
  
//  Collection<String> invIds = null;
  Map<Attributed,Collection<AgeExternalObjectAttribute>> extAttrMap = new HashMap<Attributed, Collection<AgeExternalObjectAttribute>>();
  
  LogNode extAttrLog = connLog.branch("Connecting external object attributes");
  boolean extAttrRes = true;

  LogNode uniqLog = connLog.branch("Verifing object Id uniqueness");
  boolean uniqRes = true;
  
  Stack<Attributed> attrStk = new Stack<Attributed>();
  
  for( AgeObjectWritable obj : sbm.getObjects() )
  {
   if( stor.hasObject(obj.getId()) )
   {
    uniqRes=false;
    uniqLog.log(Level.ERROR, "Id: '"+obj.getId()+"' is already used in the database. Class: "+obj.getAgeElClass()+" Order: "+obj.getOrder());
   }
   
   attrStk.clear();
   attrStk.push(obj);
   extAttrRes = extAttrRes && connectExternalAttrs( attrStk, stor, extAttrLog  );
  }
  
  res = uniqRes && extAttrRes;
  
  if( extAttrRes )
   extAttrLog.log(Level.INFO, "Success");

  if( uniqRes )
   uniqLog.log(Level.INFO, "Success");
  
  boolean extRelRes = true;
  if( sbm.getExternalRelations() != null )
  {
   LogNode extRelLog = connLog.branch("Connecting external object relations");

   
   for( AgeExternalRelationWritable exr : sbm.getExternalRelations() )
   {
    String ref = exr.getTargetObjectId();
    
    AgeObject tgObj = stor.getObjectById(ref);
    
    if( tgObj == null )
    {
     extRelRes = false;
     extRelLog.log(Level.ERROR,"Invalid external relation: '"+ref+"'. Target object not found. Source object: '"+exr.getSourceObject().getId()
       +"' (Class: "+exr.getSourceObject().getAgeElClass()
       +", Order: "+exr.getSourceObject().getOrder()+"). Relation: "+exr.getAgeElClass()+" Order: "+exr.getOrder());
    }
    
    exr.setTargetObject(tgObj);
   }
  }
  
  if( invIds != null )
   throw new RelationResolveException(exr.getOrder(),exr.getSourceObject().getOrder(),"Can't resolve external relation: "+exr.getTargetObjectId());
  
  return false;
 }

 
 private boolean connectExternalAttrs( Stack<Attributed> atStk, AgeStorageAdm stor, LogNode log )
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
     AgeObject obj  = (AgeObject)atStk.get(0);
     
     String attrName = attr.getAgeElClass().getName();
     
     if( atStk.size() > 1 )
     {
      attrName = atStk.get(1).getAttributedClass().getName();
      for( int i = 2; i < atStk.size(); i++ )
       attrName += "["+atStk.get(i).getAttributedClass().getName()+"]";
      
      attrName+= "["+attr.getAgeElClass().getName()+"]";
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
   connectExternalAttrs(atStk,stor,log);
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
