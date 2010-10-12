package uk.ac.ebi.age.parser.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.age.log.LogNode;
import uk.ac.ebi.age.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeExternalRelation;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectPropertyWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.AgeTabValue;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ClassReference;
import uk.ac.ebi.age.parser.ConvertionException;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.SemanticException;

public class AgeTabSemanticValidatorImpl2 implements AgeTab2AgeConverter
{
// private AttrAttchRel attributeAttachmentClass;
 
 @Override
 public SubmissionWritable convert(AgeTabSubmission data, ContextSemanticModel sm, LogNode log ) throws SemanticException, ConvertionException
 {
  boolean result = true;
  
  SubmissionWritable res = sm.createSubmission();
  
  Map<AgeClass, Map<String,AgeObjectWritable>> classMap = new HashMap<AgeClass, Map<String,AgeObjectWritable>>();
  Map<AgeClass, Collection<AgeObjectWritable>> prototypeMap = new HashMap<AgeClass, Collection<AgeObjectWritable>>();
  
  Map<String,AgeObjectWritable> objectMap = null;
  
  Map< BlockHeader, AgeClass > blk2classMap = new HashMap<BlockHeader, AgeClass>();
  
  for( BlockHeader hdr : data.getBlocks() )
  {
   ClassReference colHdr = hdr.getClassColumnHeader();

   LogNode blkLog = log.branch("Processing block for class "+colHdr.getName());
   
   
   if( colHdr.getQualifiers() != null )
   {
    blkLog.log(Level.WARN, "Class reference must not be qualified. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());
//    throw new SemanticException(colHdr.getRow(),colHdr.getCol(),"Class reference must not be qualified");
   }
   
   AgeClass cls = null;

   if( colHdr.isCustom() )
   {
    if( sm.getContext().isCustomClassAllowed() )
    {
     AgeClass parent = null;
     
     if( colHdr.getParentClass() != null )
     {
      parent = sm.getDefinedAgeClass(colHdr.getParentClass());
      
      if( parent == null )
      {
       blkLog.log(Level.ERROR, "Defined class '"+colHdr.getParentClass()+"' (used as superclass) is not found. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol() );
       result = false;
       continue;
      }
     }
      
     cls = sm.getOrCreateCustomAgeClass(colHdr.getName(), null, parent);
    }
    else
    {
     blkLog.log(Level.ERROR, "Custom classes are not allowed within this context. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());
     result = false;
     continue;
//     throw new SemanticException(colHdr.getRow(),colHdr.getCol(),"Custom classes are not allowed within this context");
    }
   }
   else
   {
    cls = sm.getDefinedAgeClass( colHdr.getName() );
    
    if( cls == null )
    {
     blkLog.log(Level.ERROR, "Class '"+colHdr.getName()+"' not found. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());
     result = false;
     continue;
//     throw new SemanticException(colHdr.getRow(),colHdr.getCol(),"Class '"+colHdr.getName()+"' not found");
    }
   }
  
   blk2classMap.put(hdr, cls);
   
   objectMap = classMap.get(cls);
   
   if(objectMap == null)
    classMap.put( cls, objectMap=new HashMap<String, AgeObjectWritable>() );
   
   
   for( AgeTabObject atObj : data.getObjects(hdr) )
   {
    if( atObj.isPrototype() )
     continue;
    
    AgeObjectWritable obj = objectMap.get(atObj.getId());
    
    if( obj == null )
    {
     obj = sm.createAgeObject(atObj.isIdDefined()?atObj.getId():null, cls);
     obj.setOrder( atObj.getRow() );
     
     objectMap.put(atObj.getId(), obj);
    }
   }
  }
  
  List<ValueConverter> convs = new ArrayList<ValueConverter>(20);
  
  for( Map.Entry<BlockHeader, AgeClass> me : blk2classMap.entrySet() )
  {
   LogNode subLog = log.branch("Creating value converters for class '"+me.getValue().getName()+"'. Block at :"+me.getKey().getClassColumnHeader().getRow());
   
   if( ! createConvertors( me.getKey(), me.getValue(), convs, sm, classMap, subLog ) )
   {
    subLog.log(Level.ERROR,"Convertors creation failed");
    result = false;
   }
   
   objectMap = classMap.get( me.getValue() );
  
   subLog = log.branch("Converting values for class '"+me.getValue().getName()+"'. Block at :"+me.getKey().getClassColumnHeader().getRow());
 
   for( AgeTabObject atObj : data.getObjects(me.getKey()) )
   {
    AgeObjectWritable obj = objectMap.get(atObj.getId());
    
    LogNode objLog = subLog.branch("Processing object: "+atObj.getId());
    
    if( obj == null ) // It seems it must not happen
    {
     obj = sm.createAgeObject(null, me.getValue());
     obj.setOrder( atObj.getRow() );
    }
    
//    for( ValueConverter cnv : convs )
//    {
//     List<AgeTabValue> vals = atObj.getValues(cnv.getClassReference());
//     
//     cnv.convert(obj,vals);
//    }

    boolean hasValue=true;
    int ln=0;
    while( hasValue )
    {
     hasValue=false;
     
     for( ValueConverter cnv : convs )
     {
      
      List<AgeTabValue> vals = atObj.getValues(cnv.getClassReference());
      

       if(vals == null || vals.size() <= ln)
       {
        try
        {
         cnv.convert(obj, null);
        }
        catch (ConvertionException e)
        {
         objLog.log(Level.ERROR, "Empty value processing error: "+e.getMessage());
        }
       }
       else
       {
        hasValue = true;

        AgeTabValue val = vals.get(ln);

        LogNode colLog = objLog.branch("Processing column: " + cnv.getClassReference().getCol() + ". Value: '" + val.getValue() + "'");


        try
        {
         cnv.convert(obj, val);
         colLog.log(Level.INFO, "Ok");
        }
        catch (ConvertionException e) 
        {
         colLog.log(Level.ERROR, "Conversion error: "+e.getMessage());
         result = false;
        }

       }

      
      
     }
     
     ln++;
    }
    
    if( atObj.isPrototype() )
    {
     Collection<AgeObjectWritable> protoList = prototypeMap.get(me.getValue());
     
     if( protoList == null )
      protoList = new ArrayList<AgeObjectWritable>(3);

     protoList.add(obj);
    }
    else
    {
     res.addObject(obj);
     obj.setSubmission(res);
    }
   }
   

  }

//  attributeAttachmentClass = new AttrAttchRel(sm.getAttributeAttachmentClass());
  
  finalizeValues( res.getObjects() );
  
  for( Collection<AgeObjectWritable> pObjs : prototypeMap.values() )
   finalizeValues( pObjs );
  

  AgeClass lastClass=null;
  Collection<AgeObjectWritable> protos=null;
  for( AgeObjectWritable obj : res.getObjects() )
  {
   if( lastClass != obj.getAgeElClass() )
   {
    lastClass=obj.getAgeElClass();
    protos = prototypeMap.get(lastClass);
   }
   
   if( protos != null )
   {
    for( AgeObjectWritable po : protos )
    {
     for( AgeAttributeWritable prat : po.getAttributes() )
      obj.addAttribute(prat.createClone());
    }
   }
  }
  
//  validateData(res);
  
  imputeReverseRelations( res );
  
  return res;
 }
 
 
// private AgeClass getCustomAgeClass(ClassReference colHdr, ContextSemanticModel sm)
// {
//  AgeClass parent = null;
//  
//  if( colHdr.getParentClass() != null )
//  {
//   parent = sm.getDefinedAgeClass(colHdr.getParentClass());
//   
//   if( parent == null )
//    return null;
//  }
//   
//  return  sm.getOrCreateCustomAgeClass(colHdr.getName(),null,parent);
// }
 
 private void finalizeValues( Collection<AgeObjectWritable> data )
 {
  class AttrInfo
  {
   AgeAttributeWritable attr;
   AgeObjectWritable obj;
   
   boolean isBool=false;
   boolean isInt=false;
   boolean isReal=false;
   
   int     intValue;
   boolean boolValue;
   double  realValue;
  }
  
  class AttrClassInfo
  {
   AgeAttributeClassWritable atClass;

   boolean isBool=true;
   boolean isInt=true;
   boolean isReal=true;
   
   List<AttrInfo> attributes = new ArrayList<AttrInfo>();
  }

  Map<AgeClass, Map<AgeAttributeClass,AttrClassInfo > > wMap = new HashMap<AgeClass, Map<AgeAttributeClass,AttrClassInfo >>();
  
  Map<AgeAttributeClass, AttrClassInfo > cClassMap = null;
  AgeClass cClass = null;
  
  for( AgeObjectWritable obj : data )
  {
   
   if( obj.getAgeElClass() != cClass )
   {
    cClass=obj.getAgeElClass();
    
    cClassMap = wMap.get(cClass);
    
    if( cClassMap == null )
    {
     cClassMap = new HashMap<AgeAttributeClass, AttrClassInfo>();
     wMap.put(cClass, cClassMap);
    }
   }
   
   for( AgeAttributeWritable attr : obj.getAttributes() )
   {
    attr.finalizeValue();
    
    if( attr.getAgeElClass().getDataType() == DataType.GUESS )
    {
     AttrClassInfo atcInfo = cClassMap.get(attr.getAgeElClass());
     
     if(atcInfo == null)
     {
      atcInfo = new AttrClassInfo();
      atcInfo.atClass = (AgeAttributeClassWritable)attr.getAgeElClass();
      
      cClassMap.put(atcInfo.atClass, atcInfo);
     }
     
     AttrInfo aInf = new AttrInfo();
     aInf.attr=attr;
     aInf.obj=obj;
     
     atcInfo.attributes.add(aInf);
     
     String value = attr.getValue().toString().trim();
     
     if( atcInfo.isBool )
     {
      if( value.equalsIgnoreCase("true") )
      {
       aInf.isBool = true;
       aInf.boolValue=true;
      }
      else if( value.equalsIgnoreCase("false") )
      {
       aInf.isBool = true;
       aInf.boolValue=false;
      }
      else
       atcInfo.isBool=false;
     }
     
     if( ! aInf.isBool )
     {
      if( atcInfo.isInt )
      {
       try
       {
        aInf.intValue = Integer.parseInt( value );
        aInf.isInt=true;
       }
       catch(Exception e)
       {
        atcInfo.isInt=false;
       }
      }
      
      if( atcInfo.isReal )
      {
       try
       {
        aInf.realValue = Double.parseDouble( value );
        aInf.isReal=true;
       }
       catch(Exception e)
       {
        atcInfo.isReal=false;
       }
      }
     }
    }
   }
  }
  
  for( Map<AgeAttributeClass,AttrClassInfo > actMap : wMap.values() )
  {
   for( AttrClassInfo acInfo : actMap.values() )
   {
    DataType typ;
    
    if( acInfo.isBool )
     acInfo.atClass.setDataType( typ = DataType.BOOLEAN );
    else if( acInfo.isInt )
     acInfo.atClass.setDataType( typ = DataType.INTEGER );
    else if( acInfo.isReal )
     acInfo.atClass.setDataType( typ = DataType.REAL );
    else
     acInfo.atClass.setDataType( typ = DataType.STRING );
    
    if( typ != DataType.STRING)
    {
     for( AttrInfo ai : acInfo.attributes )
     {
      ai.obj.removeAttribute(ai.attr);
      AgeAttributeWritable nAttr = ai.obj.createAgeAttribute(acInfo.atClass);
      
      nAttr.setOrder(ai.attr.getOrder());
      
      if( typ == DataType.BOOLEAN )
       nAttr.setBooleanValue(ai.boolValue);
      else if( typ == DataType.INTEGER )
       nAttr.setIntValue(ai.intValue);
      else if( typ == DataType.REAL )
       nAttr.setDoubleValue(ai.realValue);
     }
    }
   }
  }
  
 }
 
 

 private void imputeReverseRelations( SubmissionWritable data )
 {
  for( AgeObjectWritable obj : data.getObjects() )
  {
   
   for( AgeRelationWritable rl : obj.getRelations() )
   {
    if( rl instanceof AgeExternalRelation )
     continue;
    
    AgeRelationClass invClass = rl.getAgeElClass().getInverseRelationClass();
    
    if( invClass == null )
     continue;
    
    boolean found=false;
    for( AgeRelationWritable irl : rl.getTargetObject().getRelations() )
    {
     if( irl.getAgeElClass().equals(invClass) && irl.getTargetObject() == obj )
     {
      found=true;
      break;
     }
    }
    
    if( ! found )
     rl.getTargetObject().createRelation(obj, invClass).setInferred(true);
   }
  }
 }

// private void validateData( Submission data ) throws RestrictionException
// {
//  for( AgeObject obj : data.getObjects() )
//   IsInstanceOfRestriction.isInstanceOf(obj, obj.getAgeElClass());
//  
////  for( SubmissionBlock blk : data.getSubmissionBlocks() )
////  {
////   for( AgeObject obj : blk.getObjects() )
////    IsInstanceOfRestriction.isInstanceOf(obj, obj.getAgeXClass());
////  }
// }

/* 
 private void isInstanceOf(AgeObject obj, AgeClass cls) throws ConvertionException
 {
  for( AgeClass supcls : cls.getSuperClasses() )
   isInstanceOf(obj, supcls);
  
  for(AgeRestriction rest : cls.getRestrictions() )
  {
   RestrictionValidator rvld = rest.getValidator();
   
   for( AgeRelation rel : obj.getRelations() )
     rvld.validate( rel );
   
   if( ! rvld.isSatisfied() )
    throw new ConvertionException(obj, rest, rvld.getErrorMessage() );
  }
  
  for(AgeRestriction rest : cls.getAttributeRestrictions() )
  {
   RestrictionValidator rvld = rest.getValidator();
   
   for( AgeAttribute rel : obj.getAttributes() )
   {
    attributeAttachmentClass.setAttribute( rel );
    rvld.validate( attributeAttachmentClass );
   }
   
   if( ! rvld.isSatisfied() )
    throw new ConvertionException(obj, rest, rvld.getErrorMessage() );
  }

 }
*/
// private static class ObjectBlock
// {
//  AgeClass ageClass;
//  BlockHeader header;
//
//  public ObjectBlock(BlockHeader header, AgeClass ageClass)
//  {
//   this.header = header;
//   this.ageClass = ageClass;
//  }
// }

 /*
 private static class AttrAttchRel implements AgeRelation
 {
  private AgeAttribute attr;
  private AgeRelationClass relCls;
  
  AttrAttchRel( AgeRelationClass rc )
  {
   relCls=rc;
  }
  
  public AgeRelationClass getRelationClass()
  {
   return relCls;
  }

  public void setAttribute(AgeAttribute at)
  {
   attr=at;
  }

  public AgeAbstractObject getTargetObject()
  {
   return attr;
  }
  
 }
*/
 
 private AgeAttributeClass getCustomAttributeClass( ClassReference cr , AgeClass aCls, ContextSemanticModel sm, LogNode log)
 {
   if(!sm.getContext().isCustomAttributeClassAllowed())
   {
    log.log(Level.ERROR, "Custom attribure class (" + cr.getName() + ") is not allowed within this context. Row: "+cr.getRow()+" Col: "+cr.getCol() );
    return null;
//    throw new SemanticException(cr.getRow(), cr.getCol(), "Custom attribure class (" + cr.getName() + ") is not allowed within this context.");
   }
   
   AgeAttributeClass attrClass = sm.getCustomAgeAttributeClass(cr.getName(), aCls);

   String typeName = cr.getFlagValue(typeFlag);

   DataType type = DataType.GUESS;

   if(typeName != null)
   {
    try
    {
     type = DataType.valueOf(typeName);
    }
    catch(Exception e)
    {
     log.log(Level.ERROR, "Invalid type name: " + typeName+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null;
//     throw new SemanticException(cr.getRow(), cr.getCol(), "Invalid type name: " + typeName);
    }
   }
   else
   {
    if(attrClass != null)
     type = attrClass.getDataType();
   }

   AgeAttributeClass parent = null;

   if( cr.getParentClass() != null )
   {
    parent = sm.getDefinedAgeAttributeClass(cr.getParentClass());
    
    if( parent == null )
    {
     log.log(Level.ERROR, "Defined attribute class '"+cr.getParentClass()+"' (used as superclass) is not found. Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null; 
    }
   }
    
   attrClass = sm.getOrCreateCustomAgeAttributeClass(cr.getName(), type, aCls, parent);

   
   if(attrClass.getDataType() != type)
   {
    log.log(Level.ERROR, "Data type ('" + type + "') mismatches with the previous definition: " + attrClass.getDataType()+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
    return null; 
//    throw new SemanticException(cr.getRow(), cr.getCol(), "Data type ('" + type + "') mismatches with the previous definition: " + attrClass.getDataType());
   }
   
   
   return attrClass;
 }
 
 private int addConverter( List<ValueConverter> convs, ValueConverter cnv )
 {
  int i=0;
  for( ValueConverter exstC : convs )
  {
   if( exstC.getClassReference().equals( cnv.getClassReference() ) )
   {
    convs.add(new InvalidColumnConvertor(cnv.getClassReference()));
    return i;
   }
   
   i++;
//   if( exstC.getProperty() == cnv.getProperty() && exstC.getQualifiedProperty() == cnv.getQualifiedProperty() && cnv.getProperty() != null )
//    throw new SemanticException(cnv.getClassReference().getRow(), cnv.getClassReference().getCol(),
//      "Column header duplicates header at column "+exstC.getClassReference().getCol());
  }
  
  convs.add(cnv);
  return -1;
 }
 
 private boolean createConvertors( BlockHeader blck, AgeClass blkCls, List<ValueConverter> convs, ContextSemanticModel sm,
   Map<AgeClass, Map<String,AgeObjectWritable>> classMap, LogNode log ) throws SemanticException
 {
  boolean result = true;
  
  convs.clear();
  
  for( ClassReference attHd : blck.getColumnHeaders() )
  {
   if( attHd == null )
   {
    addConverter(convs, new EmptyColumnConvertor(attHd) );
    continue;
   }
   
   List<ClassReference> qList = attHd.getQualifiers();
   if( qList != null && qList.size() > 0 )
   {
    ClassReference qualif = qList.get(qList.size()-1);
    
    if( qualif.getQualifiers() != null )
    {
     log.log(Level.ERROR, "A qualifier reference must not be qualified ifself. Use syntax attr[qual1][qual2]. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
     //throw new SemanticException(attHd.getRow(), attHd.getCol(), "A qualifier reference must not be qualified ifself. Use syntax attr[qual1][qual2]");
    }
    
    ValueConverter hostConverter = null;
    for( int i=convs.size()-1; i >= 0; i-- )
    {
     ValueConverter vc = convs.get(i);
     
     ClassReference cr = vc.getClassReference();
     
     if( cr == null || ! attHd.isQualifierFor(cr) )
      continue;
     else
     {
      hostConverter=vc;
      break;
     }
    }
    
    if( hostConverter == null )
    {
     log.log(Level.ERROR, "A qualifier must follow to a qualified property. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "A qualifier must follow to a qualified property.");
    }

    
    if( qualif.isCustom() && ! sm.getContext().isCustomQualifierAllowed() )
    {
     log.log(Level.ERROR, "Custom qualifier ("+qualif.getName()+") is not allowed within this context. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "Custom qualifier ("+qualif.getName()+") is not allowed within this context.");
    }
    
    AgeAttributeClass qClass = null;
    
    if( qualif.isCustom() )
    {
     qClass = getCustomAttributeClass(qualif, blkCls, sm, log);
    
     if( qClass == null )
     {
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
     }
    }
    else
    {
     qClass=sm.getDefinedAgeAttributeClass(qualif.getName());
     
     if( qClass == null )
     {
      log.log(Level.ERROR, "Unknown attribute class (qualifier): '"+qualif.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
//      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Unknown attribute class (qualifier): '"+qualif.getName()+"'");
     }
    }
    
    int dupCol = addConverter(convs, new QualifierConvertor( attHd, qClass, hostConverter ) );
    
    if( dupCol != -1 )
    {
     log.log(Level.ERROR, "Column header duplicates header at column "+convs.get(dupCol).getClassReference().getCol()
       +". Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     result = false;
    }
    
    continue;
   }
   
   if( attHd.isCustom() )
   {
    String rangeClassName = attHd.getFlagValue(rangeFlag);
    
    
    if( rangeClassName != null )
    {
     if( ! sm.getContext().isCustomRelationClassAllowed() )
     {
      log.log(Level.ERROR, "Custom relation class ("+attHd.getName()+") is not allowed within this context. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
//      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Custom relation class ("+attHd.getName()+") is not allowed within this context.");
     }
      
     AgeClass rangeClass=null;
     
     ClassReference rgHdr=null;
     try
     {
      rgHdr = AgeTabSyntaxParser.string2ClassReference(rangeClassName);
     }
     catch(ParserException e)
     {
      log.log(Level.ERROR, "Invalid range class syntax. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
//      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Invalid range class syntax",e);
     }
     
     if( rgHdr.isCustom() )
      rangeClass = sm.getCustomAgeClass(rangeClassName);
     else
      rangeClass = sm.getDefinedAgeClass(rangeClassName);

     if( rangeClass == null )
     {
      log.log(Level.ERROR, "Invalid range class: '"+rangeClassName+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      addConverter(convs, new InvalidColumnConvertor(attHd) );
      result = false;
      continue;
//      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Invalid range class: '"+rangeClassName+"'");
     }

     AgeRelationClass parent = null;
     
     if( attHd.getParentClass() != null )
     {
      parent = sm.getDefinedAgeRelationClass(attHd.getParentClass());
      
      if( parent == null )
      {
       log.log(Level.ERROR, "Defined relation class '"+attHd.getParentClass()+"' (used as superclass) is not found. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
       addConverter(convs, new InvalidColumnConvertor(attHd) );
       result = false;
       continue;
      }
     }
      
     AgeRelationClass relCls = sm.getOrCreateCustomAgeRelationClass(attHd.getName(), rangeClass, blkCls, parent);
     
//     AgeRelationClass relCls = sm.getCustomAgeRelationClass(attHd.getName());
//     
//     if( relCls == null )
//      relCls = sm.createCustomAgeRelationClass(attHd.getName(), rangeClass, blkCls);
     
     int dupCol = addConverter(convs, new RelationConvertor(attHd,relCls,classMap.get(rangeClass)) );
     
     if( dupCol != -1 )
     {
      log.log(Level.ERROR, "Column header duplicates header at column "+convs.get(dupCol).getClassReference().getCol()
        +". Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      result = false;
     }

    }
    else
    {
     AgeAttributeClass attrClass = getCustomAttributeClass(attHd, blkCls, sm, log);
     
     if( attrClass == null )
     {
      result=false;
      addConverter(convs, new InvalidColumnConvertor(attHd) );
     }
     else
     {

      int dupCol = addConverter(convs, new AttributeConvertor(attHd, attrClass));

      if(dupCol != -1)
      {
       log.log(Level.ERROR, "Column header duplicates header at column " + convs.get(dupCol).getClassReference().getCol() + ". Row: " + attHd.getRow()
         + " Col: " + attHd.getCol());
       result = false;
      }
     }
    }
   }
   else
   {
    AgeClassProperty prop = sm.getDefinedAgeClassProperty(attHd.getName());
    
    if( prop == null )
    {
     log.log(Level.ERROR, "Defined property '"+attHd.getName()+"' not found. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "Unknown object property: '"+attHd.getName()+"'");
    }
    
//    if( ! sm.isValidProperty( prop, blck.ageClass ) )
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "Defined property '"+attHd.getName()+"' is not valid for class '"+blck.ageClass.getName()+"'");

    if( prop instanceof AgeAttributeClass )
    {
     AgeAttributeClass attClass = (AgeAttributeClass)prop;
     
     int dupCol = addConverter(convs, new AttributeConvertor(attHd,attClass) );
     
     if( dupCol != -1 )
     {
      log.log(Level.ERROR, "Column header duplicates header at column "+convs.get(dupCol).getClassReference().getCol()
        +". Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      result = false;
     }
    }
    else
    {
     AgeRelationClass rCls = (AgeRelationClass)prop;
     
     if( rCls.getDomain() != null && rCls.getDomain().size() > 0 )
     {
      boolean found=false;
      
      for( AgeClass dmcls : rCls.getDomain() )
      {
       if( blkCls.isClassOrSubclass(dmcls) )
       {
        found=true;
        break;
       }
      }
      
      if( !found )
      {
       log.log(Level.ERROR, "Class '"+blkCls.getName()+"' is not in the domain of relation class '"+rCls.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
       addConverter(convs, new InvalidColumnConvertor(attHd) );
       result = false;
       continue;
//       throw new SemanticException(attHd.getRow(),attHd.getCol(),"Class '"+blkCls.getName()+"' is not in the domain of relation class '"+rCls.getName()+"'");
      }
     }
     
     int dupCol = addConverter(convs, new DefinedRelationConvertor( attHd, rCls, classMap) );
     
     if( dupCol != -1 )
     {
      log.log(Level.ERROR, "Column header duplicates header at column "+convs.get(dupCol).getClassReference().getCol()
        +". Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      result = false;
     }

    }
   }
  }
  
  return result;
 }
 
 
 private abstract class ValueConverter
 {
  protected ClassReference colHdr;
  protected AgeObjectPropertyWritable lastProp;
  
  protected ValueConverter( ClassReference hd )
  {
   colHdr=hd;
  }

  abstract public AgeClassProperty getProperty();
  abstract public AgeClassProperty getQualifiedProperty();

  public abstract void convert(AgeObjectWritable obj, AgeTabValue vls) throws ConvertionException;

  public ClassReference getClassReference()
  {
   return colHdr;
  }
  
  protected void setLastConvertedProperty( AgeObjectPropertyWritable p )
  {
   lastProp=p;
  }
  
  public AgeObjectPropertyWritable getLastConvertedProperty()
  {
   return lastProp;
  }
 }
 
 private class DefinedRelationConvertor extends ValueConverter
 {
  private Collection<Map<String, AgeObjectWritable>> rangeObjects;
  private AgeRelationClass relClass;
  
  public DefinedRelationConvertor(ClassReference hd, AgeRelationClass rlClass, Map<AgeClass, Map<String, AgeObjectWritable>> classMap)
  {
   super(hd);
   
   relClass=rlClass;
   
   Collection<AgeClass> rngSet = rlClass.getRange();
   
   if( rngSet == null || rngSet.size() == 0 )
    rangeObjects=classMap.values();
   else
   {
    rangeObjects = new LinkedList<Map<String,AgeObjectWritable>>();
    
    for( Map.Entry<AgeClass, Map<String, AgeObjectWritable>> me : classMap.entrySet() )
     if( rngSet.contains(me.getKey()) )
      rangeObjects.add(me.getValue());
   }
  }

  
  public AgeClassProperty getProperty()
  {
   return relClass;
  }
 
  public AgeClassProperty getQualifiedProperty()
  {
   return null;
  }

  
  @Override
  public void convert( AgeObjectWritable obj, AgeTabValue atVal ) throws ConvertionException
  {
   setLastConvertedProperty(null);

   if(atVal == null)
    return;

   int found = 0;

   String val = atVal.getValue().trim();

   if(val.length() == 0)
    return;

   AgeObjectWritable targetObj = null;

   for(Map<String, AgeObjectWritable> omap : rangeObjects)
   {
    AgeObjectWritable candObj = omap.get(val);

    if(candObj != null)
    {
     targetObj = candObj;
     found++;
    }
   }

   if(found > 1)
    throw new ConvertionException(atVal.getRow(), atVal.getCol(), "Ambiguous reference");

   AgeRelationWritable rel = null;
   if(targetObj == null)
    rel = obj.createExternalRelation(val, relClass);
   else
    rel = obj.createRelation(targetObj, relClass);

   rel.setOrder(getClassReference().getCol());
  
   setLastConvertedProperty(rel);

  }
 }

 
 private class RelationConvertor extends ValueConverter
 {
  private Map<String, AgeObjectWritable> rangeObjects;
  private AgeRelationClass       relClass;

  public RelationConvertor(ClassReference hd, AgeRelationClass relCls, Map<String, AgeObjectWritable> map)
  {
   super(hd);
   rangeObjects = map;
   relClass = relCls;
  }

  @Override
  public void convert(AgeObjectWritable obj, AgeTabValue atVal)
  {
   setLastConvertedProperty(null);

   if(atVal == null )
    return;
   
   String val = atVal.getValue().trim();

   if(val.length() == 0)
    return;

   AgeObjectWritable targetObj = null;

   if(rangeObjects != null)
    targetObj = rangeObjects.get(val);

   AgeRelationWritable rel = null;
   if(targetObj == null)
    rel = obj.createExternalRelation(val, relClass);
   else
    rel = obj.createRelation(targetObj, relClass);

   rel.setOrder(getClassReference().getCol());
   setLastConvertedProperty(rel);

  }
  
  @Override
  public AgeClassProperty getProperty()
  {
   return relClass;
  }
  
  public AgeClassProperty getQualifiedProperty()
  {
   return null;
  }

 }
 
 
 private class EmptyColumnConvertor  extends ValueConverter
 {

  protected EmptyColumnConvertor( ClassReference cr )
  {
   super(cr);
  }

  @Override
  public void convert(AgeObjectWritable obj, AgeTabValue vls) throws ConvertionException
  {
   if(vls == null)
    return;

   if(vls.getValue().length() > 0)
    throw new ConvertionException(vls.getRow(), vls.getCol(), "Cells in the column with no header must be empty");
  }

  @Override
  public AgeClassProperty getProperty()
  {
   return null;
  }
  
  public AgeClassProperty getQualifiedProperty()
  {
   return null;
  }

 }
 
 private class AttributeConvertor extends ValueConverter
 {
  private AgeAttributeClass attrClass;

  public AttributeConvertor(ClassReference hd, AgeAttributeClass attCls) throws SemanticException
  {
   super( hd );
   attrClass = attCls;
   
   DataType dt = attrClass.getDataType();
   
   if( dt == null )
    throw new SemanticException(hd.getRow(), hd.getCol(), "Attribute class: '"+attrClass.getName()+"' has no data type and can't be instantiated");
  }

  @Override
  public void convert(AgeObjectWritable obj, AgeTabValue vl) throws ConvertionException
  {
   setLastConvertedProperty(null);
   
   if( vl == null || vl.getValue().length() == 0 )
    return;
   
   AgeAttributeWritable attr = null;
   boolean exstAttr=false;
   
   if( attrClass.getDataType().isMultiline())
   {
    Collection<? extends AgeAttributeWritable> atcoll = obj.getAttributes(attrClass);
    
    if( atcoll == null || atcoll.size() == 0 )
     attr = obj.createAgeAttribute(attrClass);
    else
    {
     attr = atcoll.iterator().next();
     exstAttr=true;
    }
   }
   else
    attr = obj.createAgeAttribute(attrClass);
   
 
//   AgeAttribute attr = obj.getAttribute(attrClass);
//   AgeAttributeWritable attrAlt = obj.createAgeAttribute(attrClass);
//   
//   if( attr == null )
//    attrAlt = obj.createAgeAttribute(attrClass);
//   else if( attr instanceof AgeAttributeWritable )
//    attrAlt= (AgeAttributeWritable) attr;
//   else
//    throw new ConvertionException(getColumnHeader().getRow(), getColumnHeader().getRow(), "Attribute '"+attrClass+"' already exists in the object '"+obj.getId()+"' and isn't alterable" );
   
    try
    {
     attr.updateValue(vl.getValue());
    }
    catch(FormatException e)
    {
     throw new ConvertionException(vl.getRow(), vl.getCol(), "Invalid value ("+vl.getValue()+") for attribute: "+attrClass.getName() );
    }
   
    if( ! exstAttr )
     attr.setOrder( getClassReference().getCol() );
    
    setLastConvertedProperty(attr);
  }
  
  public AgeClassProperty getProperty()
  {
   return attrClass;
  }
  
  public AgeClassProperty getQualifiedProperty()
  {
   return null;
  }

 }
 
 private class QualifierConvertor extends ValueConverter
 {
  private AgeAttributeClass attrClass;
  private ValueConverter hostConverter;


  public QualifierConvertor(ClassReference attHd, AgeAttributeClass qClass, ValueConverter hc) throws SemanticException
  {
   super(attHd);
   attrClass = qClass;

   DataType dt = attrClass.getDataType();
   
   if( dt == null )
    throw new SemanticException(attHd.getRow(), attHd.getCol(), "Attribute class: '"+attrClass.getName()+"' has no data type and can't be instantiated");
   
   hostConverter = hc;
  }

  @Override
  public void convert(AgeObjectWritable obj, AgeTabValue val) throws ConvertionException
  {

   if(val == null || val.getValue().length() == 0)
   {
    setLastConvertedProperty(null);
    return;
   }

   AgeObjectPropertyWritable prop = hostConverter.getLastConvertedProperty();


   if(prop == null && (getLastConvertedProperty() == null || !attrClass.getDataType().isMultiline()))
    throw new ConvertionException(val.getRow(), val.getCol(), "There is no main value for qualification");

   boolean exstProp = false;

   AgeAttributeWritable attrAlt = null;

   if(prop == null)
   {
    attrAlt = (AgeAttributeWritable) getLastConvertedProperty();
    exstProp = true;
   }
   else
   {
    if(attrClass.getDataType().isMultiline())
    {
     Collection<? extends AgeAttributeWritable> qs = prop.getAttributes();

     if(qs == null)
      attrAlt = obj.getAgeElClass().getSemanticModel().createAgeAttribute(attrClass);
     else
     {
      for(AgeAttributeWritable q : qs)
      {
       if(q.getAgeElClass() == attrClass)
       {
        exstProp = true;
        attrAlt = q;
        break;
       }
      }
     }

    }

    if(attrAlt == null)
     attrAlt = obj.getAgeElClass().getSemanticModel().createAgeAttribute(attrClass);
   }

   try
   {
    attrAlt.updateValue(val.getValue());
   }
   catch(FormatException e)
   {
    throw new ConvertionException(val.getRow(), val.getCol(), "Invalid value (" + val.getValue() + ") for attribute: "
      + attrClass.getName());
   }

   if( !exstProp )
   {
    attrAlt.setOrder(getClassReference().getCol());
   
    prop.addAttribute(attrAlt);
   }
   
   setLastConvertedProperty(attrAlt);
  }

  @Override
  public AgeClassProperty getProperty()
  {
   return attrClass;
  }

  @Override
  public AgeClassProperty getQualifiedProperty()
  {
   return hostConverter.getProperty();
  }
 }
 
 private class InvalidColumnConvertor extends ValueConverter
 {

  public InvalidColumnConvertor( ClassReference hd )
  {
   super(hd);
  }

  
  @Override
  public AgeClassProperty getProperty()
  {
   return null;
  }

  @Override
  public AgeClassProperty getQualifiedProperty()
  {
   return null;
  }

  @Override
  public void convert(AgeObjectWritable obj, AgeTabValue vls) throws ConvertionException
  {
  }
 }
}
