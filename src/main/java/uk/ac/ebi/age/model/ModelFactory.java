package uk.ac.ebi.age.model;

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

public interface ModelFactory
{

 DataModuleWritable createDataModule(ContextSemanticModel sm);

 AgeClassWritable createAgeClass(String name, String id, String pfx, SemanticModel sm);

 AgeClassWritable createCustomAgeClass(String name, String pfx, ContextSemanticModel sm);

 AgeObjectWritable createAgeObject(ClassRef ageClassRef, String id);

 AgeRelationClassWritable createAgeRelationClass(String name, String id, SemanticModel sm);

 AgeRelationClassWritable createCustomAgeRelationClass(String name, ContextSemanticModel sm, AgeClass range,
   AgeClass owner);

 AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, SemanticModel sm);

 AgeAttributeClassWritable createCustomAgeAttributeClass(String name, DataType type, ContextSemanticModel sm,
   AgeClass owner);

 AgeExternalRelationWritable createExternalRelation(RelationClassRef ref, AgeObjectWritable sourceObj, String id,
   ResolveScope scope);

 AgeAttributeWritable createExternalObjectAttribute(AttributeClassRef atCls, AttributedWritable host, String id,
   ResolveScope scope);

 AgeAttributeWritable createAgeStringAttribute(AttributeClassRef attrClass, AttributedWritable host);

 AgeAttributeWritable createAgeIntegerAttribute(AttributeClassRef attrClass, AttributedWritable host);

 AgeAttributeWritable createAgeRealAttribute(AttributeClassRef attrClass, AttributedWritable host);

 AgeAttributeWritable createAgeBooleanAttribute(AttributeClassRef attrClass, AttributedWritable host);

 AgeAttributeWritable createAgeFileAttribute(AttributeClassRef attrClass, AttributedWritable host, ResolveScope scope);

 AgeAttributeWritable createAgeObjectAttribute(AttributeClassRef attrClass, AttributedWritable host);

 AgeRelationWritable createRelation(RelationClassRef relClassRef, AgeObjectWritable targetObj);
 AgeRelationWritable createInferredInverseRelation(AgeRelationWritable dirRel);

 AgeAttributeClassPlug createAgeAttributeClassPlug(AgeAttributeClass attrClass, ContextSemanticModel sm);

 AgeClassPlug createAgeClassPlug(AgeClass attrClass, ContextSemanticModel sm);

 AgeRelationClassPlug createAgeRelationClassPlug(AgeRelationClass attrClass, ContextSemanticModel sm);

 AgeRelationClassPlug createAgeRelationInverseClassPlug(AgeRelationClass cls, ContextSemanticModel sm);

 AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, SemanticModel semanticModelImpl);

 AgeAnnotationWritable createAgeAnnotation(AgeAnnotationClass cls, SemanticModel semanticModelImpl);

 AttributeAttachmentRuleWritable createAgeAttributeAttachmentRule(RestrictionType type, SemanticModel sm);

 RelationRuleWritable createAgeRelationRule(RestrictionType type, SemanticModel sm);

 QualifierRuleWritable createAgeQualifierRule(SemanticModel sm);

 ClassRef createClassRef(AgeClassPlug plug, int order, String heading, boolean hrz, ContextSemanticModel modl);

 AttributeClassRef createAttributeClassRef(AgeAttributeClassPlug plug, int order, String heading);

 RelationClassRef createRelationClassRef(AgeRelationClassPlug plug, int order, String heading);

 ContextSemanticModel createContextSemanticModel(SemanticModel mm);

 SemanticModel createModelInstance();

}