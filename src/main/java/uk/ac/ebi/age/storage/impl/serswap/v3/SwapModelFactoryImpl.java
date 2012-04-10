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
import uk.ac.ebi.age.model.ResolveScope;
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

public class SwapModelFactoryImpl implements SwapModelFactory
{
 private ModelFactory baseFactory;

 public SwapModelFactoryImpl( ModelFactory fct )
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

 @Override
 public AgeExternalRelationWritable createExternalRelation(RelationClassRef ref, AgeObjectWritable sourceObj, String id, ResolveScope scp )
 {
  return new SwapExternalRelation(ref, sourceObj, id, scp);
 }

 public AgeRelationWritable createRelation(RelationClassRef relClassRef, AgeObjectWritable sourceObj, AgeObjectWritable targetObj)
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

 @Override
 public SemanticModel createModelInstance()
 {
  SemanticModel mod = baseFactory.createModelInstance();
  
  mod.setModelFactory( this );
  
  return mod;
 }


 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, AttributedWritable host, String id,
   ResolveScope scope)
 {
  return new SwapExternalObjectAttribute(atCls, id, host, scope);
 }

 @Override
 public AgeAttributeWritable createAgeStringAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  if( host instanceof AgeObject )
   return new SwapStringAttribute(attrClass, host);
   
  return baseFactory.createAgeStringAttribute(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeIntegerAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  if( host instanceof AgeObject )
   return new SwapIntegerAttribute(attrClass, host);
   
  return baseFactory.createAgeIntegerAttribute(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeRealAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  if( host instanceof AgeObject )
   return new SwapRealAttribute(attrClass, host);
   
  return baseFactory.createAgeRealAttribute(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeBooleanAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  if( host instanceof AgeObject )
   return new SwapBooleanAttribute(attrClass, host);
   
  return baseFactory.createAgeBooleanAttribute(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeFileAttribute(AttributeClassRef attrClass, AttributedWritable host,
   ResolveScope scope)
 {
  if( host instanceof AgeObject )
   return new SwapFileAttribute(attrClass, host, scope);
   
  return baseFactory.createAgeFileAttribute(attrClass, host, scope);
 }

 @Override
 public AgeAttributeWritable createAgeObjectAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return new SwapObjectAttribute(attrClass, host);
 }

 @Override
 public AgeRelationWritable createInferredInverseRelation(AgeRelationWritable dirRel)
 {
  return baseFactory.createInferredInverseRelation(dirRel);
 }

 @Override
 public AgeExternalRelationWritable createDefinedInferredExternalInverseRelation(AgeObjectProxy tgObj,
   AgeObjectProxy src, AgeRelationClass cls)
 {
  return new SwapDefinedImplicitInvExtRelation(tgObj,src,cls);
 }

 @Override
 public AgeExternalRelationWritable createCustomInferredExternalInverseRelation(AgeObjectProxy tgObj,
   AgeObjectProxy src, String clsName)
 {
  return new SwapCustomImplicitInvExtRelation(tgObj,src,clsName);
 }
}
