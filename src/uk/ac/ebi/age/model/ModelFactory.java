package uk.ac.ebi.age.model;

import java.util.Collection;

import uk.ac.ebi.age.model.writable.AgeAttributeClassWritable;
import uk.ac.ebi.age.model.writable.AgeAttributeWritable;
import uk.ac.ebi.age.model.writable.AgeClassWritable;
import uk.ac.ebi.age.model.writable.AgeExternalRelationWritable;
import uk.ac.ebi.age.model.writable.AgeObjectWritable;
import uk.ac.ebi.age.model.writable.AgeRelationClassWritable;
import uk.ac.ebi.age.model.writable.AgeRelationWritable;
import uk.ac.ebi.age.model.writable.SubmissionWritable;

public abstract class ModelFactory
{
 public abstract SubmissionWritable createSubmission( ContextSemanticModel sm );

 public abstract AgeClassWritable createAgeClass(String name, String pfx, SemanticModel sm);
 public abstract AgeClass createCustomAgeClass(String name, String pfx, SemanticModel sm);

 public abstract AgeObjectWritable createAgeObject(String id, AgeClass ageClass, SemanticModel sm);

 public abstract AgeRelationClassWritable  createAgeRelationClass(String name, SemanticModel sm);
 public abstract AgeRelationClass createCustomAgeRelationClass(String name, SemanticModel sm, AgeClass range, AgeClass owner);

 public abstract AgeAttributeClassWritable  createAgeAttributeClass( String name, DataType type, SemanticModel sm );
 public abstract AgeAttributeClass createCustomAgeAttributeClass( String name, DataType type, SemanticModel sm, AgeClass owner );

 public abstract AgeExternalRelationWritable createExternalRelation(AgeObjectWritable sourceObj, String id, AgeRelationClass targetClass,  SemanticModel sm);

 public abstract AgeAttributeWritable createAgeAttribute(AgeObject obj, AgeAttributeClass attrClass, SemanticModel sm);

 public abstract AgeRelationWritable createRelation(AgeObjectWritable targetObj, AgeRelationClass relClass, SemanticModel semanticModel);

 public abstract AgeRestriction createSomeValuesFromRestriction(AgeClass scls, AgeRestriction fillerRestr, AgeRelationClass relcls);

 public abstract AgeRestriction createAllValuesFromRestriction(AgeClass srcClas, AgeRestriction fillerRestr, AgeRelationClass ageRelation);

 public abstract AgeRestriction createMaxCardinalityRestriction(AgeClass srcClas, AgeRestriction fillerRestr, AgeRelationClass ageRelation, int cardinatily);

 public abstract AgeRestriction createMinCardinalityRestriction(AgeClass srcClas, AgeRestriction fillerRestr, AgeRelationClass ageRelation, int cardinatily);

 public abstract AgeRestriction createExactCardinalityRestriction(AgeClass srcClas, AgeRestriction fillerRestr, AgeRelationClass ageRelation, int cardinatily);

 public abstract AgeRestriction createAndLogicRestriction(Collection<AgeRestriction> operands);

 public abstract AgeRestriction createOrLogicRestriction(Collection<AgeRestriction> operands);

 public abstract AgeRestriction createIsClassRestriction(AgeClass srcClas, AgeAbstractClass ageClass);
}
