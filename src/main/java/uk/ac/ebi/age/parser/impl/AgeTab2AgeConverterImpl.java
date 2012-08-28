package uk.ac.ebi.age.parser.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.age.authz.ACR.Permit;
import uk.ac.ebi.age.authz.PermissionManager;
import uk.ac.ebi.age.ext.authz.SystemAction;
import uk.ac.ebi.age.ext.log.LogNode;
import uk.ac.ebi.age.ext.log.LogNode.Level;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeExternalRelation;
import uk.ac.ebi.age.model.AgeObjectProperty;
import uk.ac.ebi.age.model.AgePropertyClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.AttributedClass;
import uk.ac.ebi.age.model.ClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.FormatException;
import uk.ac.ebi.age.model.IdScope;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.ResolveScope;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeFileAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeObjectPropertyWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.parser.AgeTab2AgeConverter;
import uk.ac.ebi.age.parser.AgeTabModule;
import uk.ac.ebi.age.parser.AgeTabObject;
import uk.ac.ebi.age.parser.AgeTabSyntaxParser;
import uk.ac.ebi.age.parser.AgeTabValue;
import uk.ac.ebi.age.parser.BlockHeader;
import uk.ac.ebi.age.parser.ClassReference;
import uk.ac.ebi.age.parser.ConvertionException;
import uk.ac.ebi.age.parser.SyntaxProfile;
import uk.ac.ebi.age.parser.SyntaxProfileDefinition;

public class AgeTab2AgeConverterImpl implements AgeTab2AgeConverter
{
 private final PermissionManager permissionManager;
 private final SyntaxProfile syntaxProfile;
 
 public AgeTab2AgeConverterImpl( PermissionManager pMngr, SyntaxProfile syntaxProfile )
 {
  permissionManager = pMngr;
  this.syntaxProfile=syntaxProfile;
 }
 
 @Override
 public DataModuleWritable convert(AgeTabModule data, ContextSemanticModel sm, LogNode log )// throws SemanticException, ConvertionException
 {
  boolean result = true;
  
  DataModuleWritable res = sm.createDataModule();
  
  Map<AgeClass, Map<String,AgeObjectWritable>> classMap = new HashMap<AgeClass, Map<String,AgeObjectWritable>>();
  Map<AgeClass, AgeObjectWritable> prototypeMap = new HashMap<AgeClass, AgeObjectWritable>();
  
  Map<String,AgeObjectWritable> objectMap = null;
  
  Map< BlockHeader, AgeClass > blk2classMap = new LinkedHashMap<BlockHeader, AgeClass>();
  
  for( BlockHeader hdr : data.getBlocks() )
  {
   ClassReference colHdr = hdr.getClassColumnHeader();


   LogNode blkLog = log.branch("Processing block for class "+colHdr.getName()+" at line: "+colHdr.getRow());
   
   
   if( colHdr.getQualifiers() != null )
   {
    blkLog.log(Level.WARN, "Class reference must not be qualified. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());
//    throw new SemanticException(colHdr.getRow(),colHdr.getCol(),"Class reference must not be qualified");
   }
   
   AgeClass cls = getClassForBlock(colHdr, sm, blkLog);

   if( cls == null )
   {
    result = false;
    continue;
   }

   SyntaxProfileDefinition profileDef = colHdr.isCustom()?
     syntaxProfile.getCommonSyntaxProfile():syntaxProfile.getClassSpecificSyntaxProfile(colHdr.getName());
  
   ClassRef clsRef = sm.getModelFactory().createClassRef(sm.getAgeClassPlug(cls), colHdr.getRow(), colHdr.getOriginalReference(), hdr.isHorizontal(), sm);
   
   blk2classMap.put(hdr, cls);
   
   objectMap = classMap.get(cls);
   
   if(objectMap == null)
    classMap.put( cls, objectMap=new HashMap<String, AgeObjectWritable>() );
   
   
   for( AgeTabObject atObj : data.getObjects(hdr) ) // Creating (if necessary) objects for every new ID
   {
    
    if( atObj.isPrototype() && ! profileDef.isResetPrototype() )
    {
     AgeObjectWritable obj = prototypeMap.get(cls);
     
     if( obj == null )
     {
      obj = sm.createAgeObject(clsRef, profileDef.getPrototypeObjectId());
      obj.setOrder( atObj.getRow() );
      
      prototypeMap.put(cls, obj);
     }
     
     continue;
    }
    
    AgeObjectWritable obj = objectMap.get(atObj.getId());
    
    if( obj == null ) // if obj != null it means that it has been defined earlier in the other or the same block 
    {
     String id = atObj.getId();

     if( ! atObj.isIdDefined() )
     {
      id=null; //We will generate ID later. The commented code below is obsolete

//      id = cls.getIdPrefix()+IdGenerator.getInstance().getStringId()+"-"+atObj.getId();
//      
//      if( atObj.isIdDefined() )
//       id+="-"+atObj.getId();
     }
     
     obj = sm.createAgeObject(clsRef, id);
     obj.setOrder( atObj.getRow() );
     obj.setId(id);
     obj.setIdScope(atObj.getIdScope());
     
     objectMap.put(atObj.getId(), obj);
    }
   }
  }
  
  List<ValueConverter> convs = new ArrayList<ValueConverter>(20);
  
  for( Map.Entry<BlockHeader, AgeClass> me : blk2classMap.entrySet() )
  {
   LogNode subLog = log.branch("Creating value converters for class '"+me.getValue()+"'. Block at: "+me.getKey().getClassColumnHeader().getRow());
   
   if( ! createConvertors( me.getKey(), me.getValue(), convs, sm, classMap, subLog ) )
   {
    subLog.log(Level.ERROR,"Convertors creation failed");
    result = false; //We don't stop here, erroneous columns will be ignored
   }
 
   SyntaxProfileDefinition profileDef = me.getKey().getClassColumnHeader().isCustom()?
     syntaxProfile.getCommonSyntaxProfile():syntaxProfile.getClassSpecificSyntaxProfile(me.getKey().getClassColumnHeader().getName());

   AgeObjectWritable prototype = prototypeMap.get(me.getValue());
   
   objectMap = classMap.get( me.getValue() );
  
   subLog = log.branch("Converting values for class '"+me.getValue().getName()+"'. Block at :"+me.getKey().getClassColumnHeader().getRow());
 
   for( AgeTabObject atObj : data.getObjects(me.getKey()) )
   {
    LogNode objLog = subLog.branch("Processing object: "+atObj.getId());

    AgeObjectWritable obj = null;
    
    
    if( atObj.isPrototype()  )
    {
     if( profileDef.isResetPrototype() )
     {
      ClassReference colHdr = me.getKey().getClassColumnHeader();
      ClassRef clsRef = sm.getModelFactory().createClassRef(sm.getAgeClassPlug(me.getValue()), colHdr.getRow(), colHdr.getOriginalReference(), me.getKey().isHorizontal(), sm);

      obj = sm.createAgeObject(clsRef, profileDef.getPrototypeObjectId());
      obj.setOrder( atObj.getRow() );
      
      prototypeMap.put(me.getValue(), obj);
     }
     else
      obj = prototypeMap.get(me.getValue());
    }
    else
     obj = objectMap.get(atObj.getId());

    for( ValueConverter cnv : convs )
     cnv.resetObject(obj);
    
    if( prototype != null && ! atObj.isPrototype() )
     applyPrototype(obj, prototype);
    
    boolean hasValue=true;
    int ln=0;
    while( hasValue )
    {
     hasValue=false;
     
     for( ValueConverter cnv : convs )
     {
      cnv.resetLine(ln);
      
      List<AgeTabValue> vals = atObj.getValues(cnv.getClassReference());
      

       if(vals == null || vals.size() <= ln)
       {
        try
        {
         cnv.convert(null);
        }
        catch (ConvertionException e)
        {
         objLog.log(Level.ERROR, "Empty value processing error: "+e.getMessage()+". Row: "+e.getRow()+" Col: "+e.getColumn());
         result = false;
        }
       }
       else
       {
        hasValue = true;

        AgeTabValue val = vals.get(ln);

//        LogNode colLog = objLog.branch("Processing column: " + cnv.getClassReference().getCol() + ". Value: '" + val.getValue() + "'");


        try
        {
         cnv.convert(val);
//         colLog.log(Level.INFO, "Ok");
        }
        catch (ConvertionException e) 
        {
         objLog.log(Level.ERROR, "Conversion error: "+e.getMessage()+". Row: "+e.getRow()+" Col: "+e.getColumn());
         result = false;
        }

       }
      
     }
     
     ln++;
    }
    
    
    if( ! atObj.isPrototype() )
    {
     res.addObject(obj);
     obj.setDataModule(res);
    }
   }
  }
  
  if( ! result )
  {
   return null;
  }
  

  
  imputeInverseRelations( res );
  
  finalizeValues( res.getObjects() );
  
//  for( Collection<AgeObjectWritable> pObjs : prototypeMap.values() )
//   finalizeValues( pObjs );
  

//  AgeClass lastClass=null;
//  Collection<AgeObjectWritable> protos=null;
//  for( AgeObjectWritable obj : res.getObjects() )
//  {
//   if( lastClass != obj.getAgeElClass() )
//   {
//    lastClass=obj.getAgeElClass();
//    protos = prototypeMap.get(lastClass);
//   }
//   
//   if( protos != null )
//   {
//    for( AgeObjectWritable po : protos )
//    {
//     if( po.getAttributes() != null)
//     {
//      for( AgeAttributeWritable prat : po.getAttributes() )
//       obj.addAttribute(prat.createClone());
//     }
//     
//     if( po.getRelations() != null)
//     {
//      for( AgeRelationWritable prel : po.getRelations() )
//       obj.addRelation(prel.createClone());
//     }
//    }
//   }
//  }
  
//  validateData(res);
  
  
  return res;
 }
 
 
 private void applyPrototype(AgeObjectWritable obj, AgeObjectWritable prototype)
 {
  if( prototype.getAttributes() != null )
  {
   Collection<? extends AgeAttributeClass> protoAtCls = prototype.getAttributeClasses();
   
   clsloop : for( AgeAttributeClass pAtCla : protoAtCls)
   {
    if( obj.getAttributes() != null )
    {
     for( AgeAttributeWritable oa : obj.getAttributes() )
      if( oa.getAgeElClass() == pAtCla )
       continue clsloop;
    } 
    
    for( AgeAttributeWritable ptAttr : prototype.getAttributesByClass(pAtCla, false) )
     obj.addAttribute( ptAttr.createClone(obj) );
   }
  }
  
  if( prototype.getRelations() != null )
  {
   Collection<? extends AgeRelationClass> protoRlCls = prototype.getRelationClasses();
   
   clsloop : for( AgeRelationClass pRlCla : protoRlCls)
   {
    if( obj.getRelationClasses() != null && obj.getRelationClasses().size() > 0 )
    {
     for( AgeRelationWritable or : obj.getRelations() )
      if( or.getAgeElClass() == pRlCla )
       continue clsloop;
    } 
    
    for( AgeRelationWritable ptRel : prototype.getRelationsByClass(pRlCla, false) )
     obj.addRelation( ptRel.createClone(obj) );
   }
  }
 }
 
 private AgeClass getClassForBlock(ClassReference colHdr, ContextSemanticModel sm, LogNode blkLog)
 {
  if( colHdr.isCustom() )
  {
   if( permissionManager.checkSystemPermission(SystemAction.CUSTCLASSDEF) == Permit.ALLOW )
   {
    AgeClass parent = null;
    
    if( colHdr.getParentClass() != null )
    {
     parent = sm.getDefinedAgeClass(colHdr.getParentClass());
     
     if( parent == null )
     {
      blkLog.log(Level.ERROR, "Defined class '"+colHdr.getParentClass()+"' (used as superclass) is not found. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol() );

      return null;
     }
    }
     
    return sm.getOrCreateCustomAgeClass(colHdr.getName(), null, parent);
   }
   else
   {
    blkLog.log(Level.ERROR, "Custom classes are not allowed within this context. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());

    return null;
   }
  }
  else
  {
   AgeClass cls = sm.getDefinedAgeClass( colHdr.getName() );
   
   if( cls == null )
   {
    if( syntaxProfile.getCommonSyntaxProfile().allowImplicitCustomClasses() )
    {
     blkLog.log(Level.WARN, "Defined class '"+colHdr.getName()+"' not found, generating custom class. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());
     colHdr.setCustom(true);
     return getClassForBlock(colHdr, sm, blkLog);
    }
    
    blkLog.log(Level.ERROR, "Defined class '"+colHdr.getName()+"' not found. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());

    return null;
   }
   
   if( cls.isAbstract() )
   {
    blkLog.log(Level.ERROR, "Abstract class instantiation '"+colHdr.getName()+"'. Row: "+colHdr.getRow()+" Col: "+colHdr.getCol());

    return null;
   }

   
   return cls;
  }
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
 
 
 private void finalizeValues( Collection<? extends AttributedWritable> data ) //TODO
 {
  class AttrInfo
  {
   AgeAttributeWritable attr;
   AttributedWritable obj;
   
//   boolean isBool=false;
//   boolean isInt=false;
//   boolean isReal=false;
   
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

  Map<AttributedClass, Map<AgeAttributeClass,AttrClassInfo > > wMap = new HashMap<AttributedClass, Map<AgeAttributeClass,AttrClassInfo >>();
  
  Map<AgeAttributeClass, AttrClassInfo > cClassMap = null;
  AttributedClass cClass = null;
  
  for( AttributedWritable obj : data )
  {
   obj.sortAttributes();
   
   if( obj instanceof AgeObjectWritable )
   {
    AgeObjectWritable aow = (AgeObjectWritable)obj;
    
    if( aow.getRelations() != null )
     finalizeValues(aow.getRelations());
   }
   
   if( obj.getAttributes() != null )
    finalizeValues(obj.getAttributes());
   
   if(obj.getAttributedClass() != cClass)
   {
    cClass = obj.getAttributedClass();

    cClassMap = wMap.get(cClass);

    if(cClassMap == null)
    {
     cClassMap = new HashMap<AgeAttributeClass, AttrClassInfo>();
     wMap.put(cClass, cClassMap);
    }
   }

   if(obj.getAttributes() != null)
   {
    for(AgeAttributeWritable attr : obj.getAttributes())
    {
     attr.finalizeValue();

     if(attr.getAgeElClass().getDataType() == DataType.GUESS)
     {
      AttrClassInfo atcInfo = cClassMap.get(attr.getAgeElClass());

      if(atcInfo == null)
      {
       atcInfo = new AttrClassInfo();
       atcInfo.atClass = (AgeAttributeClassWritable) attr.getAgeElClass();

       cClassMap.put(atcInfo.atClass, atcInfo);
      }

      AttrInfo aInf = new AttrInfo();
      aInf.attr = attr;
      aInf.obj = obj;

      atcInfo.attributes.add(aInf);

      String value = attr.getValue().toString().trim();

      if(atcInfo.isBool)
      {
       if(value.equalsIgnoreCase("true"))
       {
        // aInf.isBool = true;
        aInf.boolValue = true;
       }
       else if(value.equalsIgnoreCase("false"))
       {
        // aInf.isBool = true;
        aInf.boolValue = false;
       }
       else
        atcInfo.isBool = false;
      }

      if(!atcInfo.isBool)
      {
       if(atcInfo.isInt)
       {
        try
        {
         aInf.intValue = Integer.parseInt(value);
         // aInf.isInt=true;
        }
        catch(Exception e)
        {
         atcInfo.isInt = false;
        }
       }

       if(atcInfo.isReal)
       {
        try
        {
         aInf.realValue = Double.parseDouble(value);
         // aInf.isReal=true;
        }
        catch(Exception e)
        {
         atcInfo.isReal = false;
        }
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
      AgeAttributeWritable nAttr = ai.obj.createAgeAttribute(ai.attr.getClassReference());
      
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
 
 

 private void imputeInverseRelations( DataModuleWritable data )
 {
  Map<AgeRelationClass, RelationClassRef> relRefMap = new HashMap<AgeRelationClass, RelationClassRef>();
  
  for( AgeObjectWritable obj : data.getObjects() )
  {
   
   if( obj.getRelations() == null )
    continue;
   
   for( AgeRelationWritable rl : obj.getRelations() )
   {
    if( rl instanceof AgeExternalRelation )
     continue;
    
    if( rl.getInverseRelation() != null )
     continue;
    
    AgeRelationClass invClass = rl.getAgeElClass().getInverseRelationClass();
    
    if( invClass == null )
     continue;
    
    boolean found=false;
    
    if( rl.getTargetObject().getRelations() != null )
    {
     for(AgeRelationWritable irl : rl.getTargetObject().getRelations())
     {
      if( irl.getTargetObject() == obj && irl.getAgeElClass().equals(invClass) )
      {
       if( irl.getInverseRelation() == null )
        irl.setInverseRelation(rl);
       
       rl.setInverseRelation(irl);
       
       found = true;
       break;
      }
     }
    }
    
    if( ! found )
    {
     AgeRelationWritable invRel = data.getContextSemanticModel().createInferredInverseRelation(rl);
     rl.getTargetObject().addRelation( invRel );
     
     rl.setInverseRelation(invRel);
    }
   }
  }
 }

 
 private AgeAttributeClass getCustomAttributeClass( ClassReference cr , AgeClass aCls, ContextSemanticModel sm, LogNode log)
 {
   if(permissionManager.checkSystemPermission(SystemAction.CUSTATTRCLASSDEF) != Permit.ALLOW)
   {
    log.log(Level.ERROR, "Custom attribure class (" + cr.getName() + ") is not allowed within this context. Row: "+cr.getRow()+" Col: "+cr.getCol() );
    return null;
   }
   
   AgeAttributeClass attrClass = sm.getCustomAgeAttributeClass(cr.getName(), aCls);

   String typeName = cr.getFlagValue(AgeTabSyntaxParser.typeFlag);
   
   ClassReference targCR = cr.getTargetClassRef();
   
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

   if( attrClass != null )
   {
    if(attrClass.getDataType() != type)
    {
     log.log(Level.ERROR, "Data type ('" + type + "') mismatches with the previous definition: " + attrClass.getDataType()+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null; 
//     throw new SemanticException(cr.getRow(), cr.getCol(), "Data type ('" + type + "') mismatches with the previous definition: " + attrClass.getDataType());
    }

    if( type == DataType.OBJECT )
    {
     if( attrClass.getTargetClass() == null )
     {
      log.log(Level.ERROR, "Reference to OBJECT attribute class with no target class. Row: "+cr.getRow()+" Col: "+cr.getCol() );
      return null; 
     }
     
     if( targCR != null && ! ( targCR.getName().equals(attrClass.getTargetClass().getName()) && targCR.isCustom() == attrClass.isCustom()) )
     {
      String prevTarg = attrClass.getTargetClass().getName();
      
     
      log.log(Level.ERROR, "Target class '"+targCR.getName()+"' "+
        (targCR.isCustom()?"(custom) ":"")+"mismatches with previous definition: '"+prevTarg+"'"+
        (attrClass.getTargetClass().isCustom()?" (custom)":"")+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
      return null; 
     }
    }
   }
   
   AgeAttributeClassWritable parent = null;

   if( cr.getParentClass() != null )
   {
    parent = (AgeAttributeClassWritable)sm.getDefinedAgeAttributeClass(cr.getParentClass());
    
    if( parent == null )
    {
     log.log(Level.ERROR, "Defined attribute class '"+cr.getParentClass()+"' (used as superclass) is not found. Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null; 
    }
   }
   
   AgeClass targetClass=null;
   
   if( targCR != null )
   {
    if( type == DataType.GUESS )
     type = DataType.OBJECT;
    
    if( targCR.isCustom() )
     targetClass = sm.getCustomAgeClass(targCR.getName());
    else
     targetClass = sm.getDefinedAgeClass(targCR.getName());
    
    if( targetClass == null )
    {
     log.log(Level.ERROR, "Target class '"+targCR.getName()+"' "+
       (targCR.isCustom()?"(custom ) ":"")+"not found. Row: "+cr.getRow()+" Col: "+cr.getCol() );
     return null; 
    }
   }
   
   if( type == DataType.OBJECT && targetClass == null)
   {
    log.log(Level.ERROR, "Target class must be defined for object attrubute class: '"+cr.getName()+"'"+
      (cr.isCustom()?" (custom )":"")+". Row: "+cr.getRow()+" Col: "+cr.getCol() );
    return null; 
   }
    
   
   AgeAttributeClassWritable attrClassW = sm.getOrCreateCustomAgeAttributeClass(cr.getName(), type, aCls, parent);

   if( targetClass != null )
    attrClassW.setTargetClass(targetClass);
   
   
   return attrClassW;
 }
 
 private int addConverter( List<ValueConverter> convs, ValueConverter cnv )
 {
  /*
  
  int i=0;
  
  if( cnv.getClassReference() != null )
  {
   for(ValueConverter exstC : convs)
   {
    if(exstC.getClassReference() == null)
     continue;

    if(exstC.getClassReference().equals(cnv.getClassReference()))
    {
     convs.add(new InvalidColumnConvertor(cnv.getClassReference()));
     return i;
    }

    i++;
    
    // if( exstC.getProperty() == cnv.getProperty() &&
    // exstC.getQualifiedProperty() == cnv.getQualifiedProperty() &&
    // cnv.getProperty() != null )
    // throw new SemanticException(cnv.getClassReference().getRow(),
    // cnv.getClassReference().getCol(),
    // "Column header duplicates header at column "+exstC.getClassReference().getCol());
   }
  }
  */
  
  convs.add(cnv);
  return -1;
 }
 
 private ValueConverter findHostConverter(ClassReference attHd, List<ValueConverter> convs, LogNode log )
 {
  List<ClassReference> qList = attHd.getQualifiers();
  ClassReference qualif = qList.get(qList.size()-1);

  
  if( qualif.getQualifiers() != null )
  {
   log.log(Level.ERROR, "A qualifier reference must not be qualified ifself. Use syntax attr[qual1][qual2]. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
   return null;
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
   log.log(Level.ERROR, "A qualifier must follow to a qualified property. Row: "+attHd.getRow()+" Col: "+attHd.getCol());

  
  return hostConverter;
  
 }
 
 private boolean createConvertors( BlockHeader blck, AgeClass blkCls, List<ValueConverter> convs, ContextSemanticModel sm,
   Map<AgeClass, Map<String,AgeObjectWritable>> classMap, boolean implCustom, LogNode log )// throws SemanticException
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
   
   if( attHd.getEmbeddedClassRef() != null )
   {
    List<ChainConverter.ChainElement> chain = new ArrayList<ChainConverter.ChainElement>();
    
    createEmbeddedObjectChain(blkCls, attHd, chain, sm, implCustom, log);
    
    addConverter(convs, new ChainConverter(chain, attHd));
    
    continue;
   }
   
   
   if( qList != null && qList.size() > 0 )
   {
    ValueConverter hostConverter = findHostConverter(attHd, convs, log);
    
    if( hostConverter == null )
    {
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
    }
     
    ClassReference qualif = qList.get(qList.size()-1);
    
    AgeAttributeClass qClass = (AgeAttributeClass)getPropertyClass(qualif, blkCls, sm, implCustom, true, log);
    
    if( qClass == null )
    {
     addConverter(convs, new InvalidColumnConvertor(attHd) );
     result = false;
     continue;
    }
    
    
    if( qClass.getDataType() == DataType.OBJECT )
    {
     if( qClass.getTargetClass() == null )
     {
      log.log(Level.ERROR, "No target class defined for OBJECT attribute class '"+qClass.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      result=false;
      addConverter(convs, new InvalidColumnConvertor(attHd) );
     }
     else
     {
      addConverter(convs, new ObjectQualifierConvertor(attHd, qClass, hostConverter, classMap.get(qClass.getTargetClass()),
        sm, syntaxProfile.getClassSpecificSyntaxProfile(blkCls.getName()) ) );
     }
    }
    else if( qClass.getDataType() == DataType.FILE )
    {
     addConverter(convs, new FileQualifierConvertor(attHd, qClass, hostConverter,
        sm, syntaxProfile.getClassSpecificSyntaxProfile(blkCls.getName()) ) );
    }
    else
     addConverter(convs, new ScalarQualifierConvertor( attHd, qClass, hostConverter, sm ) );
    
    continue;
   }
   
   AgePropertyClass propClass = getPropertyClass(attHd, blkCls, sm, implCustom, false, log);
   
   if( propClass == null )
   {
    result=false;
    addConverter(convs, new InvalidColumnConvertor(attHd) );
    continue;
   }
   
   if( propClass instanceof AgeAttributeClass )
   {
    AgeAttributeClass attrClass = (AgeAttributeClass)propClass;
    
    if( attrClass.getDataType() == DataType.OBJECT )
    {
     if( attrClass.getTargetClass() == null )
     {
      log.log(Level.ERROR, "No target class defined for OBJECT attribute class '"+attrClass.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
      result=false;
      addConverter(convs, new InvalidColumnConvertor(attHd) );
     }
     else
      addConverter(convs, new ObjectAttributeConvertor(attHd,attrClass, classMap.get(attrClass.getTargetClass()), sm ) );
      
    }
    else if( attrClass.getDataType() == DataType.FILE )
     addConverter(convs, new FileAttributeConvertor(attHd, attrClass, sm ) );
    else
     addConverter(convs, new AttributeConvertor(attHd, attrClass, sm));
   }
   else
   {
    AgeRelationClass relCls = (AgeRelationClass) propClass;
    
    if( attHd.isCustom() )
     addConverter(convs, new CustomRelationConvertor( attHd, relCls, sm, classMap.get( relCls.getRange().iterator().next() )) );
    else
     addConverter(convs, new DefinedRelationConvertor(attHd, relCls, sm, classMap) );
   }
   
  }
  
  return result;
 }
 
 
 private AgePropertyClass getPropertyClass( ClassReference attHd, AgeClass blkOwner, ContextSemanticModel sm, boolean implCust, boolean qualf, LogNode log )
 {
  if( attHd.isCustom() )
  {
   ClassReference rgHdr=attHd.getRangeClassRef();
   
   if( rgHdr != null )
   {
    if( permissionManager.checkSystemPermission(SystemAction.CUSTRELCLASSDEF) != Permit.ALLOW )
    {
     log.log(Level.ERROR, "Custom relation class ("+attHd.getName()+") is not allowed within this context. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     return null;
    }
     
    AgeClass rangeClass=null;
  
    if( rgHdr.isCustom() )
     rangeClass = sm.getCustomAgeClass(rgHdr.getName());
    else
    {
     rangeClass = sm.getDefinedAgeClass(rgHdr.getName());
    
     if( rangeClass == null && implCust )
      rangeClass = sm.getCustomAgeClass(rgHdr.getName());
    }
    
    
    if( rangeClass == null )
    {
     log.log(Level.ERROR, "Invalid range class: '"+rgHdr.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     return null;
    }

    AgeRelationClass parent = null;
    
    if( attHd.getParentClass() != null )
    {
     parent = sm.getDefinedAgeRelationClass(attHd.getParentClass());
     
     if( parent == null )
     {
      log.log(Level.ERROR, "Defined relation class '"+attHd.getParentClass()+"' (used as superclass) is not found. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
      return null;
     }
    }
     
    AgeRelationClassWritable relCls = sm.getOrCreateCustomAgeRelationClass(attHd.getName(), rangeClass, blkOwner, parent);
    
    if( relCls == null )
    {
     log.log(Level.ERROR, "Can't create custom relation class: '" + attHd.getName() + "'. Row: " + attHd.getRow() + " Col: " + attHd.getCol());
     return null;
    }
    
    return relCls;
    
   } //no range flag, i.e. attribute ref
   else
   {
    if( permissionManager.checkSystemPermission(qualf?SystemAction.CUSTQUALCLASSDEF:SystemAction.CUSTATTRCLASSDEF) != Permit.ALLOW )
    {
     log.log(Level.ERROR, "Custom "+(qualf?"qualifier":"attribute")+" class ("+attHd.getName()+") is not allowed within this context. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
     return null;
    }

    AgeAttributeClass attrClass = getCustomAttributeClass(attHd, blkOwner, sm, log);
    
    if( attrClass == null )
    {
     log.log(Level.ERROR, "Can't create custom attribute class: '" + attHd.getName() + "'. Row: " + attHd.getRow() + " Col: " + attHd.getCol());
     return null;
    }
    
    return attrClass;
   }
  }
  else //no 'custom' syntax, presumably defined class
  {
   AgePropertyClass prop = sm.getDefinedAgeClassProperty(attHd.getName());
   
  
   if( prop == null )
   {
    
    if( ! implCust )
    {
     log.log(Level.ERROR, "Defined property '" + attHd.getName() + "' not found. Row: " + attHd.getRow() + " Col: " + attHd.getCol());
     return null;
    }
    
    log.log(Level.WARN, "Defined attribute/relation class ("+attHd.getName()+") not found. Generating custom class. Row: "+attHd.getRow()+" Col: "+attHd.getCol());
    attHd.setCustom(true);

    return getPropertyClass(attHd, blkOwner, sm, implCust, qualf, log);
   }
   
   
   if( prop instanceof AgeAttributeClass )
   {
    AgeAttributeClass attClass = (AgeAttributeClass)prop;

    if( attClass.isAbstract() )
    {
     log.log(Level.ERROR, "Abstract attribute class instantiation '"+attHd.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
     return null;
    }

   }
   else
   {
    AgeRelationClass rCls = (AgeRelationClass)prop;
    
    if( rCls.isAbstract() )
    {
     log.log(Level.ERROR, "Abstract relation class instantiation '"+attHd.getName()+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
     return null;
    }

    
    if( rCls.getDomain() != null && rCls.getDomain().size() > 0 )
    {
     boolean found=false;
     
     for( AgeClass dmcls : rCls.getDomain() )
     {
      if( blkOwner.isClassOrSubclass(dmcls) )
      {
       found=true;
       break;
      }
     }
     
     if( !found )
     {
      log.log(Level.ERROR, "Class '"+blkOwner+"' is not in the domain of relation class '"+rCls+"'. Row: "+attHd.getRow()+" Col: "+attHd.getCol() );
      return null;
     }
    }
   }

   return prop;
  }
 }
 
 private boolean createEmbeddedObjectChain(AgeClass blkOwner, ClassReference attHd, List<ChainConverter.ChainElement> chain, ContextSemanticModel sm, boolean implCust, LogNode log)
 {
  ChainConverter.ChainElement cEl = new ChainConverter.ChainElement();

  AgePropertyClass prop = getPropertyClass(attHd, blkOwner, sm, implCust, log);

  if(prop == null)
   return false;

  if(prop instanceof AgeRelationClass)
  {
   if(attHd.getQualifiers() == null && attHd.getEmbeddedClassRef() != null)
   {
    log.log(Level.ERROR,
      "Object attribute class expexted instead of relation class '" + ((AgeRelationClass) prop).getName()+"'");
    return false;
   }

   cEl.elType = ChainConverter.ChainElement.ELTYPE.RELATION;
   cEl.elClassRef = sm.getModelFactory().createRelationClassRef(sm.getAgeRelationClassPlug((AgeRelationClass) prop),
     attHd.getOrder(), attHd.getOriginalReference());
  }
  else
  {
   if(attHd.getQualifiers() == null && attHd.getEmbeddedClassRef() != null && ((AgeAttributeClass) prop).getDataType() != DataType.OBJECT)
   {
    log.log(Level.ERROR, "Object attribute class expexted instead of '" + ((AgeAttributeClass) prop).getName()+"'");
    return false;
   }
   
   cEl.elType = ChainConverter.ChainElement.ELTYPE.ATTRIBUTE;
   cEl.elClassRef = sm.getModelFactory().createAttributeClassRef(sm.getAgeAttributeClassPlug((AgeAttributeClass) prop),
     attHd.getOrder(), attHd.getOriginalReference());

  }
  
  chain.add(cEl);

  if(attHd.getQualifiers() != null)
  {
   for(ClassReference qcr : attHd.getQualifiers())
   {
    AgeAttributeClass qClass = (AgeAttributeClass)  getPropertyClass(qcr, blkOwner, sm, implCust, log);
    
    if( qClass == null )
     return false;

    cEl = new ChainConverter.ChainElement();

    cEl.elClassRef = sm.getModelFactory().createAttributeClassRef(sm.getAgeAttributeClassPlug(qClass), qcr.getOrder(),
      attHd.getOriginalReference());

    cEl.elType = ChainConverter.ChainElement.ELTYPE.QUALIFIER;

    chain.add(cEl);
   }
  }

  if(attHd.getEmbeddedClassRef() != null)
   createEmbeddedObjectChain(blkOwner, attHd.getEmbeddedClassRef(), chain, sm, implCust, log);
 }


 private static abstract class ValueConverter
 {
  protected ClassReference colHdr;
  protected AttributedWritable lastProp;
  
  protected ValueConverter( ClassReference hd )
  {
   colHdr=hd;
  }

  abstract public AgePropertyClass getProperty();
//  abstract public AgeClassProperty getQualifiedProperty();

  public abstract void convert(AgeTabValue vls) throws ConvertionException;

  public abstract void resetObject( AgeObjectWritable obj );
  public void resetLine( int ln )
  {}
  
  public ClassReference getClassReference()
  {
   return colHdr;
  }
  
  protected void setLastConvertedValue( AttributedWritable p )
  {
   lastProp=p;
  }
  
  protected AttributedWritable getLastConvertedProperty()
  {
   return lastProp;
  }
  

  protected static AgeFileAttributeWritable convertFileValue(AgeTabValue atVal, AttributeClassRef classRef,  SyntaxProfileDefinition profDef, AttributedWritable prop)
  {
   if(atVal == null )
    return null;
   
   String val = null;
   ResolveScope scope = null;
   
   
   if( atVal.matchPrefix( profDef.getClusterResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getGlobalResolveScopePrefix() ) )
   {
    scope =  ResolveScope.GLOBAL;
   
    val = atVal.getValue().substring(profDef.getGlobalResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getClusterCascadeResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CASCADE_CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterCascadeResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getDefaultResolveScopePrefix() ) )
   {
    scope =  profDef.getDefaultFileAttributeResolveScope();
   
    val = atVal.getValue().substring(profDef.getDefaultResolveScopePrefix().length());
   }
   else
   {
    scope = profDef.getDefaultFileAttributeResolveScope();
    
    val = atVal.getValue();
   }

   if(val.length() == 0)
    return null;


   
   AgeFileAttributeWritable fAttr = (AgeFileAttributeWritable)prop.createAgeAttribute(classRef);
   fAttr.setValue(val);
   fAttr.setTargetResolveScope(scope);
   
   return fAttr;
  }
  
  protected static AgeAttributeWritable convertObjectValue(AgeTabValue atVal, AttributeClassRef classRef,
    SyntaxProfileDefinition profDef, AttributedWritable prop, Map<String,AgeObjectWritable> rangeObjects)
  {
   String val = null;
   ResolveScope scope = null;
   
   
   if( atVal.matchPrefix( profDef.getModuleResolveScopePrefix() ) )
   {
    scope =  ResolveScope.MODULE;
   
    val = atVal.getValue().substring(profDef.getModuleResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getClusterResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getGlobalResolveScopePrefix() ) )
   {
    scope =  ResolveScope.GLOBAL;
   
    val = atVal.getValue().substring(profDef.getGlobalResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getModuleCascadeResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CASCADE_MODULE;
   
    val = atVal.getValue().substring(profDef.getModuleCascadeResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getClusterCascadeResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CASCADE_CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterCascadeResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getDefaultResolveScopePrefix() ) )
   {
    scope =  profDef.getDefaultObjectAttributeResolveScope();
   
    val = atVal.getValue().substring(profDef.getDefaultResolveScopePrefix().length());
   }
   else
   {
    scope = profDef.getDefaultObjectAttributeResolveScope();
    
    val = atVal.getValue();
   }

   if(val.length() == 0)
    return null;

   AgeObjectWritable targetObj = null;

   if(rangeObjects != null)
    targetObj = rangeObjects.get(val);

   AgeAttributeWritable obAttr = null;
   if(targetObj == null)
   {
    obAttr = prop.createExternalObjectAttribute( classRef, val, scope );
   }
   else
   {
    obAttr = prop.createAgeAttribute(classRef);
    obAttr.setValue(targetObj);
   }
   
   return obAttr;

  }

 }
 
 private class DefinedRelationConvertor extends ValueConverter
 {
  private Collection<Map<String, AgeObjectWritable>> rangeObjects;
  private final AgeRelationClass relClass;
  private AgeObjectWritable hostObject;
  private final RelationClassRef rClsRef;
  
  public DefinedRelationConvertor(ClassReference hd, AgeRelationClass rlClass, ContextSemanticModel sm, Map<AgeClass, Map<String, AgeObjectWritable>> classMap)
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
    {
     for(AgeClass rgClass : rngSet )
     {
      if( me.getKey().isClassOrSubclass(rgClass))
      {
       rangeObjects.add(me.getValue());
       break;
      }
     }
    }
   }
   
   rClsRef = sm.getModelFactory().createRelationClassRef(sm.getAgeRelationClassPlug(rlClass), hd.isHorizontal()?hd.getCol():hd.getRow(), hd.getOriginalReference());
  }
  
  @Override
  public void resetObject( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getRelations() != null )
   {
    for( AgeRelationWritable r : obj.getRelations() )
     if( r.getAgeElClass() == relClass )
      obj.removeRelation(r);
   }
   
   setLastConvertedValue(null);

  }


  
  @Override
  public AgePropertyClass getProperty()
  {
   return relClass;
  }
 
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

  
  @Override
  public void convert( AgeTabValue atVal ) throws ConvertionException
  {
//   setLastConvertedProperty(null);

   if(atVal == null)
    return;

   int found = 0;

   atVal.trim();
   
   String val = null;
   ResolveScope scope = null;
   
   SyntaxProfileDefinition profDef = syntaxProfile.getClassSpecificSyntaxProfile(hostObject.getAgeElClass().getName());
   
   if( atVal.matchPrefix( profDef.getModuleResolveScopePrefix() ) )
   {
    scope =  ResolveScope.MODULE;
   
    val = atVal.getValue().substring(profDef.getModuleResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getClusterResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getGlobalResolveScopePrefix() ) )
   {
    scope =  ResolveScope.GLOBAL;
   
    val = atVal.getValue().substring(profDef.getGlobalResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getModuleCascadeResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CASCADE_MODULE;
   
    val = atVal.getValue().substring(profDef.getModuleCascadeResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getClusterCascadeResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CASCADE_CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterCascadeResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getDefaultResolveScopePrefix() ) )
   {
    scope =  profDef.getDefaultRelationResolveScope();
   
    val = atVal.getValue().substring(profDef.getDefaultResolveScopePrefix().length());
   }
   else
   {
    scope = profDef.getDefaultRelationResolveScope();
    
    val = atVal.getValue();
   }

   if(val.length() == 0)
    return;

   AgeObjectWritable targetObj = null;

   if( scope == ResolveScope.MODULE || scope == ResolveScope.CASCADE_MODULE )
   {
    for(Map<String, AgeObjectWritable> omap : rangeObjects)
    {
     AgeObjectWritable candObj = omap.get(val);
     
     if(candObj != null)
     {
      targetObj = candObj;
      found++;
     }
    }
   }
   


   if(found > 1)
    throw new ConvertionException(atVal.getRow(), atVal.getCol(), "Ambiguous reference");
   
   if( scope == ResolveScope.MODULE && targetObj == null )
    throw new ConvertionException(atVal.getRow(), atVal.getCol(), "Unresolved relation target");

   AgeRelationWritable rel = null;
   if(targetObj == null)
    rel = hostObject.createExternalRelation( rClsRef, val, scope );
   else
    rel = hostObject.createRelation( rClsRef, targetObj );

   setLastConvertedValue(rel);

  }
 }

 
 
 private class ObjectAttributeConvertor extends ValueConverter
 {
  private final Map<String, AgeObjectWritable> rangeObjects;
//  private AgeAttributeClass       attrClass;
  private AgeObjectWritable hostObject;
  private final AttributeClassRef classRef;
  
  public ObjectAttributeConvertor(ClassReference hd, AgeAttributeClass aCls, Map<String, AgeObjectWritable> map, ContextSemanticModel sm)
  {
   super(hd);
   
   AgeAttributeClassPlug cPlug = sm.getAgeAttributeClassPlug(aCls);
   classRef = sm.getModelFactory().createAttributeClassRef(cPlug, hd.getCol(), hd.getOriginalReference());
   
   rangeObjects = map;
  }

  @Override
  public void resetObject( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getAttributes() != null )
   {
    for( AgeAttributeWritable a : obj.getAttributes() )
     if( a.getAgeElClass() == classRef.getAttributeClass() )
      obj.removeAttribute(a);
   }

  }
  
  @Override
  public void convert(AgeTabValue atVal)
  {
//   setLastConvertedProperty(null);

   if(atVal == null )
    return;
   
   atVal.trim();
   
   SyntaxProfileDefinition profDef = syntaxProfile.getClassSpecificSyntaxProfile(hostObject.getAgeElClass().getName());
   
   AgeAttributeWritable obAttr = convertObjectValue(atVal, classRef, profDef, hostObject, rangeObjects);

   if( obAttr == null )
    return;
   
   setLastConvertedValue(obAttr);
  }
  
  @Override
  public AgePropertyClass getProperty()
  {
   return classRef.getAttributeClass();
  }
  
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

 }
 
 
 private class FileAttributeConvertor extends ValueConverter
 {
//  private AgeAttributeClass       attrClass;
  private AgeObjectWritable hostObject;
  private final AttributeClassRef classRef;
  
  public FileAttributeConvertor(ClassReference hd, AgeAttributeClass aCls, ContextSemanticModel sm)
  {
   super(hd);
   
   AgeAttributeClassPlug cPlug = sm.getAgeAttributeClassPlug(aCls);
   classRef = sm.getModelFactory().createAttributeClassRef(cPlug, hd.getCol(), hd.getOriginalReference());
  }

  @Override
  public void resetObject( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getAttributes() != null )
   {
    for( AgeAttributeWritable a : obj.getAttributes() )
     if( a.getAgeElClass() == classRef.getAttributeClass() )
      obj.removeAttribute(a);
   }

  }
  
  @Override
  public void convert(AgeTabValue atVal)
  {
   if(atVal == null )
    return;
   
   atVal.trim();
   
   String val = null;
   ResolveScope scope = null;
   
   SyntaxProfileDefinition profDef = syntaxProfile.getClassSpecificSyntaxProfile(hostObject.getAgeElClass().getName());
   
   if( atVal.matchPrefix( profDef.getClusterResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getGlobalResolveScopePrefix() ) )
   {
    scope =  ResolveScope.GLOBAL;
   
    val = atVal.getValue().substring(profDef.getGlobalResolveScopePrefix().length());
   }
   if( atVal.matchPrefix( profDef.getClusterCascadeResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CASCADE_CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterCascadeResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getDefaultResolveScopePrefix() ) )
   {
    scope =  profDef.getDefaultFileAttributeResolveScope();
   
    val = atVal.getValue().substring(profDef.getDefaultResolveScopePrefix().length());
   }
   else
   {
    scope = profDef.getDefaultFileAttributeResolveScope();
    
    val = atVal.getValue();
   }

   if(val.length() == 0)
    return;

   AgeFileAttributeWritable obAttr = convertFileValue(atVal, classRef, profDef, hostObject);

   if( obAttr == null )
    return;
   
   obAttr.setValue(val);
   obAttr.setTargetResolveScope(scope);
   
   setLastConvertedValue(obAttr);
  }
  
  @Override
  public AgePropertyClass getProperty()
  {
   return classRef.getAttributeClass();
  }
  
 }

 
 
 private class CustomRelationConvertor extends ValueConverter
 {
  private final Map<String, AgeObjectWritable> rangeObjects;
  private final AgeRelationClass       relClass;
  private AgeObjectWritable hostObject;
  private final RelationClassRef rClsRef;

  public CustomRelationConvertor(ClassReference hd, AgeRelationClass relCls, ContextSemanticModel sm, Map<String, AgeObjectWritable> map)
  {
   super(hd);
   rangeObjects = map;
   relClass = relCls;

   rClsRef = sm.getModelFactory().createRelationClassRef(sm.getAgeRelationClassPlug(relCls), hd.isHorizontal()?hd.getCol():hd.getRow(), hd.getOriginalReference());
  }

  @Override
  public void resetObject( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getRelations() != null )
   {
    for( AgeRelationWritable r : obj.getRelations() )
     if( r.getAgeElClass() == relClass )
      obj.removeRelation(r);
   }

  }
  
  
  @Override
  public void convert(AgeTabValue atVal) throws ConvertionException
  {
   if(atVal == null )
    return;
   
   String val = null;
   ResolveScope scope = null;
   
   SyntaxProfileDefinition profDef = syntaxProfile.getClassSpecificSyntaxProfile(hostObject.getAgeElClass().getName());
   
   if( atVal.matchPrefix( profDef.getModuleResolveScopePrefix() ) )
   {
    scope =  ResolveScope.MODULE;
   
    val = atVal.getValue().substring(profDef.getModuleResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getClusterResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getGlobalResolveScopePrefix() ) )
   {
    scope =  ResolveScope.GLOBAL;
   
    val = atVal.getValue().substring(profDef.getGlobalResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getModuleCascadeResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CASCADE_MODULE;
   
    val = atVal.getValue().substring(profDef.getModuleCascadeResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getClusterCascadeResolveScopePrefix() ) )
   {
    scope =  ResolveScope.CASCADE_CLUSTER;
   
    val = atVal.getValue().substring(profDef.getClusterCascadeResolveScopePrefix().length());
   }
   else if( atVal.matchPrefix( profDef.getDefaultResolveScopePrefix() ) )
   {
    scope =  profDef.getDefaultRelationResolveScope();
   
    val = atVal.getValue().substring(profDef.getDefaultResolveScopePrefix().length());
   }
   else
   {
    scope = profDef.getDefaultRelationResolveScope();
    
    val = atVal.getValue();
   }

   if(val.length() == 0)
    return;

   AgeObjectWritable targetObj = null;

   if(rangeObjects != null && (scope == ResolveScope.MODULE || scope == ResolveScope.CASCADE_MODULE) )
    targetObj = rangeObjects.get(val);

   if(scope == ResolveScope.MODULE && targetObj == null )
    throw new ConvertionException(atVal.getRow(), atVal.getCol(), "Unresolved relation target");
 
   AgeRelationWritable rel = null;
   if(targetObj == null)
    rel = hostObject.createExternalRelation(rClsRef, val, scope);
   else
    rel = hostObject.createRelation(rClsRef, targetObj);

   setLastConvertedValue(rel);
  }
  
  @Override
  public AgePropertyClass getProperty()
  {
   return relClass;
  }
  
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

 }
 
 
 private class EmptyColumnConvertor  extends ValueConverter
 {

  protected EmptyColumnConvertor( ClassReference cr )
  {
   super(cr);
  }

  @Override
  public void resetObject( AgeObjectWritable ob )
  {}
  
  @Override
  public void convert( AgeTabValue vls) throws ConvertionException
  {
   if(vls == null)
    return;

   if(vls.getValue().length() > 0)
    throw new ConvertionException(vls.getRow(), vls.getCol(), "Cells in the column with no header must be empty");
  }

  @Override
  public AgePropertyClass getProperty()
  {
   return null;
  }
  
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

 }
 
 
 private class InvalidColumnConvertor extends ValueConverter
 {

  public InvalidColumnConvertor( ClassReference hd )
  {
   super(hd);
  }

  
  @Override
  public AgePropertyClass getProperty()
  {
   return null;
  }

  @Override
  public void convert(AgeTabValue vls) throws ConvertionException
  {
  }


  @Override
  public void resetObject(AgeObjectWritable obj)
  {
  }
 }
 
 private class AttributeConvertor extends ValueConverter
 {
  private final AttributeClassRef classRef;
  private AgeObjectWritable hostObject;

  public AttributeConvertor(ClassReference hd, AgeAttributeClass attCls, ContextSemanticModel sm) // throws SemanticException
  {
   super( hd );
   
   AgeAttributeClassPlug cPlug = sm.getAgeAttributeClassPlug(attCls);
   classRef = sm.getModelFactory().createAttributeClassRef(cPlug, hd.getCol(), hd.getOriginalReference());
  }

  @Override
  public void resetObject( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   if( obj.getAttributes() != null )
   {
    for( AgeAttributeWritable a : obj.getAttributes() )
     if( a.getAgeElClass() == classRef.getAttributeClass() )
      obj.removeAttribute(a);
   }
   
   setLastConvertedValue(null);
  }
  
  @Override
  public void convert(AgeTabValue vl) throws ConvertionException
  {
//   setLastConvertedProperty(null);
   
   if( vl == null || vl.getValue().length() == 0 )
   {
    if( classRef.getAttributeClass().getDataType().isMultiline() )
    {
     AgeAttributeWritable attr = (AgeAttributeWritable)getLastConvertedProperty();
     
     if( attr != null )
     {
      try
      {
       attr.updateValue("");
      }
      catch(FormatException e)
      {}
     }
    }
    
    return;
   }
   
   AgeAttributeWritable attr = null;
   boolean exstAttr=false;
   
   if( classRef.getAttributeClass().getDataType().isMultiline() )
   {
    Collection<? extends AgeAttributeWritable> atcoll = hostObject.getAttributesByClass(classRef.getAttributeClass(), false);
    
    if( atcoll == null || atcoll.size() == 0 )
     attr = hostObject.createAgeAttribute(classRef);
    else
    {
     attr = atcoll.iterator().next();
     exstAttr=true;
    }
   }
   else
    attr = hostObject.createAgeAttribute(classRef);
   
 
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
     throw new ConvertionException(vl.getRow(), vl.getCol(), "Invalid value ("+vl.getValue()+") for attribute: "+classRef.getAttributeClass().getName() );
    }
   
   
    setLastConvertedValue(attr);
  }
  
  @Override
  public AgePropertyClass getProperty()
  {
   return classRef.getAttributeClass();
  }
  
//  public AgeClassProperty getQualifiedProperty()
//  {
//   return null;
//  }

 }
 
 private abstract class QualifierConvertor extends ValueConverter
 {
  protected AttributeClassRef classRef;
  private final ValueConverter hostConverter;
  protected AttributedWritable contextProperty;

  public QualifierConvertor(ClassReference attHd, AgeAttributeClass qClass, ValueConverter hc, ContextSemanticModel sm)// throws SemanticException
  {
   super(attHd);
   
   AgeAttributeClassPlug cPlug = sm.getAgeAttributeClassPlug(qClass);
   classRef = sm.getModelFactory().createAttributeClassRef(cPlug, attHd.getCol(), attHd.getOriginalReference());
   hostConverter = hc;
  }

  
  public ValueConverter getHostConvertor()
  {
   return hostConverter;
  }
  
  @Override
  public void resetObject( AgeObjectWritable obj )
  {
   List<AgeAttributeClass> chain = new ArrayList<AgeAttributeClass>(5);
   
   chain.add(classRef.getAttributeClass());
   
   ValueConverter cHost = getHostConvertor();
   
   while( cHost instanceof QualifierConvertor )
   {
    chain.add( (AgeAttributeClass)cHost.getProperty() );
    cHost = ((QualifierConvertor)cHost).getHostConvertor();
   }

   AgePropertyClass topProp = cHost.getProperty();
   
   if( topProp instanceof AgeRelationClass && obj.getRelations() != null )
   {
    for( AgeRelationWritable r : obj.getRelations() )
    {
     if( r.getAgeElClass() == topProp )
      removeQualifiers( r, chain, chain.size()-1 );
    }
   }
   
  }
  
  @Override
  public AgePropertyClass getProperty()
  {
   return classRef.getAttributeClass();
  }

  @Override
  protected AttributedWritable getLastConvertedProperty()
  {
   if( contextProperty == hostConverter.getLastConvertedProperty() )
    return super.getLastConvertedProperty();
   
   return null;
  }

  
  private void removeQualifiers( AttributedWritable host, List<AgeAttributeClass> chain, int lvl )
  {
   AgeAttributeClass lvlClass = chain.get(lvl);
   
   for( AgeAttributeWritable a : host.getAttributes() )
   {
    if( a.getAttributedClass() == lvlClass )
    {
     if( lvl == 0 )
      host.removeAttribute(a);
     else
      removeQualifiers(a, chain, lvl-1);
    }
   }
  }

 }
 
 private class ScalarQualifierConvertor extends QualifierConvertor
 {

  public ScalarQualifierConvertor(ClassReference attHd, AgeAttributeClass qClass, ValueConverter hc, ContextSemanticModel sm)// throws SemanticException
  {
   super(attHd, qClass, hc, sm);
  }

  @Override
  public void convert(AgeTabValue val) throws ConvertionException
  {

   if(val == null || val.getValue().length() == 0)
   {
    if( classRef.getAttributeClass().getDataType().isMultiline() && getLastConvertedProperty() != null )
    {
     try
     {
      ((AgeAttributeWritable)getLastConvertedProperty()).updateValue("");
     }
     catch(FormatException e)
     {}
    }
    
    return;
   }
   
   AttributedWritable prop = getHostConvertor().getLastConvertedProperty();

   // if there is no host value checking whether the qualifier is multiline
   if( prop == null )
    throw new ConvertionException(val.getRow(), val.getCol(), "There is no main value for qualification");

   AgeAttributeWritable attrAlt = null;

   if(classRef.getAttributeClass().getDataType().isMultiline())
    attrAlt = (AgeAttributeWritable)getLastConvertedProperty();
   
   if( attrAlt == null )
    attrAlt = prop.createAgeAttribute(classRef);

   try
   {
    attrAlt.updateValue(val.getValue());
   }
   catch(FormatException e)
   {
    throw new ConvertionException(val.getRow(), val.getCol(), "Invalid value (" + val.getValue() + ") for attribute: "
      + classRef.getAttributeClass().getName());
   }

   
   contextProperty=prop;
   setLastConvertedValue(attrAlt);
  }

 }
 
 private class ObjectQualifierConvertor extends QualifierConvertor
 {
  private final Map<String, AgeObjectWritable> rangeObjects;
  private final SyntaxProfileDefinition profDef;

  public ObjectQualifierConvertor(ClassReference attHd, AgeAttributeClass qClass, ValueConverter hc, Map<String, AgeObjectWritable> rangeMap,
    ContextSemanticModel sm, SyntaxProfileDefinition pd)
  {
   super(attHd, qClass, hc, sm);

   rangeObjects=rangeMap;
   profDef = pd;
  }
  
  
  @Override
  public void convert(AgeTabValue atVal) throws ConvertionException
  {
   if(atVal == null )
    return;
   
   AttributedWritable prop = getHostConvertor().getLastConvertedProperty();


   if(prop == null )
    throw new ConvertionException(atVal.getRow(), atVal.getCol(), "There is no main value for qualification");

   AgeAttributeWritable obAttr = convertObjectValue(atVal, classRef, profDef, prop, rangeObjects);
   
   if( obAttr == null )
    return;
   
   contextProperty = prop;
   setLastConvertedValue(obAttr);
  }
 }

 
 private  class FileQualifierConvertor extends QualifierConvertor
 {
  private final SyntaxProfileDefinition profDef;

  public FileQualifierConvertor(ClassReference attHd, AgeAttributeClass qClass, ValueConverter hc,
    ContextSemanticModel sm, SyntaxProfileDefinition pd)
  {
   super(attHd, qClass, hc, sm);

   profDef = pd;
  }
  
  
  @Override
  public void convert(AgeTabValue atVal) throws ConvertionException
  {
   if(atVal == null )
    return;
   
   AttributedWritable prop = getHostConvertor().getLastConvertedProperty();

   if(prop == null )
    throw new ConvertionException(atVal.getRow(), atVal.getCol(), "There is no main value for qualification");

   
   AgeFileAttributeWritable obAttr = convertFileValue(atVal, classRef, profDef, prop);
   
   if( obAttr == null )
    return;
   
   contextProperty = prop;
   setLastConvertedValue(obAttr);
  }
 }

 
 private static class ChainConverter extends ValueConverter
 {
  static class ChainElement
  {
   enum ELTYPE
   {
    ATTRIBUTE,
    QUALIFIER,
    RELATION
   }
   
   ELTYPE elType;
   Object elClassRef;
  }

  private final List<ChainElement> chain;
  private final int chainLength;
  private AgeObjectWritable hostObject;

  private int lineNum;
  
  private static int counter=1;

 
  protected ChainConverter( List<ChainElement> chn, ClassReference cref )
  {
   super(cref);

   chain = chn;
   chainLength = chain.size();
  }


  @Override
  public AgePropertyClass getProperty()
  {
   return ((AgeObjectProperty)chain.get(chain.size()-1).elClassRef).getAgeElClass();
  }

  @Override
  public void convert(AgeTabValue vls) throws ConvertionException
  {
   if( vls == null || vls.getValue().length() == 0 )
    return;

   AgeObjectPropertyWritable pathProp = null;
   AttributedWritable attrHost = null;
   
   int level=0;
   
   for( int i=0; i < chainLength; i++ )
   {
    ChainElement  ce = chain.get(i);
    
    if( pathProp == null )
     attrHost = hostObject;
    else
    {
     if( ce.elType == ChainElement.ELTYPE.QUALIFIER )
      attrHost = pathProp;
     else
     {
      attrHost = ((AgeObjectAttributeWritable)pathProp).getValue();
      level++;
     }
    }
    
    if( attrHost == null )
    {
     AgeObjectAttributeWritable objatt = (AgeObjectAttributeWritable)pathProp;
     ContextSemanticModel sm = hostObject.getSemanticModel();
     
     ClassRef clsRef = sm.getModelFactory().createClassRef( 
       sm.getAgeClassPlug(objatt.getAgeElClass().getTargetClass()),
       vls.getColumnHeader().getOrder(),
       vls.getColumnHeader().getOriginalReference(),
       vls.getColumnHeader().isHorizontal(), sm);

     
     AgeObjectWritable embObj = sm.createAgeObject(clsRef, "__emb_"+objatt.getAgeElClass().getTargetClass()+counter++);
     
     embObj.setIdScope(IdScope.MODULE);
     
     objatt.setValue(embObj);
     
     attrHost = embObj;
    }    

    if(level == 0 && chain.get(i+1).elType != ChainElement.ELTYPE.QUALIFIER )
    {
     Collection<? extends AgeAttributeWritable> oattrs =  attrHost.getAttributesByClass(((AttributeClassRef)ce.elClassRef).getAttributeClass(), false);
     
     if( oattrs == null || oattrs.size() <= lineNum )
      pathProp = null;
     else
     {
      Iterator<? extends AgeAttributeWritable> itr = oattrs.iterator();
      
      for( int j=0; j <=lineNum; j++ )
       pathProp = itr.next();
     }
    }
    else if( ce.elType == ChainElement.ELTYPE.RELATION )
     pathProp = ((AgeObjectWritable)attrHost).getRelation(((RelationClassRef)ce.elClassRef).getAgeRelationClass());
    else
     pathProp = attrHost.getAttribute(((AttributeClassRef)ce.elClassRef).getAttributeClass());

    if( pathAttr == null )
     pathAttr = attrHost.createAgeAttribute(ce.elClassRef);
   }
   
   AttributeClassRef lastCR = chain.get(chain.size()-1).elClassRef;
   
   if( pathAttr.getClassReference() != lastCR )
    pathAttr = attrHost.createAgeAttribute(lastCR);
  
   try
   {
    pathAttr.updateValue(vls.getValue());
   }
   catch(FormatException e)
   {
    throw new ConvertionException(vls.getRow(), vls.getCol(), "Invalid value ("+vls.getValue()+") for attribute: "+pathAttr.getAgeElClass().getName() );
   }
  }

  @Override
  public void resetObject( AgeObjectWritable obj )
  {
   hostObject = obj;
   
   setLastConvertedValue(null);
  }

  @Override
  public void resetLine( int ln )
  {
   lineNum = ln;
  }
 }
}
