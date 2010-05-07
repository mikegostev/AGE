package uk.ac.ebi.age.parser.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassProperty;
import uk.ac.ebi.age.model.AgeExternalRelation;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.RestrictionException;
import uk.ac.ebi.age.model.Submission;
import uk.ac.ebi.age.model.impl.IsInstanceOfRestriction;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;
import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSemanticValidator;
import uk.ac.ebi.age.parser.AgeTabSubmission;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.AgeTabValue;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ColumnHeader;
import uk.ac.ebi.age.parser.ConvertionException;
import uk.ac.ebi.age.parser.ParserException;
import uk.ac.ebi.age.parser.SemanticException;

public class AgeTabSemanticValidatorImpl extends AgeTabSemanticValidator
{
// private AttrAttchRel attributeAttachmentClass;
 
 @Override
 public SubmissionWritable parse(AgeTabSubmission data, ContextSemanticModel sm ) throws SemanticException, ConvertionException, RestrictionException
 {
  SubmissionWritable res = sm.createSubmission();
  
  Map<AgeClass, Map<String,AgeObjectWritable>> classMap = new HashMap<AgeClass, Map<String,AgeObjectWritable>>();
  
  Map<String,AgeObjectWritable> objectMap = null;
  
  Map< BlockHeader, AgeClass > blk2classMap = new HashMap<BlockHeader, AgeClass>();
  
  for( BlockHeader hdr : data.getBlocks() )
  {
   ColumnHeader colHdr = hdr.getClassColumnHeader();
   
   AgeClass cls = null;

   if( colHdr.isCustom() )
   {
    if( sm.getContext().isCustomClassAllowed() )
    {
     cls = sm.getCustomAgeClass(colHdr.getName());

     if(cls == null)
      cls = sm.createCustomAgeClass(colHdr.getName(),null);
    }
    else 
     throw new SemanticException(colHdr.getRow(),colHdr.getCol(),"Custom classes are not allowed within this context");
   }
   else
   {
    cls = sm.getDefinedAgeClass( colHdr.getName() );
    
    if( cls == null )
     throw new SemanticException(colHdr.getRow(),colHdr.getCol(),"Class '"+colHdr.getName()+"' not found");
   }
  
   blk2classMap.put(hdr, cls);
   
   objectMap = classMap.get(cls);
   
   if(objectMap == null)
    classMap.put( cls, objectMap=new HashMap<String, AgeObjectWritable>() );
   
   
   for( AgeTabObject atObj : data.getObjects(hdr) )
   {
    AgeObjectWritable obj = objectMap.get(atObj.getId());
    
    if( obj == null )
    {
     obj = sm.createAgeObject(atObj.getId(), cls);
     obj.setOrder( atObj.getRow() );
     
     objectMap.put(atObj.getId(), obj);
    }
   }
  }
  
  List<ValueConverter> convs = new ArrayList<ValueConverter>(20);
  
  for( Map.Entry<BlockHeader, AgeClass> me : blk2classMap.entrySet() )
  {
//   SubmissionBlock sBlock = new SubmissionBlock(me.getKey(), me.getValue());
//   res.addSubmissionBlock(sBlock);
   
//   if( me.getValue().isCustom() )
//    res.addClass( me.getValue() );
   
   createConvertors( me.getKey(), me.getValue(), convs, sm, classMap);
   
   objectMap = classMap.get( me.getValue() );
   
   for( AgeTabObject atObj : data.getObjects(me.getKey()) )
   {
    AgeObjectWritable obj = objectMap.get(atObj.getId());
    
    for( ValueConverter cnv : convs )
    {
     List<AgeTabValue> vals = atObj.getValues(cnv.getColumnHeader());
     
     cnv.convert(obj,vals);
    }
    
    res.addObject(obj);
    obj.setSubmission(res);
   }

  }

//  attributeAttachmentClass = new AttrAttchRel(sm.getAttributeAttachmentClass());
  
  validateData(res);
  
  imputeReverseRelations( res );
  
  return res;
 }

 private void imputeReverseRelations( SubmissionWritable data )
 {
  for( AgeObjectWritable obj : data.getObjects() )
  {
   
   for( AgeRelationWritable rl : obj.getRelations() )
   {
    if( rl instanceof AgeExternalRelation )
     continue;
    
    AgeRelationClass invClass = rl.getAgeElClass().getInverseClass();
    
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

 private void validateData( Submission data ) throws RestrictionException
 {
  for( AgeObject obj : data.getObjects() )
   IsInstanceOfRestriction.isInstanceOf(obj, obj.getAgeElClass());
  
//  for( SubmissionBlock blk : data.getSubmissionBlocks() )
//  {
//   for( AgeObject obj : blk.getObjects() )
//    IsInstanceOfRestriction.isInstanceOf(obj, obj.getAgeXClass());
//  }
 }

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
 
 private void createConvertors( BlockHeader blck, AgeClass blkCls, List<ValueConverter> convs, ContextSemanticModel sm, Map<AgeClass, Map<String,AgeObjectWritable>> classMap ) throws SemanticException
 {
  convs.clear();
  
  for( ColumnHeader attHd : blck.getColumnHeaders() )
  {
   
   if( attHd.isCustom() )
   {
    String rangeClassName = attHd.getFlagValue(rangeFlag);
    
    if( rangeClassName != null )
    {
     if( ! sm.getContext().isCustomRelationClassAllowed() )
      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Custom relation class ("+attHd.getName()+") is not allowed within this context.");
      
     AgeClass rangeClass=null;
     
     ColumnHeader rgHdr=null;
     try
     {
      rgHdr = AgeTabSyntaxParser.string2ColumnHeader(rangeClassName);
     }
     catch(ParserException e)
     {
      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Invalid range class syntax",e);
     }
     
     if( rgHdr.isCustom() )
      rangeClass = sm.getCustomAgeClass(rangeClassName);
     else
      rangeClass = sm.getDefinedAgeClass(rangeClassName);

     if( rangeClass == null )
      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Invalid range class: '"+rangeClassName+"'");

     AgeRelationClass relCls = sm.getCustomAgeRelationClass(attHd.getName());
     
     if( relCls == null )
      relCls = sm.createCustomAgeRelationClass(attHd.getName(), rangeClass, blkCls);
     
     convs.add( new RelationConvertor(attHd,relCls,classMap.get(rangeClass)) );
    }
    else
    {
     if( ! sm.getContext().isCustomAttributeClassAllowed() )
      throw new SemanticException(attHd.getRow(), attHd.getCol(), "Custom attribure class ("+attHd.getName()+") is not allowed within this context.");
     
     AgeAttributeClass attrClass = sm.getCustomAgeAttributeClass(attHd.getName(), blkCls);
     
     String typeName = attHd.getFlagValue(typeFlag);
     
     DataType type = DataType.STRING;
     
     if( typeName != null )
     {
      try
      {
       type = DataType.valueOf(typeName);
      }
      catch(Exception e)
      {
       throw new SemanticException(attHd.getRow(), attHd.getCol(), "Invalid type name: "+typeName);
      }
     }
     else
     {
      if( attrClass != null )
       type = attrClass.getDataType();
     }
     
     if( attrClass != null )
     {
      if( attrClass.getDataType() != type )
       throw new SemanticException(attHd.getRow(), attHd.getCol(), "Data type ('"+type+"') mismatches with the previous definition: "+attrClass.getDataType());
     }
     else
      attrClass = sm.createCustomAgeAttributeClass(attHd.getName(),type, blkCls);

     convs.add( new AttributeConvertor(attHd,attrClass) );
    }
   }
   else
   {
    AgeClassProperty prop = sm.getDefinedAgeClassProperty(attHd.getName());
    
    if( prop == null )
     throw new SemanticException(attHd.getRow(), attHd.getCol(), "Unknown object property: '"+attHd.getName()+"'");
    
//    if( ! sm.isValidProperty( prop, blck.ageClass ) )
//     throw new SemanticException(attHd.getRow(), attHd.getCol(), "Defined property '"+attHd.getName()+"' is not valid for class '"+blck.ageClass.getName()+"'");

    if( prop instanceof AgeAttributeClass )
    {
     AgeAttributeClass attClass = (AgeAttributeClass)prop;
     
     convs.add( new AttributeConvertor(attHd,attClass) );
    }
    else
    {
     AgeRelationClass rCls = (AgeRelationClass)prop;
     
     if( rCls.getDomain() != null )
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
       throw new SemanticException(attHd.getRow(),attHd.getCol(),"Class '"+blkCls.getName()+"' is not in the domain of relation class '"+rCls.getName()+"'");
     }
     
     convs.add( new DefinedRelationConvertor( attHd,rCls, classMap) );
    }
   }
  }
 }
 
 
 private abstract class ValueConverter
 {
  protected ColumnHeader colHdr;
//  protected SemanticModel semantic;
  
  protected ValueConverter( ColumnHeader hd )
  {
   colHdr=hd;
//   semantic=sm;
  }
  
  public abstract void convert(AgeObjectWritable obj, List<AgeTabValue> vls) throws ConvertionException;

  public ColumnHeader getColumnHeader()
  {
   return colHdr;
  }
 }
 
 private class DefinedRelationConvertor extends ValueConverter
 {
  private Collection<Map<String, AgeObjectWritable>> rangeObjects;
  private AgeRelationClass relClass;
  
  public DefinedRelationConvertor(ColumnHeader hd, AgeRelationClass rlClass, Map<AgeClass, Map<String, AgeObjectWritable>> classMap)
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

  @Override
  public void convert( AgeObjectWritable obj, List<AgeTabValue> vals ) throws ConvertionException
  {
   
   for( AgeTabValue atVal : vals )
   {
    int found=0;

    String val = atVal.getValue().trim();
    
    if( val.length() == 0 )
     continue;
    
    AgeObjectWritable targetObj = null;
    
    for( Map<String, AgeObjectWritable> omap : rangeObjects )
    {
     AgeObjectWritable candObj = omap.get(val);
     
     if( candObj != null )
     {
      targetObj=candObj;
      found++;
     }
    }
    
    if( found > 1 )
     throw new ConvertionException(atVal.getRow(), atVal.getCol(), "Ambiguous reference");
    
    
    AgeRelationWritable rel=null;
    if( targetObj == null )
     rel = obj.createExternalRelation(val,relClass);
    else
     rel = obj.createRelation(targetObj,relClass);
    
    rel.setOrder(getColumnHeader().getCol());
   }
   
    
  }
 }

 
 private class RelationConvertor extends ValueConverter
 {
  private Map<String, AgeObjectWritable> rangeObjects;
  private AgeRelationClass       relClass;

  public RelationConvertor(ColumnHeader hd, AgeRelationClass relCls, Map<String, AgeObjectWritable> map)
  {
   super(hd);
   rangeObjects = map;
   relClass = relCls;
  }

  @Override
  public void convert(AgeObjectWritable obj, List<AgeTabValue> vals)
  {
   for(AgeTabValue atVal : vals)
   {
    String val = atVal.getValue().trim();

    if(val.length() == 0)
     continue;

    AgeObjectWritable targetObj = null;
     
    if( rangeObjects != null )
     targetObj = rangeObjects.get(val);

    AgeRelationWritable rel=null;
    if(targetObj == null)
     rel = obj.createExternalRelation(val, relClass);
    else
     rel = obj.createRelation(targetObj, relClass);
    
    rel.setOrder(getColumnHeader().getCol());
   }

  }
 }
 
 private class AttributeConvertor extends ValueConverter
 {
  private AgeAttributeClass attrClass;

  public AttributeConvertor(ColumnHeader hd, AgeAttributeClass attCls) throws SemanticException
  {
   super(hd);
   attrClass = attCls;
   
   DataType dt = attrClass.getDataType();
   
   if( dt == null )
    throw new SemanticException(hd.getRow(), hd.getCol(), "Attribute class: '"+attrClass.getName()+"' has no data type and can't be instantiated");
  }

  @Override
  public void convert(AgeObjectWritable obj, List<AgeTabValue> vals) throws ConvertionException
  {
   if( vals == null || vals.size() == 0 )
    return;
   
   ColumnHeader hdr = vals.get(0).getColumnHeader();
   
   AgeAttributeWritable attrAlt = hdr.getParameter()==null?obj.createAgeAttribute(attrClass):obj.createAgeAttribute(attrClass,hdr.getParameter());
  
//   AgeAttribute attr = obj.getAttribute(attrClass);
//   AgeAttributeWritable attrAlt = obj.createAgeAttribute(attrClass);
//   
//   if( attr == null )
//    attrAlt = obj.createAgeAttribute(attrClass);
//   else if( attr instanceof AgeAttributeWritable )
//    attrAlt= (AgeAttributeWritable) attr;
//   else
//    throw new ConvertionException(getColumnHeader().getRow(), getColumnHeader().getRow(), "Attribute '"+attrClass+"' already exists in the object '"+obj.getId()+"' and isn't alterable" );
   
   for( AgeTabValue vl : vals )
   { 
    try
    {
     attrAlt.updateValue(vl.getValue());
    }
    catch(FormatException e)
    {
     throw new ConvertionException(vl.getRow(), vl.getCol(), "Invalid value ("+vl.getValue()+") for attribute: "+attrClass.getName() );
    }
   }
   
   attrAlt.finalizeValue();
   attrAlt.setOrder( getColumnHeader().getCol() );
  }
 }
}
