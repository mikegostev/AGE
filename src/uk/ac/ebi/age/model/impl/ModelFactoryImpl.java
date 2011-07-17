package uk.ac.ebi.age.model.impl;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAnnotationClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.AttributeClassRef;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelFactory;
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
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;

public class ModelFactoryImpl extends ModelFactory implements Serializable
{
 private static final long serialVersionUID = 1L;

 private static ModelFactoryImpl instance = new ModelFactoryImpl();

 private ModelFactoryImpl()
 {}
 
 public static ModelFactory getInstance()
 {
  return instance;
 }

 
 private ModelFactory v1factory = uk.ac.ebi.age.model.impl.v1.ModelFactoryImpl.getInstance();
 private ModelFactory v2factory = uk.ac.ebi.age.model.impl.v2.ModelFactoryImpl.getInstance();
 
 
 @Override
 public DataModuleWritable createDataModule( ContextSemanticModel sm )
 {
  return v2factory.createDataModule( sm );
 }

 @Override
 public AgeObjectWritable createAgeObject(String id, AgeClass ageClass, ContextSemanticModel sm)
 {
  return v2factory.createAgeObject(id, ageClass, sm);
 }

 @Override
 public AgeClassWritable createAgeClass(String name, String id, String pfx, SemanticModel sm)
 {
  return v1factory.createAgeClass(name, id, pfx, sm);
 }

 @Override
 public AgeRelationClassWritable createAgeRelationClass(String name, String id, SemanticModel sm)
 {
  return v1factory.createAgeRelationClass(name, id, sm);
 }

 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, SemanticModel sm)
 {
  return v1factory.createAgeAnnotationClass(name, id, sm);
 }

 
 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, SemanticModel sm)
 {
  return v1factory.createAgeAttributeClass(name, id, type, sm);
 }


 @Override
 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClass, ContextSemanticModel sm)
 {
  return v2factory.createAgeAttribute(attrClass, sm);
 }


 @Override
 public AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String id, AgeRelationClass targetClass, ContextSemanticModel sm)
 {
  return v2factory.createExternalRelation( sourceObj, id, targetClass, sm);
 }
 

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, String id, ContextSemanticModel sm)
 {
  return v2factory.createExternalObjectAttribute(atCls, id, sm);
 }

 @Override
 public AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass, ContextSemanticModel sm)
 {
  return v1factory.createRelation(targetObj, relClass, sm);
 }


 @Override
 public AgeAttributeClassWritable createCustomAgeAttributeClass(String name, DataType type, ContextSemanticModel sm, AgeClass owner)
 {
  return v1factory.createCustomAgeAttributeClass(name, type, sm, owner);
 }

 @Override
 public AgeClassWritable createCustomAgeClass(String name, String pfx, ContextSemanticModel sm)
 {
  return v1factory.createCustomAgeClass(name, pfx, sm);
 }

 @Override
 public AgeRelationClassWritable createCustomAgeRelationClass(String name, ContextSemanticModel sm, AgeClass range, AgeClass owner)
 {
  return v1factory.createCustomAgeRelationClass(name, sm, range, owner);
 }

 @Override
 public AgeAttributeClassPlug createAgeAttributeClassPlug(AgeAttributeClass attrClass, ContextSemanticModel sm)
 {
  return v1factory.createAgeAttributeClassPlug(attrClass, sm);
 }

 @Override
 public AgeClassPlug createAgeClassPlug(AgeClass cls, ContextSemanticModel sm)
 {
  return v1factory.createAgeClassPlug(cls, sm);
 }

 @Override
 public AgeRelationClassPlug createAgeRelationClassPlug(AgeRelationClass relClass, ContextSemanticModel sm)
 {
  return v1factory.createAgeRelationClassPlug(relClass, sm);
 }

 @Override
 public AgeRelationClassPlug createAgeRelationInverseClassPlug(AgeRelationClass relClass, ContextSemanticModel sm)
 {
  return v1factory.createAgeRelationInverseClassPlug(relClass, sm);
 }

 @Override
 public AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls, SemanticModel sm)
 {
  return v1factory.createAgeAnnotation(cls, sm);
 }

 @Override
 public AttributeAttachmentRuleWritable createAgeAttributeAttachmentRule(RestrictionType type, SemanticModel sm)
 {
  return v1factory.createAgeAttributeAttachmentRule(type,sm);
 }

 @Override
 public RelationRuleWritable createAgeRelationRule(RestrictionType type, SemanticModel sm)
 {
  return v1factory.createAgeRelationRule(type, sm);
 }

 @Override
 public QualifierRuleWritable createAgeQualifierRule(SemanticModel sm)
 {
  return v1factory.createAgeQualifierRule( sm );
 }

 @Override
 public AttributeClassRef createAttributeClassRef(AgeAttributeClassPlug plug, int order, String heading)
 {
  return v2factory.createAttributeClassRef(plug, order, heading);
 }


}
