package uk.ac.ebi.age.storage.impl.serswap.v3;

import uk.ac.ebi.age.model.AgeAnnotationClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeObject;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.RelationClassRef;
import uk.ac.ebi.age.model.RestrictionType;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAnnotationClassWritable;
import uk.ac.ebi.age.model.writable.AgeAnnotationWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.AttributeAttachmentRuleWritable;
import uk.ac.ebi.age.model.writable.AttributedWritable;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;

public class SwapModelFactory extends ModelFactory
{
 private ModelFactory baseFactory;

 public SwapModelFactory( ModelFactory fct )
 {
  baseFactory = fct;
 }
 
 public SwapDataModuleImpl createDataModule(ContextSemanticModel sm)
 {
  return new SwapDataModuleImpl(baseFactory.createDataModule(sm));
 }

 public AgeClassWritable createAgeClass(String name, String id, String pfx, SemanticModel sm)
 {
  return baseFactory.createAgeClass(name, id, pfx, sm);
 }

 public AgeClassWritable createCustomAgeClass(String name, String pfx, ContextSemanticModel sm)
 {
  return baseFactory.createCustomAgeClass(name, pfx, sm);
 }

 public AgeObjectWritable createAgeObject(ClassRef ageClassRef, String id)
 {
  return baseFactory.createAgeObject(ageClassRef, id);
 }

 public AgeRelationClassWritable createAgeRelationClass(String name, String id, SemanticModel sm)
 {
  return baseFactory.createAgeRelationClass(name, id, sm);
 }

 public AgeRelationClassWritable createCustomAgeRelationClass(String name, ContextSemanticModel sm, AgeClass range,
   AgeClass owner)
 {
  return baseFactory.createCustomAgeRelationClass(name, sm, range, owner);
 }

 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, SemanticModel sm)
 {
  return baseFactory.createAgeAttributeClass(name, id, type, sm);
 }

 public AgeAttributeClassWritable createCustomAgeAttributeClass(String name, DataType type, ContextSemanticModel sm,
   AgeClass owner)
 {
  return baseFactory.createCustomAgeAttributeClass(name, type, sm, owner);
 }

 public AgeExternalRelationWritable createExternalRelation(RelationClassRef ref, AgeObjectWritable sourceObj, String id, boolean glb )
 {
  return new SwapExternalRelation(ref, sourceObj, id, glb);
 }

 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, AttributedWritable host, String id, boolean glb )
 {
  return new SwapExternalObjectAttribute(atCls, id, host, glb);
 }

 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClassRef, AttributedWritable host)
 {
  if( attrClassRef.getAttributeClass().getDataType() == DataType.OBJECT )
   return new SwapObjectAttribute(attrClassRef, host);
  
  if( ! ( host instanceof AgeObject ) )
    return baseFactory.createAgeAttribute(attrClassRef, host);
  
  AgeAttributeWritable attr=null;
  
  switch( attrClassRef.getAttributeClass().getDataType() )
  {
   case INTEGER:
    attr = new SwapIntegerAttribute(attrClassRef, host);
    break;
   
   case REAL:
    attr = new SwapRealAttribute(attrClassRef, host);
    break;
   
   case BOOLEAN:
    attr = new SwapBooleanAttribute(attrClassRef, host);
    break;
   
   case URI:
   case TEXT: 
   case STRING:
   case GUESS:
    attr = new SwapStringAttribute(attrClassRef, host);
    break;

   case FILE:
    attr = new SwapFileAttribute(attrClassRef, host);
    break;
    
   case OBJECT: //See above
  }
  
  
  return attr;
  

 }

 public AgeRelationWritable createRelation(RelationClassRef relClassRef, AgeObjectWritable targetObj)
 {
  return new SwapRelation(relClassRef, targetObj);
  //baseFactory.createRelation(relClassRef, targetObj);
 }

 public AgeAttributeClassPlug createAgeAttributeClassPlug(AgeAttributeClass attrClass, ContextSemanticModel sm)
 {
  return baseFactory.createAgeAttributeClassPlug(attrClass, sm);
 }

 public AgeClassPlug createAgeClassPlug(AgeClass attrClass, ContextSemanticModel sm)
 {
  return baseFactory.createAgeClassPlug(attrClass, sm);
 }

 public AgeRelationClassPlug createAgeRelationClassPlug(AgeRelationClass attrClass, ContextSemanticModel sm)
 {
  return baseFactory.createAgeRelationClassPlug(attrClass, sm);
 }

 public AgeRelationClassPlug createAgeRelationInverseClassPlug(AgeRelationClass cls, ContextSemanticModel sm)
 {
  return baseFactory.createAgeRelationInverseClassPlug(cls, sm);
 }

 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, SemanticModel semanticModelImpl)
 {
  return baseFactory.createAgeAnnotationClass(name, id, semanticModelImpl);
 }

 public AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls, SemanticModel semanticModelImpl)
 {
  return baseFactory.createAgeAnnotation(cls, semanticModelImpl);
 }

 public AttributeAttachmentRuleWritable createAgeAttributeAttachmentRule(RestrictionType type, SemanticModel sm)
 {
  return baseFactory.createAgeAttributeAttachmentRule(type, sm);
 }

 public RelationRuleWritable createAgeRelationRule(RestrictionType type, SemanticModel sm)
 {
  return baseFactory.createAgeRelationRule(type, sm);
 }

 public QualifierRuleWritable createAgeQualifierRule(SemanticModel sm)
 {
  return baseFactory.createAgeQualifierRule(sm);
 }

 public ClassRef createClassRef(AgeClassPlug plug, int order, String heading, boolean hrz, ContextSemanticModel modl)
 {
  return baseFactory.createClassRef(plug, order, heading, hrz, modl);
 }

 public AttributeClassRef createAttributeClassRef(AgeAttributeClassPlug plug, int order, String heading)
 {
  return baseFactory.createAttributeClassRef(plug, order, heading);
 }

 public RelationClassRef createRelationClassRef(AgeRelationClassPlug plug, int order, String heading)
 {
  return baseFactory.createRelationClassRef(plug, order, heading);
 }

 public ContextSemanticModel createContextSemanticModel(SemanticModel mm)
 {
  return baseFactory.createContextSemanticModel(mm);
 }
}
