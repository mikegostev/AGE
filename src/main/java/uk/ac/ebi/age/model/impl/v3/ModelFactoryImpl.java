package uk.ac.ebi.age.model.impl.v3;

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

public class ModelFactoryImpl extends ModelFactory implements Serializable
{
 private static final long serialVersionUID = 3L;

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
  return new AgeObjectImpl(ageClassRef, id);
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
 public AgeExternalRelationWritable createExternalRelation(RelationClassRef ref, AgeObjectWritable sourceObj, String id, ResolveScope scope )
 {
  throw new UnsupportedOperationException();
 }
 

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, AttributedWritable host , String id, ResolveScope scope )
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationWritable createRelation(RelationClassRef rClsR, AgeObjectWritable targetObj)
 {
  return new AgeRelationImpl(rClsR, targetObj);
 }


 @Override
 public AgeAttributeClassWritable createCustomAgeAttributeClass(String name, DataType type, ContextSemanticModel sm, AgeClass owner)
 {
  if( type == DataType.OBJECT )
   return new CustomObjectAgeAttributeClassImpl(name, sm, owner);
  
  return new CustomAgeAttributeClassImpl(name, type, sm, owner);
 }


 @Override
 public AgeClassWritable createCustomAgeClass(String name, String pfx, ContextSemanticModel sm)
 {
  return new CustomAgeClassImpl(name, pfx, sm);
 }

 @Override
 public AgeRelationClassWritable createCustomAgeRelationClass(String name, ContextSemanticModel sm, AgeClass range, AgeClass owner)
 {
  return new CustomAgeRelationClassImpl(name, sm, range, owner);
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
  return new AttrClassRef(plug, order, heading);
 }

 @Override
 public ClassRef createClassRef(AgeClassPlug plug, int order, String heading, boolean hrz, ContextSemanticModel modl )
 {
  return new uk.ac.ebi.age.model.impl.v3.ClassRef(plug, order, heading, hrz, modl );
 }

 @Override
 public RelationClassRef createRelationClassRef(AgeRelationClassPlug plug, int order, String heading)
 {
  return new RelClassRef(plug, order, heading);
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
  return new AgeStringAttributeImpl(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeIntegerAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return new AgeIntegerAttributeImpl(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeRealAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return new AgeRealAttributeImpl(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeBooleanAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return new AgeBooleanAttributeImpl(attrClass, host);
 }

 @Override
 public AgeAttributeWritable createAgeFileAttribute(AttributeClassRef attrClass, AttributedWritable host, ResolveScope scope)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeWritable createAgeObjectAttribute(AttributeClassRef attrClass, AttributedWritable host)
 {
  return new AgeObjectAttributeImpl(attrClass, host);
 }


}
