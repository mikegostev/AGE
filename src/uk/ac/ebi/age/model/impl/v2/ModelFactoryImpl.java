package uk.ac.ebi.age.model.impl.v2;

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

 
 @Override
 public DataModuleWritable createDataModule( ContextSemanticModel sm )
 {
  return new DataModuleImpl( sm );
 }

 @Override
 public AgeObjectWritable createAgeObject(String id, AgeClass ageClass, SemanticModel sm)
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
 public AgeAttributeWritable createAgeAttribute(AttributeClassRef attrClassRef, SemanticModel sm)
 {
  AgeAttributeWritable attr=null;
  
  switch( attrClassRef.getAttributeClass().getDataType() )
  {
   case INTEGER:
    attr = new AgeIntegerAttributeImpl(attrClassRef, sm);
    break;
   
   case REAL:
    attr = new AgeRealAttributeImpl(attrClassRef, sm);
    break;
   
   case BOOLEAN:
    attr = new AgeBooleanAttributeImpl(attrClassRef, sm);
    break;
   
   case URI:
   case TEXT: 
   case STRING:
   case GUESS:
    attr = new AgeStringAttributeImpl(attrClassRef, sm);
    break;
   
   case OBJECT:
    attr = new AgeObjectAttributeImpl(attrClassRef, sm);
  }
  
  
  return attr;
  
 }

 @Override
 public AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String id, AgeRelationClass targetClass, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }
 

 @Override
 public AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, String id, SemanticModel sm)
 {
  return new AgeExternalObjectAttributeImpl(atCls, id, sm);
 }

 @Override
 public AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass, SemanticModel semanticModel)
 {
  throw new UnsupportedOperationException();
 }


 @Override
 public AgeAttributeClassWritable createCustomAgeAttributeClass(String name, DataType type, SemanticModel sm, AgeClass owner)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeClassWritable createCustomAgeClass(String name, String pfx, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationClassWritable createCustomAgeRelationClass(String name, SemanticModel sm, AgeClass range, AgeClass owner)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeAttributeClassPlug createAgeAttributeClassPlug(AgeAttributeClass attrClass, SemanticModel sm)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeClassPlug createAgeClassPlug(AgeClass cls, SemanticModel mdl)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationClassPlug createAgeRelationClassPlug(AgeRelationClass relClass, SemanticModel mod)
 {
  throw new UnsupportedOperationException();
 }

 @Override
 public AgeRelationClassPlug createAgeRelationInverseClassPlug(AgeRelationClass relClass, SemanticModel mod)
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


}
