package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;
import uk.ac.ebi.age.model.Cardinality;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.QualifiersCondition;
import uk.ac.ebi.age.model.RelationRule;
import uk.ac.ebi.age.model.RestrictionType;

public interface RelationRuleWritable extends RelationRule
{

 void setCardinality(int cardinality);

 void setCardinalityType(Cardinality cardinalityType);

 void setQualifiersUnique(boolean qualifiersUnique);

 void setRelationSubclassesIncluded(boolean relationSubclassesIncluded);

 void setType(RestrictionType type);

 void setSubclassesIncluded(boolean subclassesIncluded);

 void setQualifiersCondition(QualifiersCondition qualifiersCondition);

 void setRelationClass(AgeRelationClass ageRelationClass);

 void setTargetClass(AgeClass ageClass);

 void addQualifier(QualifierRule qrul);

}
