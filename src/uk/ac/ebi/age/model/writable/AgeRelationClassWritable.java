package uk.ac.ebi.age.model.writable;

import uk.ac.ebi.age.model.AgeClass;
import uk.ac.ebi.age.model.AgeRelationClass;

public interface AgeRelationClassWritable extends AgeRelationClass
{
 void addSubClass(AgeRelationClass makeRelationsBranch);
 void addSuperClass(AgeRelationClass ageRelCls);

 void addDomainClass(AgeClass dmCls);

 void addRangeClass(AgeClass dmCls);

 void setInverseClass(AgeRelationClass ageEl);
 
 void setImplicit(boolean b);
 
}
