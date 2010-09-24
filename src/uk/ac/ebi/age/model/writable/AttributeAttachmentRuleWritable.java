package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.AttributeAttachmentRule;
import uk.ac.ebi.age.model.Cardinality;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.QualifiersCondition;
import uk.ac.ebi.age.model.RestrictionType;

public interface AttributeAttachmentRuleWritable extends AttributeAttachmentRule
{
 void setAttributeClass(AgeAttributeClass ageAttributeClass);

 void setCardinality(int cardinality);

 void setCardinalityType(Cardinality cardinalityType);

 void setSubclassesIncluded(boolean subclassesIncluded);

 void setQualifiersCondition(QualifiersCondition qualifiersCondition);

 void setQualifiersUnique(boolean qualifiersUnique);

 void setType(RestrictionType type);

 void setValueUnique(boolean valueUnique);

 void addQualifier(QualifierRule qrul);

}
