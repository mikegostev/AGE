package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeAttributeClass;
import uk.ac.ebi.age.model.QualifierRule;
import uk.ac.ebi.age.model.RestrictionType;

public interface QualifierRuleWritable extends QualifierRule
{

 void setType(RestrictionType type);

 void setAttributeClass(AgeAttributeClass ageAttributeClass);

}
