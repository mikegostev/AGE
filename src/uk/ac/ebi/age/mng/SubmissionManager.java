package uk.ac.ebi.age.mng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttribute;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Attributed;
import uk.ac.ebi.age.model.SubmissionContext;
import uk.ac.ebi.age.model.writable.AgeExternalObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.impl.AgeTab2AgeConverterImpl;
import uk.ac.ebi.age.parser.impl.AgeTabSyntaxParserImpl;
import uk.ac.ebi.age.storage.AgeStorageAdm;
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
   LogNode connLog = logRoot.branch("Connecting submission to the main graph");
   stor.lockWrite();

   Map<AgeObject,Collection<AgeRelationWritable>> invRelMap = new HashMap<AgeObject, Collection<AgeRelationWritable>>();
   
   if( connectSubmission( ageSbm, stor, invRelMap, connLog) )
    connLog.log(Level.INFO, "Success");
   else
   {
    connLog.log(Level.ERROR, "Connection failed");
    return null;
   }
   
   
   LogNode semLog = logRoot.branch("Validating semantic");

   if(validator.validate(ageSbm, semLog))
    semLog.log(Level.INFO, "Success");
   else
   {
    semLog.log(Level.ERROR, "Validation failed");
    return null;
   }

   if( invRelMap.size() > 0 )
   {
    LogNode invRelLog = connLog.branch("Validating inverse external relations semantic");
    
    boolean res = true;
    for( Map.Entry<AgeObject, Collection<AgeRelationWritable>> me :  invRelMap.entrySet() )
    {
     LogNode objLogNode = invRelLog.branch("Validating object Id: "+me.getKey().getId()+" Class: "+me.getKey().getAgeElClass());
     
     if( validator.validateRelations(me.getKey(),me.getValue(),objLogNode) )
      objLogNode.log(Level.INFO, "Success");
     else
      res = false;
    }
    
    if(res)
     invRelLog.log(Level.INFO, "Success");
    else
    {
     invRelLog.log(Level.ERROR, "Validation failed");
     return null;
    }

    LogNode storLog = connLog.branch("Storing submission");
    try
    {
     stor.storeSubmission(ageSbm);
     storLog.log(Level.INFO, "Success");
    }
    catch(Exception e)
    {
     storLog.log(Level.ERROR, "Submission storing failed: "+e.getMessage());
     return null;
    }
    
    for( Map.Entry<AgeObject, Collection<AgeRelationWritable>> me :  invRelMap.entrySet() )
     stor.addRelations(me.getKey().getId(),me.getValue());

   }

  }
  finally
  {
   stor.unlockWrite();
  }

  //Impute reverse relation and revalidate.

  return ageSbm;
 }

 private boolean connectSubmission(SubmissionWritable sbm, AgeStorageAdm stor, Map<AgeObject,Collection<AgeRelationWritable>> invRelMap, LogNode connLog)
 {
  boolean res = true;
  
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
    else
    {
      if( ! exr.getAgeElClass().isWithinRange(tgObj.getAgeElClass()) )
      {
       extRelRes = false;
       extRelLog.log(Level.ERROR,"External relation target object's class is not within range. Target object: '"+ref
         +"' (Class: "+tgObj.getAgeElClass()
         +"'). Source object: '"+exr.getSourceObject().getId()
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
         extRelRes = false;
         extRelLog.log(Level.ERROR,"Class of external inverse relation can't be custom. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Source object: '"+exr.getSourceObject().getId()
           +"' (Class: "+exr.getSourceObject().getAgeElClass()
           +", Order: "+exr.getSourceObject().getOrder()+"). Relation: '"+exr.getAgeElClass()+"' Order: "+exr.getOrder()
           +". Inverse relation: "+invRCls);
        }
        else if( ! invRCls.isWithinDomain(tgObj.getAgeElClass()) )
        {
         extRelRes = false;
         extRelLog.log(Level.ERROR,"Target object's class is not within domain of inverse relation. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Source object: '"+exr.getSourceObject().getId()
           +"' (Class: "+exr.getSourceObject().getAgeElClass()
           +", Order: "+exr.getSourceObject().getOrder()+"). Relation: '"+exr.getAgeElClass()+"' Order: "+exr.getOrder()
           +". Inverse relation: "+invRCls);
        }
        else if( ! invRCls.isWithinRange(exr.getSourceObject().getAgeElClass()) )
        {
         extRelRes = false;
         extRelLog.log(Level.ERROR,"Source object's class is not within range of inverse relation. Target object: '"+ref
           +"' (Class: "+tgObj.getAgeElClass()
           +"'). Source object: '"+exr.getSourceObject().getId()
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
        
        Collection<AgeRelationWritable> rels = invRelMap.get(tgObj);
        
        if( rels == null )
        {
         rels = new ArrayList<AgeRelationWritable>(5);
         invRelMap.put(tgObj, rels);
        }
        
        rels.add(invRel);
       }
       
       
       exr.setTargetObject(tgObj);
      }
    }
    
   }
  
   if( extRelRes )
    extRelLog.log(Level.INFO, "Success");
  }
  
  res = uniqRes && extRelRes;

  
  return res;
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
