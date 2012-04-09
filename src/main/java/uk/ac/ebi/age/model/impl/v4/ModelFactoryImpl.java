package uk.ac.ebi.age.model.impl.v4;

import java.io.Serializable;

import uk.ac.ebi.age.model.AgeAnnotationClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
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
import uk.ac.ebi.age.model.writable.DataModuleWritable;
import uk.ac.ebi.age.model.writable.QualifierRuleWritable;
import uk.ac.ebi.age.model.writable.RelationRuleWritable;

public class ModelFactoryImpl implements Serializable, ModelFactory 
{
 private static final long serialVersionUID = 4L;

 private static ModelFactoryImpl instance;

 private ModelFactoryImpl()
 {}
 
 public static ModelFactory getInstance()
 {
  if( instance == null )
   instance = new ModelFactoryImpl();
  
  return instance;
 }

 
 @Override
 public DataModuleWritable createDataModule( ContextSemanticModel sm )
 {
  return new DataModuleImpl( sm );
 }

 @Override
 public AgeObjectWritable createAgeObject(ClassRef ageClassRef, String id )
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public AgeClassWritable createAgeClass(String name, String id, String pfx, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationClassWritable createAgeRelationClass(String name, String id, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 
 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public AgeExternalRelationWritable createExternalRelation(RelationClassRef ref, AgeObjectWritable sourceObj, String id, ResolveScope global )
 {
  return new AgeExternalRelationImpl(ref, sourceObj, id, global);
 }
 

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, AttributedWritable host , String id, ResolveScope global )
 {
  return new AgeExternalObjectAttributeImpl(atCls, id, host, global);
 }

 @Override
 public AgeRelationWritable createRelation(RelationClassRef rClsR, AgeObjectWritable targetObj)
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public AgeAttributeClassWritable createCustomAgeAttributeClass(String name, DataType type, ContextSemanticModel sm, AgeClass owner)
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public AgeClassWritable createCustomAgeClass(String name, String pfx, ContextSemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationClassWritable createCustomAgeRelationClass(String name, ContextSemanticModel sm, AgeClass range, AgeClass owner)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeClassPlug createAgeAttributeClassPlug(AgeAttributeClass attrClass, ContextSemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeClassPlug createAgeClassPlug(AgeClass cls, ContextSemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationClassPlug createAgeRelationClassPlug(AgeRelationClass relClass, ContextSemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationClassPlug createAgeRelationInverseClassPlug(AgeRelationClass relClass, ContextSemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AttributeAttachmentRuleWritable createAgeAttributeAttachmentRule(RestrictionType type, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public RelationRuleWritable createAgeRelationRule(RestrictionType type, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public QualifierRuleWritable createAgeQualifierRule(SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AttributeClassRef createAttributeClassRef(AgeAttributeClassPlug plug, int order, String heading)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public ClassRef createClassRef(AgeClassPlug plug, int order, String heading, boolean hrz, ContextSemanticModel modl )
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public RelationClassRef createRelationClassRef(AgeRelationClassPlug plug, int order, String heading)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public ContextSemanticModel createContextSemanticModel(SemanticModel mm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public SemanticModel createModelInstance()
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeWritable createAgeStringAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeWritable createAgeIntegerAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeWritable createAgeRealAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeWritable createAgeBooleanAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeWritable createAgeFileAttribute(AttributeClassRef attrClass, AttributedWritable host,
   ResolveScope scope)
 {
  return new AgeFileAttributeImpl(attrClass, host, scope);
 }

 @Override
 public AgeAttributeWritable createAgeObjectAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationWritable createInferredInverseRelation(AgeRelationWritable dirRel)
 {
  throw new UnsupportedOperationException();
 }


}
