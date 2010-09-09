package uk.ac.ebi.age.model.impl.v1;

import java.io.Serializable;
import java.util.Collection;

import uk.ac.ebi.age.model.AgeAbstractClass;
import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AgeAttributeClassPlug;
import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeClassPlug;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.AgeRelationClassPlug;
import uk.ac.ebi.age.model.AgeRestriction;
import uk.ac.ebi.age.model.ContextSemanticModel;
import uk.ac.ebi.age.model.DataType;
import uk.ac.ebi.age.model.ModelFactory;
import uk.ac.ebi.age.model.SemanticModel;
import uk.ac.ebi.age.model.writable.AgeAnnotationClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;

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
 public SubmissionWritable createSubmission( ContextSemanticModel sm )
 {
  return new SubmissionImpl( sm );
 }

 @Override
 public AgeObjectWritable createAgeObject(String id, AgeClass ageClass, SemanticModel sm)
 {
  return new AgeObjectImpl(id, ageClass, sm);
 }

 @Override
 public AgeClassWritable createAgeClass(String name, String id, String pfx, SemanticModel sm)
 {
  return new AgeClassImpl(name, id, pfx, sm);
 }

 @Override
 public AgeRelationClassWritable createAgeRelationClass(String name, String id, SemanticModel sm)
 {
  return new AgeRelationClassImpl(name, id, sm);
 }

 @Override
 public AgeAnnotationClassWritable createAgeAnnotationClass(String name, String id, SemanticModel sm)
 {
  return new AgeAnnotationClassImpl(name, id, sm);
 }

 
 @Override
 public AgeAttributeClassWritable createAgeAttributeClass(String name, String id, DataType type, SemanticModel sm)
 {
  return new AgeAttributeClassImpl(name, id, type, sm);
 }


 @Override
 public AgeAttributeWritable createAgeAttribute(AgeAttributeClass attrClass, SemanticModel sm)
 {
  AgeAttributeWritable attr=null;
  
  switch( attrClass.getDataType() )
  {
   case INTEGER:
    attr = new AgeIntegerAttributeImpl(attrClass, sm);
    break;
   
   case REAL:
    attr = new AgeRealAttributeImpl(attrClass, sm);
    break;
   
   case BOOLEAN:
    attr = new AgeBooleanAttributeImpl(attrClass, sm);
    break;
   
   case URI:
   case TEXT: 
   case STRING:
   case GUESS:
    attr = new AgeStringAttributeImpl(attrClass, sm);
    break;
  }
  
  
  return attr;
 }

/* 
 @Override
 public AgeAttributeWritable createAgeAttribute(AgeObject obj, AgeAttributeClass attrClass, String prm, SemanticModel sm)
 {
  AgeAttributeWritable attr=null;
  
  switch( attrClass.getDataType() )
  {
   case INTEGER:
    attr = new AgeIntegerAttributeParamImpl(obj, attrClass, prm, sm);
    break;
   case REAL:
    attr = new AgeRealAttributeParamImpl(obj, attrClass, prm, sm);
    break;
   case BOOLEAN:
    attr = new AgeBooleanAttributeParamImpl(obj, attrClass, prm, sm);
    break;
   case STRING:
    attr = new AgeStringAttributeParamImpl(obj, attrClass, prm, sm);
    break;
  }
  
  
  return attr;
 }
*/

 @Override
 public AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String id, AgeRelationClass targetClass, SemanticModel sm)
 {
  return new AgeExternalRelationImpl(targetClass, sourceObj, id, sm);
 }

 @Override
 public AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass, SemanticModel semanticModel)
 {
  return new AgeRelationImpl(targetObj, relClass, semanticModel);
 }

 @Override
 public AgeRestriction createSomeValuesFromRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls)
 {
  return new SomeValuesFromRestrictionImpl(scls, fillerRestr, relcls);
 }

 @Override
 public AgeRestriction createAllValuesFromRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls)
 {
  return new AllValuesFromRestrictionImpl(scls, fillerRestr, relcls);
 }

 @Override
 public AgeRestriction createExactCardinalityRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls, int cardinatily)
 {
  return new ExactCardinalityRestrictionImpl(scls, fillerRestr, relcls, cardinatily);
 }

 @Override
 public AgeRestriction createMaxCardinalityRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls, int cardinatily)
 {
  return new MaxCardinalityRestrictionImpl(scls, fillerRestr, relcls, cardinatily);
 }

 @Override
 public AgeRestriction createMinCardinalityRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls, int cardinatily)
 {
  return new MinCardinalityRestrictionImpl(scls, fillerRestr, relcls, cardinatily);
 }

 @Override
 public AgeRestriction createAndLogicRestriction(Collection<AgeRestriction> operands)
 {
  return new AndLogicRestrictionImpl(operands);
 }

 @Override
 public AgeRestriction createOrLogicRestriction(Collection<AgeRestriction> operands)
 {
  return new OrLogicRestrictionImpl(operands);
 }

 @Override
 public AgeRestriction createIsClassRestriction(AgeClass srcClas, AgeAbstractClass tgtClass)
 {
  return new IsInstanceOfRestrictionImpl(srcClas, tgtClass);
 }

 @Override
 public AgeAttributeClass createCustomAgeAttributeClass(String name, DataType type, SemanticModel sm, AgeClass owner)
 {
  return new CustomAgeAttributeClassImpl(name, type, sm, owner);
 }

 @Override
 public AgeClass createCustomAgeClass(String name, String pfx, SemanticModel sm)
 {
  return new CustomAgeClassImpl(name, pfx, sm);
 }

 @Override
 public AgeRelationClass createCustomAgeRelationClass(String name, SemanticModel sm, AgeClass range, AgeClass owner)
 {
  return new CustomAgeRelationClassImpl(name, sm, range, owner);
 }

 @Override
 public AgeAttributeClassPlug createAgeAttributeClassPlug(AgeAttributeClass attrClass, SemanticModel sm)
 {
  return new AgeAttributeClassPlugPluggable(attrClass, sm);
 }

 @Override
 public AgeClassPlug createAgeClassPlug(AgeClass cls, SemanticModel mdl)
 {
  return new AgeClassPlugPluggable(cls, mdl);
 }

 @Override
 public AgeRelationClassPlug createAgeRelationClassPlug(AgeRelationClass relClass, SemanticModel mod)
 {
  return new AgeRelationClassPlugPluggable(relClass, mod);
 }

}
